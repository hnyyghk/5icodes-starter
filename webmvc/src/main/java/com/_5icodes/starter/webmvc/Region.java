package com._5icodes.starter.webmvc;

import com._5icodes.starter.common.utils.SpringUtils;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 区域获取类
 */
public class Region {
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
        AP_NORTHEAST_1("亚太东北 1 (东京)"),
        AP_SOUTHEAST_1("亚太东南 1 (新加坡)"),
        AP_SOUTHEAST_2("亚太东南 2 (悉尼)"),
        AP_SOUTHEAST_3("亚太东南 3 (吉隆坡)"),
        AP_SOUTHEAST_5("亚太东南 5 (雅加达)"),
        /**
         * 欧洲与美洲
         */
        US_WEST_1("美国西部 1 (硅谷)"),
        US_EAST_1("美国东部 1 (弗吉尼亚)"),
        EU_CENTRAL_1("欧洲中部 1 (法兰克福)"),
        EU_WEST_1("英国（伦敦）"),
        /**
         * 中东与印度
         */
        ME_EAST_1("中东东部 1 (迪拜)"),
        AP_SOUTH_1("亚太南部 1 (孟买)"),
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
            String property = environment.getProperty(EUREKA_INSTANCE_ZONE);
            zone = property != null ? property : ZONE.CN_SHENZHEN.getValue();
        }
        return zone;
    }
}