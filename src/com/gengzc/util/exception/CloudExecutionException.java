package com.gengzc.util.exception;

public class CloudExecutionException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public CloudExecutionException(String msg, Throwable cause)
	{
		super(msg, cause);
	}

	public CloudExecutionException(String msg)
	{
		super(msg);
	}
}
