package com._5icodes.starter.jdbc.trace;

import com._5icodes.starter.stress.utils.TraceTestUtils;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
 */
public class TraceTestDatasource extends AbstractDataSource {
    private final DruidDataSource originalDataSource;
    private final DruidDataSource traceTestDataSource;

    public TraceTestDatasource(DruidDataSource originalDataSource, DruidDataSource traceTestDataSource) {
        this.originalDataSource = originalDataSource;
        this.traceTestDataSource = traceTestDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return determineTargetDatasource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineTargetDatasource().getConnection(username, password);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return determineTargetDatasource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || determineTargetDatasource().isWrapperFor(iface);
    }

    protected DataSource determineTargetDatasource() {
        if (TraceTestUtils.isTraceTest()) {
            TraceTestUtils.info("this is trace test datasource: {}", traceTestDataSource.getName());
            return traceTestDataSource;
        } else {
            return originalDataSource;
        }
    }
}