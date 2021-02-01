package com._5icodes.starter.apollo;

import java.util.Collections;
import java.util.List;

public class CommonPublicNamespaceLoader implements PublicNamespaceLoader {
    @Override
    public List<String> loadPublicNamespaces() {
        return Collections.singletonList(ApolloConstants.COMMON_NAME_SPACE);
    }
}