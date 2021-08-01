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
        cn_qingdao("华北 1 (青岛)"),
        cn_beijing("华北 2 (北京)"),
        cn_zhangjiakou("华北 3 (张家口)"),
        cn_huhehaote("华北 5 (呼和浩特)"),
        cn_wulanchabu("华北 6 (乌兰察布)"),
        cn_hangzhou("华东 1 (杭州)"),
        cn_shanghai("华东 2 (上海)"),
        cn_shenzhen("华南 1 (深圳)"),
        cn_heyuan("华南 2 (河源)"),
        cn_guangzhou("华南 3 (广州)"),
        cn_chengdu("西南 1 (成都)"),
        cn_hongkong("中国（香港）"),
        ap_northeast_1("亚太东北 1 (东京)"),
        ap_southeast_1("亚太东南 1 (新加坡)"),
        ap_southeast_2("亚太东南 2 (悉尼)"),
        ap_southeast_3("亚太东南 3 (吉隆坡)"),
        ap_southeast_5("亚太东南 5 (雅加达)"),
        /**
         * 欧洲与美洲
         */
        us_west_1("美国西部 1 (硅谷)"),
        us_east_1("美国东部 1 (弗吉尼亚)"),
        eu_central_1("欧洲中部 1 (法兰克福)"),
        eu_west_1("英国（伦敦）"),
        /**
         * 中东与印度
         */
        me_east_1("中东东部 1 (迪拜)"),
        ap_south_1("亚太南部 1 (孟买)"),
        ;

        private final String name;

        ZONE(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static String getZone() {
        if (zone == null) {
            ConfigurableEnvironment environment;
            try {
                environment = SpringUtils.getBean(ConfigurableEnvironment.class);
            } catch (Exception e) {
                return ZONE.cn_shenzhen.name();
            }
            String property = environment.getProperty(EUREKA_INSTANCE_ZONE);
            zone = property != null ? property : ZONE.cn_shenzhen.name();
        }
        return zone;
    }
}