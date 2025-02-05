package com.tcs.ion.icamera.cctv.error;

import java.io.Serial;

public class OnvifException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L; // Added explicit serialVersionUID for serialization

    public OnvifException() {
        this(null, null);
    }

    public OnvifException(String message) {
        this(message, null);
    }

    public OnvifException(String message, Throwable cause) {
        super(message, cause);
    }

    public OnvifException(Throwable cause) {
        this(null, cause);
    }
}