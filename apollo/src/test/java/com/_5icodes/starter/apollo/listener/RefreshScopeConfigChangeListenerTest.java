package com._5icodes.starter.apollo.listener;

import com._5icodes.starter.apollo.ApolloConstants;
import com._5icodes.starter.apollo.utils.ApolloUtils;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.script.*"})
public class RefreshScopeConfigChangeListenerTest {
    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private RefreshScopeConfigChangeListener changeListener;

    @Test
    @PrepareForTest(ApolloUtils.class)
    public void onChange() {
        String namespace = "namespace";
        String differentNamespace = "diff_namespace";

        PowerMockito.mockStatic(ApolloUtils.class);
        PowerMockito.when(ApolloUtils.preLoadPublicNamespaces()).thenReturn(Sets.newHashSet(namespace, ApolloConstants.COMMON_NAME_SPACE));

        RefreshScope refreshScope = Mockito.mock(RefreshScope.class);
        Mockito.when(applicationContext.getBean(RefreshScope.class)).thenReturn(refreshScope);
        Map<String, ConfigChange> changes = new HashMap<>();
        changes.put("key1", new ConfigChange(namespace, "key1", null, "newValue", PropertyChangeType.ADDED));

        ConfigChangeEvent event = new ConfigChangeEvent(namespace, changes);
        changeListener.onChange(event);
        Mockito.verify(refreshScope, Mockito.never()).refreshAll();

        Mockito.clearInvocations(refreshScope);
        event = new ConfigChangeEvent(ApolloConstants.COMMON_NAME_SPACE, changes);
        changeListener.onChange(event);
        Mockito.verify(refreshScope).refreshAll();

        Mockito.clearInvocations(refreshScope);
        event = new ConfigChangeEvent(differentNamespace, changes);
        changeListener.onChange(event);
        Mockito.verify(refreshScope).refreshAll();
    }
}