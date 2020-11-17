package com._5icodes.starter.common.utils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

@UtilityClass
public class ExceptionUtils {
    public Throwable getRealException(Throwable throwable) {
        if (throwable instanceof UndeclaredThrowableException) {
            return getRealException((UndeclaredThrowableException) throwable);
        } else if (throwable instanceof InvocationTargetException) {
            return getRealException((InvocationTargetException) throwable);
        } else {
            return throwable;
        }
    }

    public Throwable getRealException(UndeclaredThrowableException e) {
        return getRealException(e.getUndeclaredThrowable());
    }

    public Throwable getRealException(InvocationTargetException e) {
        return getRealException(e.getTargetException());
    }
}