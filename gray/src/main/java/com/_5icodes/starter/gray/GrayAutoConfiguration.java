package com._5icodes.starter.gray;

import com._5icodes.starter.apollo.ApolloAutoConfiguration;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import com._5icodes.starter.gray.config.ConfigurableFactory;
import com._5icodes.starter.gray.request.RequestPredicate;
import com._5icodes.starter.gray.request.RequestPredicateFactory;
import com._5icodes.starter.gray.request.RequestPredicateJsonParser;
import com._5icodes.starter.gray.request.predicate.VersionCompareRequestPredicateFactory;
import com._5icodes.starter.gray.request.predicate.WhiteListRequestPredicateFactory;
import com._5icodes.starter.gray.rule.*;
import com._5icodes.starter.gray.rule.strategy.SimpleMappingRuleStrategyFactory;
import com._5icodes.starter.gray.rule.strategy.SimpleWeightRuleStrategyFactory;
import com._5icodes.starter.gray.server.ServerPredicate;
import com._5icodes.starter.gray.server.ServerPredicateFactory;
import com._5icodes.starter.gray.server.ServerPredicateJsonParser;
import com._5icodes.starter.gray.server.predicate.ExcludeServerPredicateFactory;
import com._5icodes.starter.gray.server.predicate.IncludeServerPredicateFactory;
import com._5icodes.starter.gray.weight.ServerWeightLoadBalance;
import com._5icodes.starter.gray.weight.ServerWeightLoadBalanceFactory;
import com._5icodes.starter.gray.weight.ServerWeightLoadBalanceJsonParser;
import com._5icodes.starter.gray.weight.server.CommonServerWeightLoadBalanceFactory;
import com._5icodes.starter.gray.weight.server.SingleServerMetaServerWeightLoadBalanceFactory;
import feign.Feign;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration(proxyBeanMethods = false)
@LoadBalancerClients(defaultConfiguration = StrategyLoadBalanceConfiguration.class)
@EnableConfigurationProperties(GrayProperties.class)
//@AutoConfigureAfter(EurekaClientAutoConfiguration.class)
@AutoConfigureBefore(value = {LoadBalancerAutoConfiguration.class, ApolloAutoConfiguration.class})
public class GrayAutoConfiguration {
    @Configuration
    @ConditionalOnClass(Feign.class)
    @AutoConfigureAfter(FeignAutoConfiguration.class)
    public static class FeignClientAwareServiceStrategyHolderConfiguration {
        @Bean
        @SneakyThrows
        public RuleStrategyHolder feignClientAwareDynamicStrategyHolder(FeignContext feignContext, RuleStrategyJsonParser ruleStrategyJsonParser) {
            Set<String> contextNames = feignContext.getContextNames();
            try {
                Class<?> traceFeignContextClass = ClassUtils.forName("org.springframework.cloud.sleuth.instrument.web.client.feign.TraceFeignContext", ClassUtils.getDefaultClassLoader());
                if (traceFeignContextClass.isAssignableFrom(feignContext.getClass())) {
                    Field field = traceFeignContextClass.getDeclaredField("delegate");
                    field.setAccessible(true);
                    contextNames = ((FeignContext) field.get(feignContext)).getContextNames();
                }
            } catch (Exception ignored) {
            }
            return new ApolloRuleStrategyHolder(contextNames, ruleStrategyJsonParser);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleStrategyHolder ruleStrategyHolder(RuleStrategyJsonParser ruleStrategyJsonParser) {
        return new ApolloRuleStrategyHolder(ruleStrategyJsonParser);
    }

    @Bean
    public VersionCompareRequestPredicateFactory versionCompareRequestPredicateFactory() {
        return new VersionCompareRequestPredicateFactory();
    }

    @Bean
    public WhiteListRequestPredicateFactory whiteListRequestPredicateFactory() {
        return new WhiteListRequestPredicateFactory();
    }

    @Bean
    public RequestPredicateJsonParser requestPredicateJsonParser(List<RequestPredicateFactory> requestPredicateFactories) {
        List<ConfigurableFactory<?, RequestPredicate>> delegates = new ArrayList<>();
        for (RequestPredicateFactory factory : requestPredicateFactories) {
            delegates.add(factory);
        }
        return new RequestPredicateJsonParser(delegates);
    }

    @Bean
    public ExcludeServerPredicateFactory excludeServerPredicateFactory() {
        return new ExcludeServerPredicateFactory();
    }

    @Bean
    public IncludeServerPredicateFactory includeServerPredicateFactory() {
        return new IncludeServerPredicateFactory();
    }

    @Bean
    public ServerPredicateJsonParser serverPredicateJsonParser(List<ServerPredicateFactory> serverPredicateFactories) {
        List<ConfigurableFactory<?, ServerPredicate>> delegates = new ArrayList<>();
        for (ServerPredicateFactory factory : serverPredicateFactories) {
            delegates.add(factory);
        }
        return new ServerPredicateJsonParser(delegates);
    }

    @Bean
    public CommonServerWeightLoadBalanceFactory commonServerWeightLoadBalanceFactory(ServerPredicateJsonParser serverPredicateJsonParser) {
        return new CommonServerWeightLoadBalanceFactory(serverPredicateJsonParser);
    }

    @Bean
    public ServerWeightLoadBalanceJsonParser serverWeightLoadBalanceJsonParser(List<ServerWeightLoadBalanceFactory> serverWeightLoadBalanceFactories) {
        List<ConfigurableFactory<?, ServerWeightLoadBalance>> delegates = new ArrayList<>();
        for (ServerWeightLoadBalanceFactory factory : serverWeightLoadBalanceFactories) {
            delegates.add(factory);
        }
        return new ServerWeightLoadBalanceJsonParser(delegates);
    }

    @Bean
    public SimpleMappingRuleStrategyFactory simpleMappingRuleStrategyFactory(RequestPredicateJsonParser requestPredicateJsonParser, ServerPredicateJsonParser serverPredicateJsonParser) {
        return new SimpleMappingRuleStrategyFactory(requestPredicateJsonParser, serverPredicateJsonParser);
    }

    @Bean
    public SimpleWeightRuleStrategyFactory simpleWeightRuleStrategyFactory(ServerWeightLoadBalanceJsonParser serverWeightLoadBalanceJsonParser) {
        return new SimpleWeightRuleStrategyFactory(serverWeightLoadBalanceJsonParser);
    }

    @Bean
    public SingleServerMetaServerWeightLoadBalanceFactory singleServerMetaServerWeightLoadBalanceFactory() {
        return new SingleServerMetaServerWeightLoadBalanceFactory();
    }

    @Bean
    public RuleStrategyJsonParser ruleStrategyJsonParser(List<RuleStrategyFactory> ruleStrategyFactories) {
        List<ConfigurableFactory<?, RuleStrategy>> delegates = new ArrayList<>();
        for (RuleStrategyFactory factory : ruleStrategyFactories) {
            delegates.add(factory);
        }
        return new RuleStrategyJsonParser(delegates);
    }

    @Bean
    public RegionZoneMetaProvider regionZoneMetaProvider() {
        return new RegionZoneMetaProvider();
    }
}