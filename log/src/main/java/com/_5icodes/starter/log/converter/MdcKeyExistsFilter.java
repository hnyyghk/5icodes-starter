package com._5icodes.starter.log.converter;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(name = "MdcKeyExistsFilter", category = Node.CATEGORY, elementType = Filter.ELEMENT_TYPE, printObject = true)
@PerformanceSensitive("allocation")
public class MdcKeyExistsFilter extends AbstractFilter {
    private final String key;

    private MdcKeyExistsFilter(String key, Result onMatch, Result onMismatch) {
        super(onMatch, onMismatch);
        this.key = key;
    }

    @Override
    public Result filter(LogEvent event) {
        return event.getContextData().containsKey(key) ? onMatch : onMismatch;
    }

    @PluginFactory
    public static MdcKeyExistsFilter createFilter(
            @PluginAttribute("key") String key,
            @PluginAttribute("onMatch") Result match,
            @PluginAttribute("onMismatch") Result mismatch) {
        Result onMatch = match == null ? Result.NEUTRAL : match;
        Result onMismatch = mismatch == null ? Result.DENY : mismatch;
        return new MdcKeyExistsFilter(key, onMatch, onMismatch);
    }
}