package com._5icodes.starter.log;

import cn.hutool.core.util.StrUtil;
import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

public class LogEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    private final static String LOG_FILE_FORMATTER = "/data/logs/{}/{}.log";
    private final static String LOG_PATTERN_FORMATTER = "/data/logs/{}/{}-%d{yyyy-MM-dd}-%i.log";

    private final static String GRAY_LOG_FILE_FORMATTER = "/data/logs/{}/{}-gray.log";
    private final static String GRAY_LOG_PATTERN_FORMATTER = "/data/logs/{}/{}-gray-%d{yyyy-MM-dd}-%i.log";

    private final static String TRACE_TEST_LOG_FILE_FORMATTER = "/data/logs/{}/{}-trace-test.log";
    private final static String TRACE_TEST_LOG_PATTERN_FORMATTER = "/data/logs/{}/{}-trace-test-%d{yyyy-MM-dd}-%i.log";

    @Override
    protected void onLocal(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, LogConstants.PROPERTY_PREFIX + ".showSql", true);
        putLogProperty(env);
        super.onLocal(env, application);
    }

    @Override
    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, LogConstants.PROPERTY_PREFIX + ".showSql", true);
        putLogProperty(env);
        super.onDev(env, application);
    }

    @Override
    protected void onStg(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, LogConstants.PROPERTY_PREFIX + ".showSql", true);
        putLogProperty(env);
        super.onStg(env, application);
    }

    @Override
    protected void onPrd(ConfigurableEnvironment env, SpringApplication application) {
        putLogProperty(env);
        super.onPrd(env, application);
    }

    private void putLogProperty(ConfigurableEnvironment env) {
        //灰度环境日志表示
        String group = env.getProperty("starter.meta.appGroup");
        Boolean gray = Boolean.parseBoolean(env.getProperty("starter.meta.gray"));
        Binder binder = Binder.get(env);
        BindResult<LogProperties> bindResult = binder.bind(LogConstants.PROPERTY_PREFIX, LogProperties.class);
        LogProperties logProperties;
        if (bindResult.isBound()) {
            logProperties = bindResult.get();
        } else {
            logProperties = new LogProperties();
        }
        String appName = SpringApplicationUtils.getApplicationName();
        String logDir = logProperties.getLogDir();
        if (!StringUtils.hasText(logDir)) {
            logDir = appName;
        }
        String fileName = gray ? StrUtil.format(GRAY_LOG_FILE_FORMATTER, logDir, appName)
                : StrUtil.format(LOG_FILE_FORMATTER, logDir, StringUtils.hasText(group) ? appName + "-" + group : appName);
        String filePattern = gray ? StrUtil.format(GRAY_LOG_PATTERN_FORMATTER, logDir, appName)
                : StrUtil.format(LOG_PATTERN_FORMATTER, logDir, StringUtils.hasText(group) ? appName + "-" + group : appName);
        System.setProperty("LOG_FILE_NAME", fileName);
        System.setProperty("LOG_FILE_PATTERN", filePattern);
        if (logProperties.isEnableTraceTestFile()) {
            //全链路压测日志命名
            String traceTestFileName = StrUtil.format(TRACE_TEST_LOG_FILE_FORMATTER, logDir, appName);
            String traceTestFilePattern = StrUtil.format(TRACE_TEST_LOG_PATTERN_FORMATTER, logDir, appName);
            System.setProperty("TRACE_TEST_LOG_FILE_NAME", traceTestFileName);
            System.setProperty("TRACE_TEST_LOG_FILE_PATTERN", traceTestFilePattern);
        } else {
            System.setProperty("TRACE_TEST_LOG_FILE_NAME", fileName);
            System.setProperty("TRACE_TEST_LOG_FILE_PATTERN", filePattern);
        }
        System.setProperty("CONSOLE_LOG_PATTERN", logProperties.getConsoleLogPattern());
        System.setProperty("FILE_LOG_PATTERN", logProperties.getFileLogPattern());
        System.setProperty("LOG_MAX_LEN", String.valueOf(logProperties.getMaxLength()));
        System.setProperty("LOG_LINE_NUM", String.valueOf(logProperties.getLineNum()));
        System.setProperty("AsyncLoggerConfig.RingBufferSize", String.valueOf(logProperties.getRingBufferSize()));
        PropertySourceUtils.put(env, "logging.config", "classpath:log4j2-template.xml");
    }
}