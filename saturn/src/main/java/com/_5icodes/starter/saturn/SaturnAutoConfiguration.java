package com._5icodes.starter.saturn;

import com.vip.saturn.embed.spring.EmbeddedSpringSaturnApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SaturnProperties.class)
@ConditionalOnExpression("'true'.equals('${saturn.enabled:true}')")
public class SaturnAutoConfiguration {
    @Bean
    public EmbeddedSpringSaturnApplication embeddedSpringSaturnApplication() {
        EmbeddedSpringSaturnApplication embeddedSpringSaturnApplication = new EmbeddedSpringSaturnApplication();
        embeddedSpringSaturnApplication.setIgnoreExceptions(true);
        return embeddedSpringSaturnApplication;
    }
}