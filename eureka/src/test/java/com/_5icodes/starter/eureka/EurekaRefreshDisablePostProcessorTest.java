package com._5icodes.starter.eureka;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

@RunWith(MockitoJUnitRunner.class)
public class EurekaRefreshDisablePostProcessorTest {
    private final EurekaRefreshDisablePostProcessor postProcessor = new EurekaRefreshDisablePostProcessor();

    @Test
    public void postProcessBeanDefinitionRegistry() throws Exception {
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(EurekaDiscoveryClientConfiguration.class.getName());
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        String[] memberClassNames = classMetadata.getMemberClassNames();
        MatcherAssert.assertThat(memberClassNames, Matchers.arrayWithSize(2));
        String memberClassToRemove = "org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration$EurekaClientConfigurationRefresher";
        MatcherAssert.assertThat(memberClassNames, Matchers.hasItemInArray(memberClassToRemove));

        postProcessor.setMetadataReaderFactory(metadataReaderFactory);
        postProcessor.postProcessBeanDefinitionRegistry(Mockito.mock(BeanDefinitionRegistry.class));

        memberClassNames = classMetadata.getMemberClassNames();
        MatcherAssert.assertThat(memberClassNames, Matchers.arrayWithSize(1));
        MatcherAssert.assertThat(memberClassNames, Matchers.not(Matchers.hasItemInArray(memberClassToRemove)));
    }

    @Test
    public void testPriority() {
        MatcherAssert.assertThat(postProcessor, Matchers.instanceOf(PriorityOrdered.class));
    }
}