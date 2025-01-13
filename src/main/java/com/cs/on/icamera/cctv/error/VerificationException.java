package com.cs.on.icamera.cctv.error;

public class VerificationException extends Exception {

	public VerificationException() {
		super();
	}

	public VerificationException(String message) {
		super(message);
	}

	public VerificationException(String message, Throwable cause) {
		super(message, cause);
	}

	public VerificationException(Throwable cause) {
		super(cause);
	}
}
