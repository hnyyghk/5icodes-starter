package com._5icodes.starter.apollo.listener;

import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class FireEnvironmentChangeEventConfigChangeListenerTest {
    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private FireEnvironmentChangeEventConfigChangeListener changeListener;

    @Test
    public void onChange() {
        String nameSpace = "application";
        Map<String, ConfigChange> changes = new HashMap<>();
        changes.put("key1", new ConfigChange(nameSpace, "key1", null, "newValue", PropertyChangeType.ADDED));
        changes.put("key2", new ConfigChange(nameSpace, "key2", "oldValue", "newValue", PropertyChangeType.MODIFIED));

        ConfigChangeEvent event = new ConfigChangeEvent(nameSpace, changes);
        changeListener.onChange(event);
        Mockito.verify(applicationContext).publishEvent(ArgumentMatchers.argThat(arg -> {
            Assert.assertTrue(arg instanceof EnvironmentChangeEvent);
            EnvironmentChangeEvent changeEvent = (EnvironmentChangeEvent) arg;
            Set<String> keys = changeEvent.getKeys();
            MatcherAssert.assertThat(keys, Matchers.containsInAnyOrder("key1", "key2"));
            return true;
        }));
    }
}