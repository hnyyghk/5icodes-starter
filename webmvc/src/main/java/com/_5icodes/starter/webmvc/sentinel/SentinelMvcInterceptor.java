package com._5icodes.starter.webmvc.sentinel;

import com._5icodes.starter.web.WebConstants;
import com._5icodes.starter.webmvc.common.OnlyOnceInterceptorConfigurer;
import com._5icodes.starter.webmvc.common.RequestMappingRegister;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.AbstractSentinelInterceptor;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor
 * @see com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot#entry
 * @see com.alibaba.csp.sentinel.slots.block.flow.FlowRuleChecker#checkFlow
 * @see <a href="https://blog.csdn.net/gaoliang1719/article/details/116335077">针对调用来源的限流</a>
 */
@Slf4j
public class SentinelMvcInterceptor implements OnlyOnceInterceptorConfigurer, Ordered {
    private final RequestMappingRegister register;

    public SentinelMvcInterceptor(RequestMappingRegister register) {
        this.register = register;
    }

    @Override
    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            String key = register.getSentinelKey((HandlerMethod) handler);
            if (key != null) {
                try {
                    // Parse the request origin using registered origin parser.
                    String origin = parseOrigin(request);
                    ContextUtil.enter(AbstractSentinelInterceptor.SENTINEL_SPRING_WEB_CONTEXT_NAME, origin);
                    SphU.entry(key, EntryType.IN);
                } catch (BlockException e) {
                    log.warn("{} is blocked with {}", key, e.getClass().getSimpleName());
                    throw e;
                }
            }
        }
        return true;
    }

    @Override
    public void doAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        exitSentinelContext();
    }

    private void exitSentinelContext() {
        while (ContextUtil.getContext() != null && ContextUtil.getContext().getCurEntry() != null) {
            ContextUtil.getContext().getCurEntry().exit();
        }
        ContextUtil.exit();
    }

    protected String parseOrigin(HttpServletRequest request) {
        String moduleId = request.getHeader(WebConstants.MODULE_ID);
        String preModuleId = request.getHeader(WebConstants.PRE_MODULE_ID);
        String preReqUri = request.getHeader(WebConstants.PRE_REQ_URI);
        moduleId = StringUtils.hasText(moduleId) ? moduleId : "unknown";
        preModuleId = StringUtils.hasText(preModuleId) ? preModuleId : "unknown";
        preReqUri = StringUtils.hasText(preReqUri) ? preReqUri : "unknown";
        return moduleId + ":" + preModuleId + ":" + preReqUri;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}