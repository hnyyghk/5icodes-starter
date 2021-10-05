package com._5icodes.starter.swagger;

import com._5icodes.starter.webmvc.result.CodeMsgResponseBodyAdvice;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.InitializingBean;
import springfox.documentation.swagger.web.ApiResourceController;
import springfox.documentation.swagger2.web.Swagger2Controller;

public class CodeMsgSwaggerSwitcher implements InitializingBean {
    private final CodeMsgResponseBodyAdvice advice;

    public CodeMsgSwaggerSwitcher(CodeMsgResponseBodyAdvice advice) {
        this.advice = advice;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        advice.addExcludeClasses(Sets.newHashSet(Swagger2Controller.class.getName(), ApiResourceController.class.getName()));
    }
}