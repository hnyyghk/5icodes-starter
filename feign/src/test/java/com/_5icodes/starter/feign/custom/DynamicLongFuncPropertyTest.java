package com._5icodes.starter.feign.custom;

import com.netflix.config.ConfigurationManager;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.function.LongSupplier;

@RunWith(MockitoJUnitRunner.class)
public class DynamicLongFuncPropertyTest {
    private static final String key = "key";
    private static final long VAL_EXPECTED = 123;
    private final LongSupplier longSupplier = () -> VAL_EXPECTED;
    private final DynamicLongFuncProperty longFuncProperty = new DynamicLongFuncProperty(key, longSupplier);

    @Test
    public void getValue() {
        long val = longFuncProperty.getValue();
        Assert.assertEquals(VAL_EXPECTED, val);
        long newVal = 234;
        ConfigurationEvent configurationEvent = new ConfigurationEvent(new Object(), AbstractConfiguration.EVENT_ADD_PROPERTY, key, newVal, false);
        Collection<ConfigurationListener> configurationListeners = ConfigurationManager.getConfigInstance().getConfigurationListeners();
        for (ConfigurationListener configurationListener : configurationListeners) {
            configurationListener.configurationChanged(configurationEvent);
        }
        val = longFuncProperty.getValue();
        Assert.assertEquals(newVal, val);
    }

    @Test
    public void getValue1() {
        DynamicLongFuncProperty spy = Mockito.spy(longFuncProperty);
        spy.getValue();
        Mockito.verify(spy).getValue();
    }
}