package com._5icodes.starter.cache;

import com._5icodes.starter.common.utils.JsonUtils;
import com.alicp.jetcache.support.AbstractValueEncoder;
import com.alicp.jetcache.support.CacheEncodeException;

public class JacksonValueEncoder extends AbstractValueEncoder {
    public static final JacksonValueEncoder INSTANCE = new JacksonValueEncoder(true);

    protected static int IDENTITY_NUMBER = 0x4A953A83;

    public JacksonValueEncoder(boolean useIdentityNumber) {
        super(useIdentityNumber);
    }

    @Override
    public byte[] apply(Object value) {
        try {
            byte[] bs1 = JsonUtils.toJsonBytes(value);
            if (useIdentityNumber) {
                byte[] bs2 = new byte[bs1.length + 4];
                writeHeader(bs2, IDENTITY_NUMBER);
                System.arraycopy(bs1, 0, bs2, 4, bs1.length);
                return bs2;
            } else {
                return bs1;
            }
        } catch (Exception e) {
            throw new CacheEncodeException("jackson encode error:", e);
        }
    }
}