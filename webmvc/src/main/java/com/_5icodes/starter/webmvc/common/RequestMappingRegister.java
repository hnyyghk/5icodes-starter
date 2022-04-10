package com._5icodes.starter.webmvc.common;

import com._5icodes.starter.monitor.meta.AbstractMetaInfoProvider;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class RequestMappingRegister extends AbstractMetaInfoProvider {
    private final Map<Method, String> keyMap = new HashMap<>();
    private final List<String> interfaces = new ArrayList<>();

    @Override
    public void doOnApplicationEvent(ApplicationStartedEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        Map<String, HandlerMapping> matchingBeans =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
        if (CollectionUtils.isEmpty(matchingBeans)) {
            return;
        }
        matchingBeans.forEach((name, mapping) -> {
            if (!(mapping instanceof RequestMappingInfoHandlerMapping)) {
                return;
            }
            RequestMappingInfoHandlerMapping handlerMapping = (RequestMappingInfoHandlerMapping) mapping;
            handlerMapping.getHandlerMethods().forEach((info, method) -> {
                PathPatternsRequestCondition pathPatternsCondition = info.getPathPatternsCondition();
                String path = String.join(",", pathPatternsCondition.getPatternValues());
                if (StringUtils.isBlank(path)) {
                    path = "/";
                }
                if (shouldApplySentinel(path)) {
                    RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
                    Set<RequestMethod> methods = methodsCondition.getMethods();
                    int methodBit = 0;
                    for (RequestMethod requestMethod : methods) {
                        methodBit = methodBit | (1 << requestMethod.ordinal());
                    }
                    String item = path + ":" + methodBit;
                    keyMap.put(method.getMethod(), item);
                    interfaces.add(item);
                    log.info("find sentinel key of request mapping: {}", item);
                }
            });
        });
        super.doOnApplicationEvent(event);
    }

    @Override
    protected Map<String, Object> doGetMetaInfo(ApplicationStartedEvent event) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(1);
        result.put("interfaces", interfaces);
        return result;
    }

    private boolean shouldApplySentinel(String path) {
        return !"/error".equals(path);
    }

    public String getSentinelKey(HandlerMethod handlerMethod) {
        return keyMap.get(handlerMethod.getMethod());
    }
}