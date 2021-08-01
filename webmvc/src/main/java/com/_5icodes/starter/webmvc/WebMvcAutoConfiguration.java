package com._5icodes.starter.webmvc;

import com._5icodes.starter.webmvc.advice.CodeMsgResponseBodyAdvice;
import com._5icodes.starter.webmvc.advice.GlobalControllerAdvice;
import com._5icodes.starter.webmvc.advice.HandlerExceptionResolverEditor;
import com._5icodes.starter.webmvc.auth.PermissionInterceptor;
import com._5icodes.starter.webmvc.auth.condition.ConditionalOnAuth;
import com._5icodes.starter.webmvc.auth.feign.FeignAuthInterceptor;
import com._5icodes.starter.webmvc.condition.ConditionalOnAutoWrap;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.servlet.Filter;
import java.util.Set;

@Configuration
@EnableConfigurationProperties({WebMvcProperties.class, SuccessProperties.class, ErrorProperties.class})
public class WebMvcAutoConfiguration {
    @Configuration
    @ConditionalOnAuth
    @ConditionalOnClass(Filter.class)
    public static class PermissionConfig {
        @Bean
        public PermissionInterceptor permissionInterceptor() {
            return new PermissionInterceptor();
        }
    }

    @Configuration
    @ConditionalOnAutoWrap
    public static class AdviceAutoConfiguration {
        @Bean
        public GlobalControllerAdvice globalControllerAdvice() {
            return new GlobalControllerAdvice();
        }

        @Bean
        public CodeMsgResponseBodyAdvice codeMsgResponseBodyAdvice(WebMvcProperties webMvcProperties) {
            CodeMsgResponseBodyAdvice codeMsgResponseBodyAdvice = new CodeMsgResponseBodyAdvice();
            Set<String> excludeClasses = webMvcProperties.getAutoWrapExcludeClasses();
            if (!CollectionUtils.isEmpty(excludeClasses)) {
                codeMsgResponseBodyAdvice.addExcludeClasses(excludeClasses);
            }
            return codeMsgResponseBodyAdvice;
        }

        @Bean
        public HandlerExceptionResolverEditor handlerExceptionResolverEditor() {
            return new HandlerExceptionResolverEditor();
        }
    }

    @Configuration
    @ConditionalOnClass(Feign.class)
    public static class FeignAuthConfiguration {
        @Bean
        public FeignAuthInterceptor feignAuthInterceptor(WebMvcProperties webMvcProperties) {
            return new FeignAuthInterceptor(webMvcProperties);
        }
    }
}