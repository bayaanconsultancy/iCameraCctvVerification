package com.cs.on.icamera.cctv.onvif;

public class OnvifException extends Exception {

	public OnvifException() {
		super();
	}

	public OnvifException(String message) {
		super(message);
	}

	public OnvifException(String message, Throwable cause) {
		super(message, cause);
	}

	public OnvifException(Throwable cause) {
		super(cause);
	}
}
