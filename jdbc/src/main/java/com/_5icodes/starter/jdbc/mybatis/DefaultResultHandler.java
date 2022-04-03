package com._5icodes.starter.jdbc.mybatis;

import com._5icodes.starter.jdbc.exception.ResultSetTooBigException;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;

public class DefaultResultHandler implements ResultHandler<Object> {
    int maxResultSet = 1000;

    private final List<Object> list;

    public DefaultResultHandler() {
        list = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public DefaultResultHandler(ObjectFactory objectFactory) {
        list = objectFactory.create(List.class);
    }

    @Override
    public void handleResult(ResultContext<?> context) {
        list.add(context.getResultObject());
        if (list.size() > maxResultSet) {
            throw new ResultSetTooBigException("ResultSet size exceed " + maxResultSet);
        }
    }

    public List<Object> getResultList() {
        return list;
    }
}