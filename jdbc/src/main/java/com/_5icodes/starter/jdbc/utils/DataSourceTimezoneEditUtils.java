package com._5icodes.starter.jdbc.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class DataSourceTimezoneEditUtils {
    public final String SERVER_TIMEZONE = "serverTimezone";
    public final String ASIA_SHANGHAI = "Asia/Shanghai";

    public String editUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return url;
        }
        if (url.contains(SERVER_TIMEZONE)) {
            return url;
        }
        String timeZone = SERVER_TIMEZONE + "=" + ASIA_SHANGHAI;
        if (url.contains("?")) {
            url += "&" + timeZone;
        } else {
            url += "?" + timeZone;
        }
        return url;
    }
}