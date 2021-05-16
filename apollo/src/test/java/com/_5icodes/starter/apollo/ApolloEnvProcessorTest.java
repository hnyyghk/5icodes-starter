package com._5icodes.starter.apollo;

import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PropertySourceUtils.class, SpringApplicationUtils.class})
@PowerMockIgnore({"javax.management.*", "javax.script.*"})
public class ApolloEnvProcessorTest {
    @Spy
    private final ApolloEnvInjector apolloEnvInjector = new ApolloEnvInjector();
    @Mock
    private ConfigurableEnvironment environment;
    @Mock
    private SpringApplication application;
    @Mock
    private MutablePropertySources propertySources;

    public static class ApolloConfigTestNull {
    }

    @EnableApolloConfig({"test1.yml", "test2.properties", "application.properties"})
    public static class ApolloConfigTestOne {
    }

    @Test
    public void onAllProfiles() {
        PowerMockito.mockStatic(PropertySourceUtils.class);
        PowerMockito.mockStatic(SpringApplicationUtils.class);
        String appName = "appName";
        PowerMockito.when(SpringApplicationUtils.getApplicationName()).thenReturn(appName);
        Mockito.doNothing().when(apolloEnvInjector).processEnableApolloConfig(environment, application);
        apolloEnvInjector.onAllProfiles(environment, application);

        PowerMockito.verifyStatic(PropertySourceUtils.class);
        PropertySourceUtils.put(environment, "app.id", appName);

        PowerMockito.verifyStatic(PropertySourceUtils.class);
        PropertySourceUtils.put(environment, PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, true);

        PowerMockito.verifyStatic(PropertySourceUtils.class);
        PropertySourceUtils.put(environment, "apollo.cacheDir", "/data/webapps/" + appName + "/conf");
    }

    @Test
    public void processDisableApolloConfig() {
        Mockito.when(environment.getPropertySources()).thenReturn(propertySources);

        PowerMockito.mockStatic(PropertySourceUtils.class);
        Mockito.when(environment.containsProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES)).thenReturn(true);
        apolloEnvInjector.processEnableApolloConfig(environment, application);
        PowerMockito.verifyStatic(PropertySourceUtils.class, VerificationModeFactory.noMoreInteractions());
        PropertySourceUtils.put(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void processEnableApolloConfigDefault() {
        PowerMockito.mockStatic(SpringApplicationUtils.class);

        Mockito.when(environment.getPropertySources()).thenReturn(propertySources);

        PowerMockito.mockStatic(PropertySourceUtils.class);
        Mockito.when(environment.containsProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES)).thenReturn(false);
        PowerMockito.<Class>when(SpringApplicationUtils.getBootApplicationClass(application)).thenReturn(ApolloConfigTestNull.class);
        apolloEnvInjector.processEnableApolloConfig(environment, application);
        PowerMockito.verifyStatic(PropertySourceUtils.class);
        PropertySourceUtils.put(ArgumentMatchers.eq(environment), ArgumentMatchers.eq(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES), ArgumentMatchers.<String>argThat(str -> {
            String[] strings = str.split(",");
            MatcherAssert.assertThat(strings, Matchers.arrayContainingInAnyOrder(ConfigConsts.NAMESPACE_APPLICATION, ApolloConstants.COMMON_NAME_SPACE));
            return true;
        }));
    }

    @Test
    public void processEnableApolloConfigDuplicate() {
        PowerMockito.mockStatic(SpringApplicationUtils.class);
        Mockito.when(environment.getPropertySources()).thenReturn(propertySources);

        PowerMockito.mockStatic(PropertySourceUtils.class);
        Mockito.when(environment.containsProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES)).thenReturn(false);
        PowerMockito.<Class>when(SpringApplicationUtils.getBootApplicationClass(application)).thenReturn(ApolloConfigTestOne.class);

        apolloEnvInjector.processEnableApolloConfig(environment, application);
        PowerMockito.verifyStatic(PropertySourceUtils.class);
        PropertySourceUtils.put(ArgumentMatchers.eq(environment), ArgumentMatchers.eq(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES), ArgumentMatchers.<String>argThat(str -> {
            String[] strings = str.split(",");
            MatcherAssert.assertThat(strings, Matchers.arrayContainingInAnyOrder("test1.yml", "test2.properties", ConfigConsts.NAMESPACE_APPLICATION, ApolloConstants.COMMON_NAME_SPACE));
            return true;
        }));
    }

    @Test
    public void onPrd() {
        ApolloEnvInjector mock = Mockito.mock(ApolloEnvInjector.class);
        Mockito.doCallRealMethod().when(mock).onPrd(environment, application);

        mock.onPrd(environment, application);
        Mockito.verify(mock).setApolloMetaLocation(environment, "http://113.104.209.69/apolloConfig");
    }

    @Test
    public void setApolloMetaLocation() {
        PowerMockito.mockStatic(PropertySourceUtils.class);
        String apolloMetaLocation = "any url";
        apolloEnvInjector.setApolloMetaLocation(environment, apolloMetaLocation);
        PowerMockito.verifyStatic(PropertySourceUtils.class);
        PropertySourceUtils.put(environment, "apollo.meta", apolloMetaLocation);
    }

    @Test
    public void getOrder() {
        Assert.assertTrue(apolloEnvInjector.getOrder() < 0);
    }
}