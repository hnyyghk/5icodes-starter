package com._5icodes.starter.webmvc;

import com._5icodes.starter.feign.FeignAutoConfiguration;
import com._5icodes.starter.webmvc.log.LogFilter;
import com._5icodes.starter.webmvc.result.CodeMsgResponseBodyAdvice;
import com._5icodes.starter.webmvc.result.GlobalControllerAdvice;
import com._5icodes.starter.webmvc.advice.HandlerExceptionResolverEditor;
import com._5icodes.starter.webmvc.auth.PermissionInterceptor;
import com._5icodes.starter.webmvc.auth.condition.ConditionalOnAuth;
import com._5icodes.starter.webmvc.auth.feign.FeignAuthInterceptor;
import com._5icodes.starter.webmvc.common.RequestMappingRegister;
import com._5icodes.starter.webmvc.condition.ConditionalOnAutoWrap;
import com._5icodes.starter.webmvc.feign.CodeMsgDecodeAnnotationConfigHolder;
import feign.Feign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.servlet.Filter;
import java.util.Set;

@Configuration
@EnableConfigurationProperties({WebMvcProperties.class, SuccessProperties.class, ErrorProperties.class})
//@AutoConfigureBefore({ErrorMvcAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class})
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

    @Bean
    public RequestMappingRegister requestMappingRegister() {
        return new RequestMappingRegister();
    }

    @Bean
    public ErrorController errorController(ErrorAttributes errorAttributes,
                                           ErrorProperties errorProperties,
                                           @Value("${server.error.path:${error:path:/error}}") String errorPath) {
        return new com._5icodes.starter.webmvc.result.ErrorController(errorAttributes, errorProperties, errorPath);
    }

    @Configuration
    @ConditionalOnClass(FeignAutoConfiguration.class)
    @AutoConfigureBefore(FeignAutoConfiguration.class)
    public static class FeignWebExceptionConfiguration {
        @Bean
        public CodeMsgDecodeAnnotationConfigHolder codeMsgDecodeAnnotationConfigHolder() {
            return new CodeMsgDecodeAnnotationConfigHolder();
        }
    }

    @Bean
    public LogFilter logFilter() {
        return new LogFilter();
    }
}