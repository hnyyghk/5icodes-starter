package com._5icodes.starter.jdbc.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class JdbcUrlResolveUtilsTest {
    @Test
    public void testNotJdbcUrl() {
        Optional<Pair<String, String>> opt = JdbcUrlResolveUtils.resolve(null);
        Assert.assertFalse(opt.isPresent());
        opt = JdbcUrlResolveUtils.resolve("");
        Assert.assertFalse(opt.isPresent());
        opt = JdbcUrlResolveUtils.resolve("  ");
        Assert.assertFalse(opt.isPresent());
        opt = JdbcUrlResolveUtils.resolve(RandomStringUtils.randomAlphabetic(10));
        Assert.assertFalse(opt.isPresent());
    }

    @Test
    public void testResolveOracleJdbcUrl() {
        Optional<Pair<String, String>> opt = JdbcUrlResolveUtils.resolve("jdbc:oracle:thin:@127.0.0.1:1234:test1");
        Assert.assertTrue(opt.isPresent());
        Pair<String, String> pair = opt.get();
        Assert.assertEquals("oracle", pair.getLeft());
        Assert.assertEquals("test1", pair.getRight());

        opt = JdbcUrlResolveUtils.resolve("jdbc:oracle:thin:@127.0.0.1:1234/test2");
        Assert.assertTrue(opt.isPresent());
        pair = opt.get();
        Assert.assertEquals("oracle", pair.getLeft());
        Assert.assertEquals("test2", pair.getRight());
    }

    @Test
    public void testResolveMysqlJdbcUrlWithoutParams() {
        Optional<Pair<String, String>> opt = JdbcUrlResolveUtils.resolve("jdbc:mysql://127.0.0.1:1234/test3");
        Assert.assertTrue(opt.isPresent());
        Pair<String, String> pair = opt.get();
        Assert.assertEquals("mysql", pair.getLeft());
        Assert.assertEquals("test3", pair.getRight());
    }

    @Test
    public void testResolveMysqlJdbcUrlWithParams() {
        Optional<Pair<String, String>> opt = JdbcUrlResolveUtils.resolve("jdbc:mysql://127.0.0.1:1234/test4?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&allowMultiQueries=true&useSSL=true&serverTimezone=Asia/Shanghai");
        Assert.assertTrue(opt.isPresent());
        Pair<String, String> pair = opt.get();
        Assert.assertEquals("mysql", pair.getLeft());
        Assert.assertEquals("test4", pair.getRight());
    }

    @Test
    public void testResolvePostgresqlJdbcUrl() {
        Optional<Pair<String, String>> opt = JdbcUrlResolveUtils.resolve("jdbc:postgresql://localhost:1234/test5?stringtype=unspecified");
        Assert.assertTrue(opt.isPresent());
        Pair<String, String> pair = opt.get();
        Assert.assertEquals("postgresql", pair.getLeft());
        Assert.assertEquals("test5", pair.getRight());
    }
}