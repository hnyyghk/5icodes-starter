package com._5icodes.starter.common.exception;

import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.type.ClassMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Optional;

public class CodeMsgEnumProcessor implements BeanFactoryPostProcessor {
    private final CachingMetadataReaderFactoryProvider cachingMetadataReaderFactoryProvider;

    public CodeMsgEnumProcessor(CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider) {
        cachingMetadataReaderFactoryProvider = metadataReaderFactoryProvider;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        String codeMsgClassName = CodeMsg.class.getName();
        cachingMetadataReaderFactoryProvider.processMetadataReader(metadataReader -> {
            try {
                ClassMetadata classMetadata = metadataReader.getClassMetadata();
                String[] interfaceNames = classMetadata.getInterfaceNames();
                if (classMetadata.isFinal() && "java.lang.Enum".equals(classMetadata.getSuperClassName())) {
                    Optional<String> optional = Arrays.stream(interfaceNames).filter(codeMsgClassName::equals).findFirst();
                    if (!optional.isPresent()) {
                        return;
                    }
                    Class<? extends CodeMsg> aClass = (Class<? extends CodeMsg>) ClassUtils.forName(classMetadata.getClassName(), ClassUtils.getDefaultClassLoader());
                    CodeMsg[] enumConstants = aClass.getEnumConstants();
                    for (CodeMsg enumConstant : enumConstants) {
                        CodeMsgRegistry.register(enumConstant);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}