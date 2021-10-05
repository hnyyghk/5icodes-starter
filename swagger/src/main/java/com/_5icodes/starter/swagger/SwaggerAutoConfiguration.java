package com._5icodes.starter.swagger;

import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.webmvc.condition.ConditionalOnAutoWrap;
import com._5icodes.starter.webmvc.result.CodeMsgResponseBodyAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ConditionalOnClass(EnableSwagger2.class)
@Profile({"local", "stg"})
public class SwaggerAutoConfiguration {
    @Configuration
    @EnableSwagger2
    public static class SwaggerLocalConfiguration {
        @Bean
        public Docket docket() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .select()
                    .apis(input -> RequestHandlerSelectors.withClassAnnotation(Controller.class).apply(input)
                            || RequestHandlerSelectors.withClassAnnotation(RestController.class).apply(input))
                    .paths(PathSelectors.any())
                    .build();
        }

        @ConditionalOnAutoWrap
        @Bean
        public CodeMsgSwaggerSwitcher swaggerSwitcher(CodeMsgResponseBodyAdvice advice) {
            return new CodeMsgSwaggerSwitcher(advice);
        }

        private ApiInfo apiInfo() {
            return new ApiInfoBuilder().title(SpringApplicationUtils.getApplicationName() + " module api-docs")
                    .version("V1.0")
                    .build();
        }
    }
}