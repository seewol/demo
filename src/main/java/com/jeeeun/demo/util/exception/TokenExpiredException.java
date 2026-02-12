package com.jeeeun.demo.util.exception;

public class TokenExpiredException extends RuntimeException{

    //    public RuntimeException(String message) {
    //        super(message);
    //    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
