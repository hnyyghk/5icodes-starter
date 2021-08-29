package com._5icodes.starter.webmvc;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.HttpConstraintElement;
import javax.servlet.HttpMethodConstraintElement;
import javax.servlet.Servlet;
import javax.servlet.ServletSecurityElement;
import javax.servlet.annotation.ServletSecurity;
import java.util.Collections;
import java.util.List;

@Configuration
@ConditionalOnClass({Servlet.class, Server.class, Loader.class, WebAppContext.class})
@ConditionalOnMissingBean(ServletWebServerFactory.class)
public class DisableJettyTraceAutoConfiguration {
    @Bean
    public ServletWebServerFactory disableTraceWebServerFactory() {
        return new JettyServletWebServerFactory() {
            @Override
            protected void postProcessWebAppContext(WebAppContext webAppContext) {
                HttpConstraintElement disable = new HttpConstraintElement(ServletSecurity.EmptyRoleSemantic.DENY);
                HttpMethodConstraintElement trace = new HttpMethodConstraintElement("TRACE", disable);
                ServletSecurityElement sse = new ServletSecurityElement(Collections.singleton(trace));
                List<ConstraintMapping> mappings = ConstraintSecurityHandler.createConstraintsWithMappingsForPath("disable", "/*", sse);
                ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
                csh.setConstraintMappings(mappings);
                webAppContext.setSecurityHandler(csh);
            }
        };
    }
}