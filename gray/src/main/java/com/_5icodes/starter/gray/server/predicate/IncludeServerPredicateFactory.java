package com._5icodes.starter.gray.server.predicate;

import com._5icodes.starter.gray.enums.ServerMetaEnum;
import com._5icodes.starter.gray.server.ServerPredicate;
import com._5icodes.starter.gray.server.ServerPredicateFactory;
import lombok.Data;

import java.util.Set;

public class IncludeServerPredicateFactory implements ServerPredicateFactory<IncludeServerPredicateFactory.Config> {
    @Override
    public ServerPredicate apply(Config config) {
        ServerMetaEnum meta = config.getMeta();
        Set<String> whiteList = config.getWhiteList();
        return server -> meta.predicateWhite(server, whiteList);
    }

    @Data
    public static class Config {
        /**
         * VERSION/TAGS/REGION
         */
        private ServerMetaEnum meta;
        private Set<String> whiteList;
    }
}