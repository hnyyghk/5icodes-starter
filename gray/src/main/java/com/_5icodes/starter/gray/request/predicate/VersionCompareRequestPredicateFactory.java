package com._5icodes.starter.gray.request.predicate;

import com._5icodes.starter.gray.request.RequestPredicate;
import com._5icodes.starter.gray.request.RequestPredicateFactory;
import com._5icodes.starter.sleuth.utils.BaggageFieldUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

@Slf4j
public class VersionCompareRequestPredicateFactory implements RequestPredicateFactory<VersionCompareRequestPredicateFactory.Config> {
    @Override
    public RequestPredicate apply(Config config) {
        CompareType compareType = config.getType();
        int[] version = parseVersion(config.getVersion());
        String header = config.getHeader();
        return () -> {
            String headerVal = BaggageFieldUtils.get(header);
            boolean predicate = compareType.test(parseVersion(headerVal), version);
            log.trace("request header {}: {} predicate result: {}", header, headerVal, predicate);
            return predicate;
        };
    }

    private static int[] parseVersion(String versionStr) {
        if (!StringUtils.hasLength(versionStr)) {
            return null;
        }
        try {
            String[] split = versionStr.split("\\.");
            int[] version = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                version[i] = Integer.parseInt(split[i]);
            }
            return version;
        } catch (Exception e) {
            log.warn("parse versionStr {} failed.", versionStr, e);
            return null;
        }
    }

    @Data
    public static class Config {
        private String header;
        private String version;
        private CompareType type;
    }

    public enum CompareType {
        /**
         * 大于等于
         */
        GE {
            @Override
            public boolean test(int diff) {
                return diff >= 0;
            }
        },
        /**
         * 大于
         */
        G {
            @Override
            public boolean test(int diff) {
                return diff > 0;
            }
        },
        /**
         * 等于
         */
        E {
            @Override
            public boolean test(int diff) {
                return diff == 0;
            }
        },
        /**
         * 小于等于
         */
        LE {
            @Override
            public boolean test(int diff) {
                return diff <= 0;
            }
        },
        /**
         * 小于
         */
        L {
            @Override
            public boolean test(int diff) {
                return diff < 0;
            }
        };

        public abstract boolean test(int diff);

        public boolean test(int[] v1, int[] v2) {
            int l1 = ArrayUtils.getLength(v1);
            int l2 = ArrayUtils.getLength(v2);
            if (l1 != l2) {
                return false;
            }
            int diff;
            for (int i = 0; i < l1; i++) {
                diff = v1[i] - v2[i];
                if (diff != 0) {
                    return test(diff);
                }
            }
            return test(0);
        }
    }
}