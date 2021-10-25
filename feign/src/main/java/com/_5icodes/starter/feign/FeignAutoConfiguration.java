package com._5icodes.starter.feign;

import com._5icodes.starter.feign.auth.BasicAuthParameterProcessor;
import com._5icodes.starter.feign.auth.BasicAuthProperties;
import com._5icodes.starter.feign.auth.BasicAuthAnnotationConfigHolder;
import com._5icodes.starter.feign.client.CustomHttpClient;
import com._5icodes.starter.feign.client.LocalFeignClientPostProcessor;
import com._5icodes.starter.feign.contract.FeignContractAnnotationConfigHolder;
import com._5icodes.starter.feign.custom.AnnotatedMethodProcessor;
import com._5icodes.starter.feign.custom.CustomContract;
import com._5icodes.starter.feign.custom.FeignClientBeanProcessor;
import com._5icodes.starter.feign.decoder.ShowBodyErrorDecoder;
import com._5icodes.starter.feign.encoder.CustomEncoderContainer;
import com._5icodes.starter.feign.encoder.CustomSpringEncoder;
import feign.Client;
import feign.Contract;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.optionals.OptionalDecoder;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientConnectionManagerFactory;
import org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
import org.springframework.cloud.openfeign.loadbalancer.FeignLoadBalancerAutoConfiguration;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@AutoConfigureBefore(org.springframework.cloud.openfeign.FeignAutoConfiguration.class)
@EnableAspectJAutoProxy
@EnableConfigurationProperties(BasicAuthProperties.class)
public class FeignAutoConfiguration {
    @Bean
    public static FeignClientBeanProcessor proxyFeignClientBeanProcessor(@Autowired(required = false) List<AnnotationConfigHolder> annotationConfigHolders) {
        return new FeignClientBeanProcessor(annotationConfigHolders);
    }

    @Profile(value = {"local", "it"})
    @Bean
    public static LocalFeignClientPostProcessor localFeignClientPostProcessor() {
        return new LocalFeignClientPostProcessor();
    }

    @Bean
    public BasicAuthAnnotationConfigHolder basicAuthAnnotationConfigHolder() {
        return new BasicAuthAnnotationConfigHolder();
    }

    @Bean
    public FeignContractAnnotationConfigHolder feignContractAnnotationConfigHolder() {
        return new FeignContractAnnotationConfigHolder();
    }

    @Bean
    @ConditionalOnMissingBean
    public Client feignClient(BlockingLoadBalancerClient loadBalancerClient, HttpClient httpClient) {
        CustomHttpClient delegate = new CustomHttpClient(httpClient);
        return new FeignBlockingLoadBalancerClient(delegate, loadBalancerClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(messageConverters)));
    }

    @Bean
    @ConditionalOnMissingBean
    public Encoder feignEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new CustomEncoderContainer(new CustomSpringEncoder(messageConverters));
    }

    @Bean
    @ConditionalOnMissingBean(ErrorDecoder.class)
    public ErrorDecoder showBodyErrorDecoder() {
        return new ShowBodyErrorDecoder();
    }

    @Bean
    public BasicAuthParameterProcessor basicAuthParameterProcessor(BasicAuthProperties basicAuthProperties) {
        return new BasicAuthParameterProcessor(basicAuthProperties.getUserName(), basicAuthProperties.getPassword());
    }

    @Bean
    @ConditionalOnMissingBean
    public Contract customContract(ObjectProvider<List<AnnotatedParameterProcessor>> parameterProcessors,
                                   ObjectProvider<List<FeignFormatterRegistrar>> feignFormatterRegistrars,
                                   ObjectProvider<List<AnnotatedMethodProcessor>> annotatedMethodProcessors) {
        FormattingConversionService conversionService = new DefaultFormattingConversionService();
        feignFormatterRegistrars.ifAvailable(formatterRegistrars -> {
            formatterRegistrars.forEach(registrar -> {
                registrar.registerFormatters(conversionService);
            });
        });
        return new CustomContract(parameterProcessors.getIfAvailable(ArrayList::new), conversionService, annotatedMethodProcessors);
    }

    /**
     * @see org.springframework.cloud.openfeign.clientconfig.HttpClientFeignConfiguration
     */
    @Configuration
    @AutoConfigureBefore(FeignLoadBalancerAutoConfiguration.class)
    @ConditionalOnMissingBean(CloseableHttpClient.class)
    @EnableConfigurationProperties(CustomFeignHttpClientProperties.class)
    public static class HttpClientFeignConfiguration {
        private final ScheduledExecutorService connectionManagerTimer = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("httpclient-close-expireConnections-%d").daemon(true).build());

        private CloseableHttpClient httpClient;

        @Autowired(required = false)
        private RegistryBuilder registryBuilder;

        @Bean
        @ConditionalOnMissingBean(HttpClientConnectionManager.class)
        public HttpClientConnectionManager connectionManager(ApacheHttpClientConnectionManagerFactory connectionManagerFactory,
                                                             CustomFeignHttpClientProperties httpClientProperties) {
            final HttpClientConnectionManager connectionManager = connectionManagerFactory.newConnectionManager(
                    httpClientProperties.isDisableSslValidation(),
                    httpClientProperties.getMaxConnections(),
                    httpClientProperties.getMaxConnectionsPerRoute(),
                    httpClientProperties.getTimeToLive(),
                    httpClientProperties.getTimeToLiveUnit(),
                    registryBuilder);
            this.connectionManagerTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    connectionManager.closeExpiredConnections();
                }
            }, 30000, httpClientProperties.getConnectionTimerRepeat(), TimeUnit.MILLISECONDS);
            return connectionManager;
        }

        @Bean
        @ConditionalOnProperty(value = "feign.compression.response.enabled", havingValue = "true")
        public CloseableHttpClient customHttpClient(HttpClientConnectionManager httpClientConnectionManager,
                                                    CustomFeignHttpClientProperties httpClientProperties) {
            HttpClientBuilder builder = HttpClientBuilder.create().disableCookieManagement().useSystemProperties();
            this.httpClient = createClient(builder, httpClientConnectionManager, httpClientProperties);
            return this.httpClient;
        }

        @Bean
        @ConditionalOnProperty(value = "feign.compression.response.enabled", havingValue = "false", matchIfMissing = true)
        public CloseableHttpClient httpClient(HttpClientConnectionManager httpClientConnectionManager,
                                              CustomFeignHttpClientProperties httpClientProperties) {
            HttpClientBuilder builder = HttpClientBuilder.create().disableContentCompression().disableCookieManagement().useSystemProperties();
            this.httpClient = createClient(builder, httpClientConnectionManager, httpClientProperties);
            return this.httpClient;
        }

        private CloseableHttpClient createClient(HttpClientBuilder builder,
                                                 HttpClientConnectionManager httpClientConnectionManager,
                                                 CustomFeignHttpClientProperties httpClientProperties) {
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setConnectTimeout(httpClientProperties.getConnectionTimeout())
                    .setRedirectsEnabled(httpClientProperties.isFollowRedirects())
                    .setConnectionRequestTimeout(httpClientProperties.getConnectionRequestTimeout())
                    .build();
            return builder.setDefaultRequestConfig(defaultRequestConfig)
                    .setConnectionManager(httpClientConnectionManager).build();
        }

        @PreDestroy
        public void destroy() throws Exception {
            connectionManagerTimer.shutdown();
            if (null != httpClient) {
                httpClient.close();
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate(CloseableHttpClient closeableHttpClient, RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.requestFactory(
                () -> {
                    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                    requestFactory.setHttpClient(closeableHttpClient);
                    return requestFactory;
                }
        ).build();
    }
}