package com.gengzc.util.exception;

public class CloudRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CloudRuntimeException(String message) {
		super(message);
	}

	public CloudRuntimeException(String message, Throwable th) {
		super(message, th);
	}

	protected CloudRuntimeException() {
	}
}