package com.accolite.app.exception;

public class SQLSyntaxErrorException extends RuntimeException {
    public SQLSyntaxErrorException(String msg) {
        super(msg);
    }
}
