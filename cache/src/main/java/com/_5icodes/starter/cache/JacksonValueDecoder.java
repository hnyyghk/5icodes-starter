package com._5icodes.starter.cache;

import com._5icodes.starter.common.utils.JsonUtils;
import com.alicp.jetcache.support.AbstractValueDecoder;

public class JacksonValueDecoder extends AbstractValueDecoder {
    public static final JacksonValueDecoder INSTANCE = new JacksonValueDecoder(true);

    public JacksonValueDecoder(boolean useIdentityNumber) {
        super(useIdentityNumber);
    }

    @Override
    public Object doApply(byte[] buffer) {
        if (useIdentityNumber) {
            byte[] bs = new byte[buffer.length - 4];
            System.arraycopy(buffer, 4, bs, 0, bs.length);
            return JsonUtils.parseBytes(bs);
        } else {
            return JsonUtils.parseBytes(buffer);
        }
    }
}