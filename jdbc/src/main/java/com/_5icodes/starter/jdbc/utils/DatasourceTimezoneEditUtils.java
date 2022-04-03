package com._5icodes.starter.jdbc.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DatasourceTimezoneEditUtils {
    public final String SERVER_TIMEZONE = "serverTimezone";
    public final String ASIA_SHANGHAI = "Asia/Shanghai";

    public String editUrl(String url) {
        if (!url.contains(SERVER_TIMEZONE)) {
            String timeZone = SERVER_TIMEZONE + "=" + ASIA_SHANGHAI;
            if (url.contains("?")) {
                url += "&" + timeZone;
            } else {
                url += "?" + timeZone;
            }
        }
        return url;
    }
}