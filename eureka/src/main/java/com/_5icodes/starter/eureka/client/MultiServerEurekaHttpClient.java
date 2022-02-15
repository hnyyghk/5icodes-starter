/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com._5icodes.starter.eureka.client;

import com._5icodes.starter.common.utils.SpringUtils;
import com.google.common.collect.Lists;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.resolver.ClusterResolver;
import com.netflix.discovery.shared.resolver.EurekaEndpoint;
import com.netflix.discovery.shared.resolver.aws.AwsEndpoint;
import com.netflix.discovery.shared.transport.*;
import com.netflix.discovery.shared.transport.decorator.EurekaHttpClientDecorator;
import com.netflix.discovery.shared.transport.decorator.ServerStatusEvaluator;
import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.monitor.Monitors;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.netflix.discovery.EurekaClientNames.METRIC_TRANSPORT_PREFIX;

/**
 * {@link MultiServerEurekaHttpClient} retries failed requests on subsequent servers in the cluster.
 * It maintains also simple quarantine list, so operations are not retried again on servers
 * that are not reachable at the moment.
 * <h3>Quarantine</h3>
 * All the servers to which communication failed are put on the quarantine list. First successful execution
 * clears this list, which makes those server eligible for serving future requests.
 * The list is also cleared once all available servers are exhausted.
 * <h3>5xx</h3>
 * If 5xx status code is returned, {@link ServerStatusEvaluator} predicate evaluates if the retries should be
 * retried on another server, or the response with this status code returned to the client.
 *
 * @author Tomasz Bak
 * @author Li gang
 * @see com.netflix.discovery.shared.transport.decorator.RetryableEurekaHttpClient
 */
public class MultiServerEurekaHttpClient extends EurekaHttpClientDecorator {

    private static final Logger logger = LoggerFactory.getLogger(MultiServerEurekaHttpClient.class);

    public static final int DEFAULT_NUMBER_OF_RETRIES = 3;

    private final String name;
    private final EurekaTransportConfig transportConfig;
    private final ClusterResolver clusterResolver;
    private final TransportClientFactory clientFactory;
    private final ServerStatusEvaluator serverStatusEvaluator;
    private final int numberOfRetries;

//    private final AtomicReference<EurekaHttpClient> delegate = new AtomicReference<>();
    private final Map<String, AtomicReference<EurekaHttpClient>> delegateMap = new ConcurrentHashMap<>();

//    private final Set<EurekaEndpoint> quarantineSet = new ConcurrentSkipListSet<>();
    private final Map<String, Set<EurekaEndpoint>> quarantineSetMap = new ConcurrentHashMap<>();

    private static final Field instanceInfoMetadataField;

