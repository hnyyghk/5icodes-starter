package com._5icodes.starter.gray;

import com._5icodes.starter.apollo.PublicNamespaceLoader;

import java.util.Collections;
import java.util.List;

public class GrayPublicNamespaceLoader implements PublicNamespaceLoader {
    @Override
    public List<String> loadPublicNamespaces() {
        return Collections.singletonList(GrayConstants.GRAY_APOLLO_NAMESPACE);
    }
}