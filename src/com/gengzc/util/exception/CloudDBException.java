package com.gengzc.util.exception;

public class CloudDBException extends Exception
{
	private static final long serialVersionUID = 1L;

	public CloudDBException(String msg, Throwable cause)
	{
		super(msg, cause);
	}

	public CloudDBException(String msg)
	{
		super(msg);
	}
}