    static {
        try {
            instanceInfoMetadataField = InstanceInfo.class.getDeclaredField("metadata");
            instanceInfoMetadataField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public MultiServerEurekaHttpClient(String name,
                                       EurekaTransportConfig transportConfig,
                                       ClusterResolver clusterResolver,
                                       TransportClientFactory clientFactory,
                                       ServerStatusEvaluator serverStatusEvaluator,
                                       int numberOfRetries) {
        this.name = name;
        this.transportConfig = transportConfig;
        this.clusterResolver = clusterResolver;
        this.clientFactory = clientFactory;
        this.serverStatusEvaluator = serverStatusEvaluator;
        this.numberOfRetries = numberOfRetries;
        Monitors.registerObject(name, this);
    }

    @Override
    public void shutdown() {
        for (Map.Entry<String, AtomicReference<EurekaHttpClient>> entry : delegateMap.entrySet()) {
            AtomicReference<EurekaHttpClient> delegate = entry.getValue();
            TransportUtils.shutdown(delegate.get());
        }
        if(Monitors.isObjectRegistered(name, this)) {
            Monitors.unregisterObject(name, this);
        }
    }

    @Override
    protected <R> EurekaHttpResponse<R> execute(RequestExecutor<R> requestExecutor) {
        String zone = SpringUtils.getBean(EurekaInstanceConfig.class).getMetadataMap().getOrDefault("zone", EurekaClientConfigBean.DEFAULT_ZONE);
        return execute(requestExecutor, zone);
    }

    private <R> EurekaHttpResponse<R> execute(RequestExecutor<R> requestExecutor, String zone) {
        List<EurekaEndpoint> candidateHosts = null;
        int endpointIdx = 0;
        AtomicReference<EurekaHttpClient> delegate = delegateMap.computeIfAbsent(zone, s -> new AtomicReference<>());
        Set<EurekaEndpoint> quarantineSet = quarantineSetMap.computeIfAbsent(zone, s -> new ConcurrentSkipListSet<>());
        for (int retry = 0; retry < numberOfRetries; retry++) {
            EurekaHttpClient currentHttpClient = delegate.get();
            EurekaEndpoint currentEndpoint = null;
            if (currentHttpClient == null) {
                if (candidateHosts == null) {
                    candidateHosts = getHostCandidates(zone, quarantineSet);
                    if (candidateHosts.isEmpty()) {
                        throw new TransportException("There is no known eureka server; cluster server list is empty");
                    }
                }
                if (endpointIdx >= candidateHosts.size()) {
                    throw new TransportException("Cannot execute request on any known server");
                }

                currentEndpoint = candidateHosts.get(endpointIdx++);
                currentHttpClient = clientFactory.newClient(currentEndpoint);
            }

            try {
                EurekaHttpResponse<R> response = requestExecutor.execute(currentHttpClient);
                if (serverStatusEvaluator.accept(response.getStatusCode(), requestExecutor.getRequestType())) {
                    delegate.set(currentHttpClient);
                    if (retry > 0) {
                        logger.info("Request execution succeeded on retry #{}", retry);
                    }
                    return response;
                }
                logger.warn("Request execution failure with status code {}; retrying on another server if available", response.getStatusCode());
            } catch (Exception e) {
                logger.warn("Request execution failed with message: {}", e.getMessage());  // just log message as the underlying client should log the stacktrace
            }

            // Connection error or 5xx from the server that must be retried on another server
            delegate.compareAndSet(currentHttpClient, null);
            if (currentEndpoint != null) {
                quarantineSet.add(currentEndpoint);
            }
        }
        throw new TransportException("Retry limit reached; giving up on completing the request");
    }

    @Override
    public EurekaHttpResponse<Applications> getApplications(String... regions) {
        RequestExecutor<Applications> requestExecutor = new RequestExecutor<Applications>() {
            @Override
            public EurekaHttpResponse<Applications> execute(EurekaHttpClient delegate) {
                return delegate.getApplications(regions);
            }

            @Override
            public RequestType getRequestType() {
                return RequestType.GetApplications;
            }
        };
        return getEurekaHttpResponse(requestExecutor);
    }

    @Override
    public EurekaHttpResponse<Applications> getDelta(String... regions) {
        RequestExecutor<Applications> requestExecutor = new RequestExecutor<Applications>() {
            @Override
            public EurekaHttpResponse<Applications> execute(EurekaHttpClient delegate) {
                return delegate.getDelta(regions);
            }

            @Override
            public RequestType getRequestType() {
                return RequestType.GetDelta;
            }
        };
        return getEurekaHttpResponse(requestExecutor);
    }

    @SneakyThrows
    private EurekaHttpResponse<Applications> getEurekaHttpResponse(RequestExecutor<Applications> requestExecutor) {
        String zone = SpringUtils.getBean(EurekaInstanceConfig.class).getMetadataMap().getOrDefault("zone", EurekaClientConfigBean.DEFAULT_ZONE);
        CompletableFuture<Applications> future = newFuture(requestExecutor, zone);
        Map<String, String> serviceUrl = SpringUtils.getBean(EurekaClientConfigBean.class).getServiceUrl();
        for (Map.Entry<String, String> entry : serviceUrl.entrySet()) {
            String key = entry.getKey();
            if (!key.equals(zone) && !key.equals(EurekaClientConfigBean.DEFAULT_ZONE)) {
                future = future.thenCombineAsync(newFuture(requestExecutor, key), (app1, app2) -> combine(Lists.newArrayList(app1, app2)));
            }
        }
        Applications applications = future.get();
        logger.debug("aggregate result of {}", requestExecutor.getRequestType().name());
        debugApplications(applications);
        return EurekaHttpResponse.anEurekaHttpResponse(200, applications).build();
    }

    private Applications combine(List<Applications> origins) {
        Applications applications = new Applications();
        if (CollectionUtils.isEmpty(origins)) {
            return applications;
        }
        Map<String, List<InstanceInfo>> map = origins.stream()
                .map(Applications::getRegisteredApplications)
                .flatMap(List::stream)
                .flatMap(application -> application.getInstances().stream())
                .collect(Collectors.groupingBy(InstanceInfo::getAppName));
        for (Map.Entry<String, List<InstanceInfo>> entry : map.entrySet()) {
            Application application = new Application(entry.getKey(), entry.getValue());
            applications.addApplication(application);
        }
        TreeMap<String, AtomicInteger> treeMap = new TreeMap<>();
        long maxVersion = 0L;
        for (Applications app : origins) {
            String appsHashCode = app.getAppsHashCode();
            if (ObjectUtils.isEmpty(appsHashCode)) {
                continue;
            }
            maxVersion = Math.max(maxVersion, app.getVersion());
            String[] split = appsHashCode.split("(?<=\\d)_");
            for (String s : split) {
                String[] s1 = s.split("_");
                if (ArrayUtils.getLength(s1) == 2) {
                    AtomicInteger atomicInteger = treeMap.computeIfAbsent(s1[0], k -> new AtomicInteger(0));
                    atomicInteger.getAndAdd(Integer.parseInt(s1[1]));
                }
            }
        }
        applications.setAppsHashCode(Applications.getReconcileHashCode(treeMap));
        applications.setVersion(maxVersion);
        return applications;
    }

    private CompletableFuture<Applications> newFuture(RequestExecutor<Applications> requestExecutor, String zone) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                EurekaHttpResponse<Applications> response = execute(requestExecutor, zone);
                Applications applications = response.getEntity();
                logger.debug("{} from zone: {}", requestExecutor.getRequestType().name(), zone);
                debugApplications(applications);
                overrideDefaultZone(applications, zone);
                return applications;
            } catch (Exception e) {
                logger.warn("{} from zone: {} error", requestExecutor.getRequestType().name(), zone, e);
                return new Applications();
            }
        });
    }

    @SneakyThrows
    private void overrideDefaultZone(Applications applications, String zone) {
        if (applications == null) {
            return;
        }
        List<Application> registeredApplications = applications.getRegisteredApplications();
        for (Application registeredApplication : registeredApplications) {
            List<InstanceInfo> instances = registeredApplication.getInstances();
            if (CollectionUtils.isEmpty(instances)) {
                continue;
            }
            for (InstanceInfo instance : instances) {
                Map<String, String> metadata = instance.getMetadata();
                String originZone = metadata.get("zone");
                if (originZone == null) {
                    if (Collections.emptyMap().equals(metadata)) {
                        metadata = new ConcurrentHashMap<>();
                        instanceInfoMetadataField.set(instance, metadata);
                    }
                    metadata.put("zone", zone);
                    logger.debug("override appName: {}, host: {}, port: {} to zone: {}", instance.getAppName(),
                            instance.getHostName(), instance.getPort(), zone);
                }
            }
        }
    }

    private void debugApplications(Applications applications) {
        if (applications == null) {
            logger.debug("applications is null");
            return;
        }
        logger.debug("applications appsHashCode: {}, version: {}", applications.getAppsHashCode(), applications.getVersion());
        List<Application> registeredApplications = applications.getRegisteredApplications();
        for (Application registeredApplication : registeredApplications) {
            logger.debug("registeredApplication: {}", registeredApplication);
        }
    }

    public static EurekaHttpClientFactory createFactory(final String name,
                                                        final EurekaTransportConfig transportConfig,
                                                        final ClusterResolver<EurekaEndpoint> clusterResolver,
                                                        final TransportClientFactory delegateFactory,
                                                        final ServerStatusEvaluator serverStatusEvaluator) {
        return new EurekaHttpClientFactory() {
            @Override
            public EurekaHttpClient newClient() {
                return new MultiServerEurekaHttpClient(name, transportConfig, clusterResolver, delegateFactory,
                        serverStatusEvaluator, DEFAULT_NUMBER_OF_RETRIES);
            }

            @Override
            public void shutdown() {
                delegateFactory.shutdown();
            }
        };
    }

    private List<EurekaEndpoint> getHostCandidates(String zone, Set<EurekaEndpoint> quarantineSet) {
//        List<EurekaEndpoint> candidateHosts = clusterResolver.getClusterEndpoints();
        EurekaClientConfigBean clientConfig = SpringUtils.getBean(EurekaClientConfigBean.class);
        List<String> eurekaServerServiceUrls = clientConfig.getEurekaServerServiceUrls(zone);
        List<EurekaEndpoint> candidateHosts = new LinkedList<>();
        for (String eurekaServerServiceUrl : eurekaServerServiceUrls) {
            candidateHosts.add(new AwsEndpoint(eurekaServerServiceUrl, clientConfig.getRegion(), zone));
        }
        quarantineSet.retainAll(candidateHosts);

        // If enough hosts are bad, we have no choice but start over again
        int threshold = (int) (candidateHosts.size() * transportConfig.getRetryableClientQuarantineRefreshPercentage());
        //Prevent threshold is too large
        if (threshold > candidateHosts.size()) {
            threshold = candidateHosts.size();
        }
        if (quarantineSet.isEmpty()) {
            // no-op
        } else if (quarantineSet.size() >= threshold) {
            logger.debug("Clearing quarantined list of size {}", quarantineSet.size());
            quarantineSet.clear();
        } else {
            List<EurekaEndpoint> remainingHosts = new ArrayList<>(candidateHosts.size());
            for (EurekaEndpoint endpoint : candidateHosts) {
                if (!quarantineSet.contains(endpoint)) {
                    remainingHosts.add(endpoint);
                }
            }
            candidateHosts = remainingHosts;
        }

        return candidateHosts;
    }

    @Monitor(name = METRIC_TRANSPORT_PREFIX + "quarantineSize",
            description = "number of servers quarantined", type = DataSourceType.GAUGE)
    public long getQuarantineSetSize() {
        return quarantineSetMap.values().stream().mapToInt(Set::size).sum();
    }
}
