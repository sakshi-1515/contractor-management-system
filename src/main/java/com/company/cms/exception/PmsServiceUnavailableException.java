package com.company.cms.exception;

public class PmsServiceUnavailableException extends RuntimeException{
    public PmsServiceUnavailableException(String message, Throwable cause){
        super(message, cause);
    }
}
