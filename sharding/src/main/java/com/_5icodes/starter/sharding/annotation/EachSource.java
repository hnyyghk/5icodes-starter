package com._5icodes.starter.sharding.annotation;

import com._5icodes.starter.sharding.constants.SourceTypeEnum;
import org.springframework.beans.factory.support.BeanNameGenerator;
import tk.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.annotation.*;

/**
 * 单个数据源配置，这里用于控制数据源中mapper接口、mapper文件，以及别名、数据库类型、数据源配置开始对应的标记
 *
 * @see tk.mybatis.spring.annotation.MapperScan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface EachSource {
    /**
     * 当前数据库别名
     */
    String dbPrefix();

    /**
     * 别名扫描路径(如果未配置，则mapper文件中无法使用别名)
     */
    String typeAliasesPackage() default "";

    /**
     * mapper文件存放的路径(如果未配置，则mapper文件必须和mapper接口在同一个包下面)
     */
    String[] mapperLocations() default {};

    /**
     * 数据源的类型
     */
    SourceTypeEnum sourceType();

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise
     * annotation declarations e.g.:
     * {@code @EnableMyBatisMapperScanner("org.my.pkg")} instead of {@code
     * @EnableMyBatisMapperScanner(basePackages= "org.my.pkg"})}.
     */
    String[] value() default {};

    /**
     * Base packages to scan for MyBatis interfaces. Note that only interfaces
     * with at least one method will be registered; concrete classes will be
     * ignored.
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages
     * to scan for annotated components. The package of each class specified will be scanned.
     * <p>Consider creating a special no-op marker class or interface in each package
     * that serves no purpose other than being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * The {@link BeanNameGenerator} class to be used for naming detected components
     * within the Spring container.
     */
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    /**
     * This property specifies the annotation that the scanner will search for.
     * <p>
     * The scanner will register all interfaces in the base package that also have
     * the specified annotation.
     * <p>
     * Note this can be combined with markerInterface.
     */
    Class<? extends Annotation> annotationClass() default Annotation.class;

    /**
     * This property specifies the parent that the scanner will search for.
     * <p>
     * The scanner will register all interfaces in the base package that also have
     * the specified interface class as a parent.
     * <p>
     * Note this can be combined with annotationClass.
     */
    Class<?> markerInterface() default Class.class;

    /**
     * Specifies a custom MapperFactoryBean to return a mybatis proxy as spring bean.
     *
     */
    Class<? extends MapperFactoryBean> factoryBean() default MapperFactoryBean.class;

    /**
     * 通用 Mapper 的配置，一行一个配置
     *
     * @return
     */
    String[] properties() default {};

    /**
     * 还可以直接配置一个 MapperHelper bean
     *
     * @return
     */
    String mapperHelperRef() default "";
}