package com._5icodes.starter.jdbc.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class JdbcUrlResolveUtils {
    private final Map<String, Pattern> DB_PATTERNS = new HashMap<>();

    static {
        // jdbc:oracle:thin:@127.0.0.1:1234:test1
        // jdbc:oracle:thin:@127.0.0.1:1234/test2
        DB_PATTERNS.put("oracle", Pattern.compile("jdbc:oracle:thin:[^:]*:[^:]*[:/]([^?]*)"));
        // jdbc:mysql://127.0.0.1:1234/test3
        // jdbc:mysql://127.0.0.1:1234/test4?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&allowMultiQueries=true&useSSL=true&serverTimezone=Asia/Shanghai
        DB_PATTERNS.put("mysql", Pattern.compile("jdbc:mysql://[^/]*/([^?]*)"));
        // jdbc:postgresql://localhost:1234/test5?stringtype=unspecified
        DB_PATTERNS.put("postgresql", Pattern.compile("jdbc:postgresql://[^/]*/([^?]*)"));
    }

    public Optional<Pair<String, String>> resolve(String jdbcUrl) {
        if (!StringUtils.hasText(jdbcUrl)) {
            return Optional.empty();
        }
        for (Map.Entry<String, Pattern> entry : DB_PATTERNS.entrySet()) {
            Matcher matcher = entry.getValue().matcher(jdbcUrl);
            if (matcher.find()) {
                return Optional.of(Pair.of(entry.getKey(), matcher.group(1)));
            }
        }
        return Optional.empty();
    }
}