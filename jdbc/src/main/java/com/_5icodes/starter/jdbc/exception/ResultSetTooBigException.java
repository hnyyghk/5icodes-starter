package com._5icodes.starter.jdbc.exception;

public class ResultSetTooBigException extends RuntimeException {
    public ResultSetTooBigException(String message) {
        super(message);
    }
}