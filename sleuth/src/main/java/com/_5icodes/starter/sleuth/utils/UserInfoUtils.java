package com._5icodes.starter.sleuth.utils;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.sleuth.SleuthConstants;

import java.util.Map;
import java.util.Optional;

/**
 * 用户信息工具类
 */
public class UserInfoUtils {
    public interface CallbackService {
        /**
         * 回调兜底方法
         *
         * @return
         */
        String callback();
    }

    private static final CallbackService DEFAULT_CALLBACK = () -> null;

    /**
     * 通过key解析用户的信息，当通过请求头不能获取到时，就会调用callbackService方法获取（应用需自己实现callbackService）
     *
     * @param key
     * @param callbackService
     * @return
     */
    public static String get(String key, CallbackService callbackService) {
        return Optional.ofNullable(BaggageFieldUtils.get(key)).orElseGet(() ->
                Optional.ofNullable(parseUserInfo(key)).orElseGet(() ->
                        Optional.ofNullable(callbackService).orElse(DEFAULT_CALLBACK).callback()));
    }

    /**
     * 通过key解析用户的信息
     *
     * @param key
     * @return
     */
    private static String parseUserInfo(String key) {
        String userInfo = BaggageFieldUtils.get(SleuthConstants.X_USER_INFO);
        try {
            Map<String, String> map = JsonUtils.parseToMap(userInfo, String.class, String.class);
            if (map != null) {
                return map.get(key);
            }
        } catch (Exception ignore) {
        }
        return null;
    }
}