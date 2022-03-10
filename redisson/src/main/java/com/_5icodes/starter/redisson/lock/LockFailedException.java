package com._5icodes.starter.redisson.lock;

public class LockFailedException extends RuntimeException {
    public LockFailedException(String message) {
        super(message);
    }

    public LockFailedException(Throwable cause) {
        super(cause);
    }
}