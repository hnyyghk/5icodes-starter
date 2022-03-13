package com._5icodes.starter.async.consumer;

import com._5icodes.starter.async.AsyncContext;
import com._5icodes.starter.async.codec.AsyncCodec;
import com._5icodes.starter.async.delay.AsyncSerializableObj;
import com._5icodes.starter.async.delay.MethodInfo;
import com._5icodes.starter.async.operations.AsyncOperations;
import com._5icodes.starter.common.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class AsyncRocketmqMessageListener {
    private final AsyncCodec asyncCodec;
    private final AsyncOperations asyncOperations;

    public AsyncRocketmqMessageListener(AsyncCodec asyncCodec, AsyncOperations asyncOperations) {
        this.asyncCodec = asyncCodec;
        this.asyncOperations = asyncOperations;
    }

    public boolean consumeMessage(List<MessageExt> msgs) {
        int size = msgs.size();
        if (size != 1) {
            log.error("async msg batch size is {}, should be 1", size);
            return false;
        }
        try {
            Object message = asyncCodec.decode(msgs.get(0).getBody());
            if (!(message instanceof AsyncSerializableObj)) {
                log.error("async consume an object with type: {}, value: {}", message.getClass(), message);
                return false;
            }
            AsyncSerializableObj obj = (AsyncSerializableObj) message;
            AsyncContext context = obj.getContext();
            MethodInfo methodInfo = obj.getMethodInfo();
            Method method = methodInfo.getDeclaringClass().getMethod(methodInfo.getMethodName(), methodInfo.getParameterTypes());
            context.setTarget(SpringUtils.getBean(context.getBeanName()));
            context.setMethod(method);
            asyncOperations.runInCallback(context);
            return true;
        } catch (Throwable e) {
            log.error("async consumeMessage error", e);
            return false;
        }
    }
}