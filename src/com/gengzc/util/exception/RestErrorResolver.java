package com.gengzc.util.exception;

import javax.servlet.http.HttpServletRequest;

import com.gengzc.util.OperationResult;

public abstract interface RestErrorResolver {
	public abstract OperationResult resolveError(
			HttpServletRequest paramHttpServletRequest, Object paramObject,
			Exception paramException);
}