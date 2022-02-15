package com._5icodes.starter.common.utils;

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 区域获取类
 */
public class RegionUtils {
    private final static String EUREKA_INSTANCE_ZONE = "eureka.instance.metadata-map.zone";

    /**
     * 机房
     */
    private static String zone;

    public enum ZONE {
        /**
         * 亚太
         */
        CN_QINGDAO("华北 1 (青岛)"),
        CN_BEIJING("华北 2 (北京)"),
        CN_ZHANGJIAKOU("华北 3 (张家口)"),
        CN_HUHEHAOTE("华北 5 (呼和浩特)"),
        CN_WULANCHABU("华北 6 (乌兰察布)"),
        CN_HANGZHOU("华东 1 (杭州)"),
        CN_SHANGHAI("华东 2 (上海)"),
        CN_SHENZHEN("华南 1 (深圳)"),
        CN_HEYUAN("华南 2 (河源)"),
        CN_GUANGZHOU("华南 3 (广州)"),
        CN_CHENGDU("西南 1 (成都)"),
        CN_HONGKONG("中国（香港）"),
        //亚太东北 1
        AP_NORTHEAST_1("日本 (东京)"),
        //亚太东南 1
        AP_SOUTHEAST_1("新加坡"),
        //亚太东南 2
        AP_SOUTHEAST_2("澳大利亚 (悉尼)"),
        //亚太东南 3
        AP_SOUTHEAST_3("马来西亚 (吉隆坡)"),
        //亚太东南 5
        AP_SOUTHEAST_5("印度尼西亚 (雅加达)"),
        //亚太东南 6
        AP_SOUTHEAST_6("菲律宾 (马尼拉)"),
        /**
         * 欧洲与美洲
         */
        //美国西部 1
        US_WEST_1("美国 (硅谷)"),
        //美国东部 1
        US_EAST_1("美国 (弗吉尼亚)"),
        //欧洲中部 1
        EU_CENTRAL_1("德国 (法兰克福)"),
        EU_WEST_1("英国（伦敦）"),
        /**
         * 中东与印度
         */
        //中东东部 1
        ME_EAST_1("阿联酋 (迪拜)"),
        //亚太南部 1
        AP_SOUTH_1("印度 (孟买)"),
        ;

        private final String name;

        ZONE(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return name().toLowerCase().replace("_", "-");
        }
    }

    public static String getZone() {
        if (zone == null) {
            ConfigurableEnvironment environment;
            try {
                environment = SpringUtils.getBean(ConfigurableEnvironment.class);
            } catch (Exception e) {
                return ZONE.CN_SHENZHEN.getValue();
            }
            zone = environment.getProperty(EUREKA_INSTANCE_ZONE, String.class, ZONE.CN_SHENZHEN.getValue());
        }
        return zone;
    }
}