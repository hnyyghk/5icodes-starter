package com._5icodes.starter.web;

import com._5icodes.starter.web.advice.CodeMsgResponseBodyAdvice;
import com._5icodes.starter.web.advice.GlobalControllerAdvice;
import com._5icodes.starter.web.advice.HandlerExceptionResolverEditor;
import com._5icodes.starter.web.condition.ConditionalOnAutoWrap;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Configuration
@EnableConfigurationProperties({WebProperties.class, SuccessProperties.class, ErrorProperties.class})
public class WebAutoConfiguration {
    @Configuration
    @ConditionalOnAutoWrap
    public static class AdviceAutoConfiguration {
        @Bean
        public GlobalControllerAdvice globalControllerAdvice() {
            return new GlobalControllerAdvice();
        }

        @Bean
        public CodeMsgResponseBodyAdvice codeMsgResponseBodyAdvice(WebProperties webProperties) {
            CodeMsgResponseBodyAdvice codeMsgResponseBodyAdvice = new CodeMsgResponseBodyAdvice();
            List<String> excludeClasses = webProperties.getAutoWrapExcludeClasses();
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
}