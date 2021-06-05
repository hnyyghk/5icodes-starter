package com._5icodes.starter.feign.encoder;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.List;

public class CustomEncoderContainer implements Encoder, InitializingBean {
    @Autowired(required = false)
    private List<CustomEncoder> customEncoders;

    private final Encoder delegate;

    public CustomEncoderContainer(Encoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (null == object) {
            return;
        }
        if (!CollectionUtils.isEmpty(customEncoders)) {
            for (CustomEncoder customEncoder : customEncoders) {
                boolean encode = customEncoder.encode(object, bodyType, template);
                if (encode) {
                    return;
                }
            }
        }
        delegate.encode(object, bodyType, template);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!CollectionUtils.isEmpty(customEncoders)) {
            AnnotationAwareOrderComparator.sort(customEncoders);
        }
    }
}