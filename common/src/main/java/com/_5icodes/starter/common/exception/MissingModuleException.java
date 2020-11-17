package com._5icodes.starter.common.exception;

import java.util.Set;

public class MissingModuleException extends RuntimeException {
    private final Set<String> missingModules;

    public MissingModuleException(Set<String> missingModules) {
        this.missingModules = missingModules;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("缺少必须模块,请在pom.xml文件中增加如下依赖:\n\n");
        for (String missingModule : missingModules) {
            sb.append("<dependency>\n");
            sb.append("  <groupId>com.5icodes</groupId>\n");
            sb.append("  <artifactId>").append(missingModule).append("</artifactId>\n");
            sb.append("</dependency>\n");
        }
        return sb.toString();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}