package com._5icodes.starter.demo.jdbc;

import com._5icodes.starter.demo.jdbc.mapper.OrderMapper;
import com._5icodes.starter.jdbc.exception.ResultSetTooBigException;
import org.apache.ibatis.exceptions.PersistenceException;
import org.assertj.core.matcher.AssertionMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JdbcDemoApplication.class)
public class JdbcDemoApplicationIT {
    @Autowired
    private OrderMapper orderMapper;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testList() {
        thrown.expect(new AssertionMatcher<Throwable>() {
            @Override
            public void assertion(Throwable actual) throws AssertionError {
                Assert.assertEquals(MyBatisSystemException.class, actual.getClass());
                MyBatisSystemException myBatisSystemException = (MyBatisSystemException) actual;
                Throwable cause = myBatisSystemException.getCause();
                Assert.assertEquals(PersistenceException.class, cause.getClass());
                PersistenceException persistenceException = (PersistenceException) cause;
                Assert.assertEquals(ResultSetTooBigException.class, persistenceException.getCause().getClass());
            }
        });
        orderMapper.selectAll();
    }

    @Test
    public void findByIdTest() {
        orderMapper.selectByPrimaryKey(1L);
    }
}