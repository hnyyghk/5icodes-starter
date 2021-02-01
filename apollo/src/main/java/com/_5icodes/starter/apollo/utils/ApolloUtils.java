package com._5icodes.starter.apollo.utils;

import com._5icodes.starter.apollo.PublicNamespaceLoader;
import com._5icodes.starter.common.utils.ServiceLoaderUtils;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@UtilityClass
public class ApolloUtils {
    private final Set<String> PUBLIC_NAMESPACES = new HashSet<>();

    static {
        Iterator<PublicNamespaceLoader> loaderIterator = ServiceLoaderUtils.loadAllIfPresent(PublicNamespaceLoader.class);
        while (loaderIterator.hasNext()) {
            PublicNamespaceLoader loader = loaderIterator.next();
            List<String> publicNamespaces = loader.loadPublicNamespaces();
            if (CollectionUtils.isEmpty(publicNamespaces)) {
                continue;
            }
            for (String publicNamespace : publicNamespaces) {
                PUBLIC_NAMESPACES.add(format(publicNamespace));
            }
        }
    }

    public String format(String namespace) {
        if (ConfigConsts.NAMESPACE_APPLICATION.equals(namespace) || (ConfigConsts.NAMESPACE_APPLICATION + "." + ConfigFileFormat.Properties.getValue()).equals(namespace)) {
            return ConfigConsts.NAMESPACE_APPLICATION;
        }
        ConfigFileFormat[] configFileFormats = ConfigFileFormat.values();
        for (ConfigFileFormat configFileFormat : configFileFormats) {
            if (namespace.endsWith("." + configFileFormat.getValue())) {
                return namespace;
            }
        }
        return namespace + "." + ConfigFileFormat.Properties.getValue();
    }

    public Set<String> preLoadPublicNamespaces() {
        return PUBLIC_NAMESPACES;
    }
}