package com._5icodes.starter.drools;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * 规则引擎配置类
 */
@Configuration
@Slf4j
//获取KieServices->获取KieContainer->获取KieSession(会话对象,用于和规则引擎交互)->Insert fact->触发规则->关闭KieSession
//kbase name:指定kbase的名称,可以任意,但是需要唯一;packages:指定规则文件的目录,需要根据实际情况填写,否则无法加载到规则文件;default:指定当前kbase是否为默认
//ksession name:指定ksession名称,可以任意,但是需要唯一;default:指定当前session是否为默认
public class DroolsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public KieServices kieServices() {
        return KieServices.Factory.get();
    }

    @Bean
    @ConditionalOnMissingBean
    public KieFileSystem kieFileSystem(KieServices kieServices) throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] files = resourcePatternResolver.getResources("classpath*:" + DroolsConstants.RULES_PATH + "**/*.*");
        for (Resource file : files) {
            String path = DroolsConstants.RULES_PATH + file.getFilename();
            kieFileSystem.write(ResourceFactory.newClassPathResource(path, "UTF-8"));
        }
        return kieFileSystem;
    }

    @Bean
    @ConditionalOnMissingBean
    public KieContainer kieContainer(KieServices kieServices, KieFileSystem kieFileSystem) {
        KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        return kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
    }

    @Bean
    @ConditionalOnMissingBean
    public KieBase kieBase(KieContainer kieContainer) {
        return kieContainer.getKieBase();
    }

    @Bean
    @ConditionalOnMissingBean
    public KModuleBeanFactoryPostProcessor kiePostProcessor() {
        return new KModuleBeanFactoryPostProcessor();
    }

//    @Bean
//    public KieContainer kieContainer() {
//        KieContainer kContainer = kieServices().newKieClasspathContainer();
//        // Let's verify that all the resources are loaded correctly
//        Results results = kContainer.verify();
//        results.getMessages().forEach((message) -> {
//            log.info(">> Message ( {} ): {}", message.getLevel(), message.getText());
//        });
//        // If there is an Error we need to stop and correct it
//        boolean hasError = results.hasMessages(Message.Level.ERROR);
//        log.info("Any Error : {}", hasError);
//        if (hasError) {
//            throw new UnsupportedOperationException();
//        }
//        // Here we make sure that all the KieBases and KieSessions
//        // that we are expecting are loaded.
//        kContainer.getKieBaseNames().stream()
//                .peek((kieBase) -> log.info(">> Loading KieBase: {}", kieBase))
//                .forEach((kieBase) -> kContainer.getKieSessionNamesInKieBase(kieBase)
//                        .forEach((kieSession) -> log.info("\t >> Containing KieSession: {}", kieSession)));
//        return kContainer;
//    }
}