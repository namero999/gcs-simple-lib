package com.creonomy.gcslib;

public class GCSException extends RuntimeException {

    public GCSException(String message) {
        super(message);
    }

    public GCSException(String message, Throwable cause) {
        super(message, cause);
    }

}