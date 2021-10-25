package com._5icodes.starter.gray.rule;

import com._5icodes.starter.apollo.utils.ApolloUtils;
import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.gray.exception.ParseException;
import com._5icodes.starter.gray.parser.JsonParser;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ApolloRuleStrategyHolder implements RuleStrategyHolder, ConfigChangeListener, ApplicationContextAware, InitializingBean {
    private Environment environment;
    private final ConcurrentHashMap<String, RuleStrategy> appStrategyMap = new ConcurrentHashMap<>();
    private volatile RuleStrategy globalRuleStrategy;
    private final RuleStrategy defaultStrategy = DefaultRuleStrategy.INSTANCE;
    private final Set<String> apps;
    private final JsonParser<RuleStrategy> ruleStrategyJsonParser;
    private final static String PREFIX = "strategy-";
    private final static String GLOBAL_KEY = "global";

    public ApolloRuleStrategyHolder(Set<String> apps, JsonParser<RuleStrategy> ruleStrategyJsonParser) {
        this.apps = apps;
        this.ruleStrategyJsonParser = ruleStrategyJsonParser;
    }

    public ApolloRuleStrategyHolder(JsonParser<RuleStrategy> ruleStrategyJsonParser) {
        this(Collections.emptySet(), ruleStrategyJsonParser);
    }

    private String formatAppName(String appName) {
        return appName.toLowerCase();
    }

    @Override
    public RuleStrategy get(String serviceId) {
        if (globalRuleStrategy != null) {
            return globalRuleStrategy;
        }
        String app = formatAppName(serviceId);
        RuleStrategy ruleStrategy = appStrategyMap.get(app);
        if (ruleStrategy == null) {
            ruleStrategy = initIfAbsent(app);
        }
        return ruleStrategy;
    }

    private RuleStrategy initIfAbsent(String app) {
        RuleStrategy ruleStrategy = wrapIfNull(parseFromEnvIgnoreError(app));
        log.info("ruleStrategy of {} is initialized to: {}", app, ruleStrategy);
        appStrategyMap.put(app, ruleStrategy);
        return ruleStrategy;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        environment = applicationContext.getEnvironment();
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        String namespace = changeEvent.getNamespace();
        Set<String> namespaces = ApolloUtils.preLoadPublicNamespaces();
        if (!namespaces.contains(namespace)) {
            return;
        }
        Set<String> keys = changeEvent.changedKeys();
        for (String key : keys) {
            if (!key.startsWith(PREFIX)) {
                continue;
            }
            String app = key.substring(PREFIX.length());
            if (shouldSkip(app)) {
                continue;
            }
            try {
                RuleStrategy ruleStrategy = parseRuleStrategy(app, changeEvent.getChange(key).getNewValue());
                if (app.equals(GLOBAL_KEY)) {
                    globalRuleStrategy = ruleStrategy;
                    log.info("global ruleStrategy is updated to {}, appStrategyMap will be cleared", ruleStrategy);
                } else {
                    ruleStrategy = wrapIfNull(ruleStrategy);
                    appStrategyMap.put(app, ruleStrategy);
                    log.info("ruleStrategy of {} is updated to {}", app, ruleStrategy);
                }
            } catch (Exception e) {
                log.error("parse ruleStrategy failed", e);
            }
        }
    }

    private boolean shouldSkip(String app) {
        if (app.equals(GLOBAL_KEY)) {
            return false;
        }
        if (CollectionUtils.isEmpty(apps)) {
            return false;
        }
        return !apps.contains(app);
    }

    @Override
    public void afterPropertiesSet() {
        globalRuleStrategy = parseFromEnvIgnoreError(GLOBAL_KEY);
    }

    private RuleStrategy parseFromEnvIgnoreError(String key) {
        try {
            return parseRuleStrategy(key, environment.getProperty(PREFIX + key));
        } catch (Exception e) {
            log.error("parse ruleStrategy failed", e);
        }
        return null;
    }

    private RuleStrategy wrapIfNull(RuleStrategy ruleStrategy) {
        return ruleStrategy == null ? defaultStrategy : ruleStrategy;
    }

    private RuleStrategy parseRuleStrategy(String app, String value) throws ParseException {
        log.info("parse ruleStrategy {} = {}", app, value);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            Map<String, String> map = JsonUtils.parseToMap(value, String.class, String.class);
            return ruleStrategyJsonParser.parse(map);
        } catch (Exception e) {
            throw new ParseException(String.format("parse ruleStrategy %s failed", value), e);
        }
    }
}