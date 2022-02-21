package com._5icodes.starter.rocketmq.interceptor;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

public interface MessageInterceptor {
    /**
     * Invoked before the Message is actually sent to the channel.
     * This allows for modification of the Message if necessary.
     * If this method returns {@code null} then the actual
     * send invocation will not occur.
     */
    default Collection<Message> preSend(Collection<Message> messages) {
        return messages;
    }

    /**
     * Invoked immediately after the send invocation. The boolean
     * value argument represents the return value of that invocation.
     */
    default void postSend(Collection<Message> messages, SendResult sendResult) {
    }

    /**
     * Invoked after the completion of a send regardless of any exception that
     * have been raised thus allowing for proper resource cleanup.
     * <p>Note that this will be invoked only if {@link #preSend} successfully
     * completed and returned a Message, i.e. it did not return {@code null}.
     */
    default void afterSendCompletion(Collection<Message> messages, SendResult sendResult, @Nullable Exception ex) {
    }

    /**
     * Invoked immediately after a List of Messages has been retrieved but before
     * They are returned to the caller. The Messages may be modified if
     * necessary; {@code null} aborts further interceptor invocations.
     */
    @Nullable
    default List<MessageExt> preConsume(List<MessageExt> msgs) {
        return msgs;
    }

    /**
     * Invoked after the completion of consume regardless of any exception that
     * have been raised thus allowing for proper resource cleanup.
     * <p>Note that this will be invoked only if {@link #preConsume} successfully
     * completed and returned not {@code null}.
     *
     * @since 4.1
     */
    default void afterConsumeCompletion(List<MessageExt> msgs, @Nullable Exception ex) {
    }
}