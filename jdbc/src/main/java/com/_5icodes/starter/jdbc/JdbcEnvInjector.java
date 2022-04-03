package com._5icodes.starter.jdbc;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.ClassUtils;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.jdbc.utils.DatasourceTimezoneEditUtils;
import com._5icodes.starter.jdbc.utils.JdbcUrlResolveUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.session.ResultContext;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

import static org.objectweb.asm.Opcodes.*;

public class JdbcEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        //datasource
        String name = "spring.datasource.druid.name";
        String validationQuery = "spring.datasource.druid.validation-query";
        String maxActive = "spring.datasource.druid.max-active";
        String initialSize = "spring.datasource.druid.initial-size";
        String maxWait = "spring.datasource.druid.max-wait";
        String minIdle = "spring.datasource.druid.min-idle";
        String timeBetweenEvictionRunsMillis = "spring.datasource.druid.time-between-eviction-runs-millis";
        String minEvictableIdleTimeMillis = "spring.datasource.druid.min-evictable-idle-time-millis";
        String testWhileIdle = "spring.datasource.druid.test-while-idle";
        String testOnBorrow = "spring.datasource.druid.test-on-borrow";
        String testOnReturn = "spring.datasource.druid.test-on-return";
        String poolPreparedStatements = "spring.datasource.druid.pool-prepared-statements";
        String maxOpenPreparedStatements = "spring.datasource.druid.max-open-prepared-statements";
        PropertySourceUtils.put(env, name, "druid-datasource");
        //用来检测连接是否有效的sql，要求是一个查询语句。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用
        PropertySourceUtils.put(env, validationQuery, "SELECT 1");
        //最大连接池个数
        PropertySourceUtils.put(env, maxActive, 50);
        //初始化连接池个数
        PropertySourceUtils.put(env, initialSize, 1);
        //配置获取连接等待超时的时间，单位毫秒，缺省启用公平锁，并发效率会有所下降
        PropertySourceUtils.put(env, maxWait, 1000);
        //最小连接池个数
        PropertySourceUtils.put(env, minIdle, 3);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 1min
        PropertySourceUtils.put(env, timeBetweenEvictionRunsMillis, 60000L);
        //配置一个连接在池中最小生存的时间，单位是毫秒 7h
        PropertySourceUtils.put(env, minEvictableIdleTimeMillis, 25200000L);
        //建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        PropertySourceUtils.put(env, testWhileIdle, true);
        //申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        PropertySourceUtils.put(env, testOnBorrow, false);
        //归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        PropertySourceUtils.put(env, testOnReturn, false);
        //开启PSCache
        PropertySourceUtils.put(env, poolPreparedStatements, true);
        //指定每个连接上PSCache的大小
        PropertySourceUtils.put(env, maxOpenPreparedStatements, 20);

        String datasourceUrlKey = "spring.datasource.url";
        String originUrl = env.getProperty(datasourceUrlKey);
        if (originUrl != null) {
            JdbcUrlResolveUtils.resolve(originUrl).ifPresent(pair -> {
                if ("mysql".equals(pair.getLeft())) {
                    String editedUrl = DatasourceTimezoneEditUtils.editUrl(originUrl);
                    if (!editedUrl.equals(originUrl)) {
                        PropertySourceUtils.putPriority(env, datasourceUrlKey, editedUrl);
                    }
                } else if ("oracle".equals(pair.getLeft())) {
                    PropertySourceUtils.put(env, validationQuery, "SELECT 1 FROM DUAL");
                }
            });
        }

        //mybatis
        String defaultStatementTimeout = "mybatis.configuration.default-statement-timeout";
        String defaultFetchSize = "mybatis.configuration.default-fetch-size";
        //设置超时时间，它决定驱动等待数据库响应的秒数。
        PropertySourceUtils.put(env, defaultStatementTimeout, 3);
        //为驱动的结果集获取数量（fetchSize）设置一个提示值。此参数只可以在查询设置中被覆盖。
        PropertySourceUtils.put(env, defaultFetchSize, 500);
        Binder binder = Binder.get(env);
        BindResult<JdbcProperties> result = binder.bind(JdbcConstants.PROPERTY_PREFIX, Bindable.of(JdbcProperties.class));
        JdbcProperties jdbcProperties;
        if (result.isBound()) {
            jdbcProperties = result.get();
        } else {
            jdbcProperties = new JdbcProperties();
        }
        PropertySourceUtils.put(env, JdbcConstants.PROPERTY_PREFIX + ".traceTestEnable", !CollectionUtils.isEmpty(jdbcProperties.getTraceTestMap()));
        int maxResultSet = jdbcProperties.getMaxResultSet();
        if (maxResultSet > 0) {
            changeMybatisResultHandler(maxResultSet);
        }
        super.onAllProfiles(env, application);
    }

    /**
     * 通过asm修改
     *
     * @see org.apache.ibatis.executor.result.DefaultResultHandler#handleResult(ResultContext)
     * 的源码为
     * @see com._5icodes.starter.jdbc.mybatis.DefaultResultHandler#handleResult(ResultContext)
     */
    private void changeMybatisResultHandler(int maxResultSet) {
        String defaultResultHandlerClassName = "org.apache.ibatis.executor.result.DefaultResultHandler";
        String asmClassName = defaultResultHandlerClassName.replaceAll("\\.", "/");
        ClassUtils.changeMethod(defaultResultHandlerClassName,
                "handleResult",
                "(Lorg/apache/ibatis/session/ResultContext;)V",
                methodVisitor -> {
                    Label label0 = new Label();
                    methodVisitor.visitLabel(label0);
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitFieldInsn(GETFIELD, asmClassName, "list", "Ljava/util/List;");
                    methodVisitor.visitVarInsn(ALOAD, 1);
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "org/apache/ibatis/session/ResultContext", "getResultObject", "()Ljava/lang/Object;", true);
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
                    methodVisitor.visitInsn(POP);
                    Label label1 = new Label();
                    methodVisitor.visitLabel(label1);
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitFieldInsn(GETFIELD, asmClassName, "list", "Ljava/util/List;");
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
                    methodVisitor.visitIntInsn(SIPUSH, maxResultSet);
                    Label label2 = new Label();
                    methodVisitor.visitJumpInsn(IF_ICMPLE, label2);
                    Label label3 = new Label();
                    methodVisitor.visitLabel(label3);
                    methodVisitor.visitTypeInsn(NEW, "com/_5icodes/starter/jdbc/exception/ResultSetTooBigException");
                    methodVisitor.visitInsn(DUP);
                    methodVisitor.visitLdcInsn("ResultSet size exceed " + maxResultSet);
                    methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/_5icodes/starter/jdbc/exception/ResultSetTooBigException", "<init>", "(Ljava/lang/String;)V", false);
                    methodVisitor.visitInsn(ATHROW);
                    methodVisitor.visitLabel(label2);
                    methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    methodVisitor.visitInsn(RETURN);
                    Label label4 = new Label();
                    methodVisitor.visitLabel(label4);
                    methodVisitor.visitLocalVariable("this", "L" + asmClassName + ";", null, label0, label4, 0);
                    methodVisitor.visitLocalVariable("context", "Lorg/apache/ibatis/session/ResultContext;", "Lorg/apache/ibatis/session/ResultContext<*>;", label0, label4, 1);
                    methodVisitor.visitEnd();
                });
    }
}