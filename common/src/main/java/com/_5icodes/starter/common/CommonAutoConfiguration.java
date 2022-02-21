package com._5icodes.starter.common;

import brave.Tracing;
import com._5icodes.starter.common.condition.EnabledConditionOutcomeCacheCleaner;
import com._5icodes.starter.common.exception.CodeMsgEnumProcessor;
import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import com._5icodes.starter.common.utils.TraceUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class CommonAutoConfiguration {
    @Bean
    public TraceUtils traceUtils(Tracing tracing) {
        return new TraceUtils(tracing);
    }

    @Bean
    public CachingMetadataReaderFactoryProvider cachingMetadataReaderFactoryProvider(ApplicationContext context) throws Exception {
        return new CachingMetadataReaderFactoryProvider(context);
    }

    @Bean
    public CodeMsgEnumProcessor codeMsgEnumProcessor(CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider) {
        return new CodeMsgEnumProcessor(metadataReaderFactoryProvider);
    }

    @Bean
    public EnabledConditionOutcomeCacheCleaner enabledConditionOutcomeCacheCleaner() {
        return new EnabledConditionOutcomeCacheCleaner();
    }
}