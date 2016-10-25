package com.gengzc.util.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.gengzc.util.OperationResult;

public class DefaultRestErrorResolver implements RestErrorResolver,
		InitializingBean {
	private static final Log LOG = LogFactory
			.getLog(DefaultRestErrorResolver.class);
	public static final long ERROR_SPRING_TYPE_MISMATCH = 10400L;
	public static final long ERROR_SPRING_UNKNOW = 10401L;
	public static final long ERROR_SPRING_UNKNOWURL = 10404L;
	private Map<String, String> exceptionMappings = Collections.emptyMap();

	private Map<String, String> exceptionMappingDefinitions = Collections
			.emptyMap();

	public OperationResult resolveError(HttpServletRequest request,
			Object handler, Exception ex) {
		OperationResult error = buildError(ex, request);
		return error;
	}

	private OperationResult buildError(Exception excep,
			HttpServletRequest request) {
		OperationResult result = new OperationResult();
		try {
			if (TypeMismatchException.class.isAssignableFrom(excep.getClass())) {
				result.setMsgCode(String.valueOf(10400L));
				TypeMismatchException exception = (TypeMismatchException) excep;
				Throwable root = null;
				if (exception.getRootCause() == null)
					root = exception;
				else {
					root = exception.getRootCause();
				}
				LOG.debug(root.getMessage());
				String message = root.getMessage().split("\n")[0];
				result.setResData(message);
			} else if (HttpRequestMethodNotSupportedException.class
					.isAssignableFrom(excep.getClass())) {
				result.setMsgCode(String.valueOf(10400L));
				HttpRequestMethodNotSupportedException exception = (HttpRequestMethodNotSupportedException) excep;
				Throwable root = null;
				if (exception.getRootCause() == null)
					root = exception;
				else {
					root = exception.getRootCause();
				}
				LOG.debug(root.getMessage());
				String message = root.getMessage().split("\n")[0];
				result.setResData(message);
			} else if (MissingServletRequestParameterException.class
					.isAssignableFrom(excep.getClass())) {
				result.setMsgCode(String.valueOf(10400L));
				MissingServletRequestParameterException exception = (MissingServletRequestParameterException) excep;
				Throwable root = null;
				if (exception.getRootCause() == null)
					root = exception;
				else {
					root = exception.getRootCause();
				}
				LOG.debug(root.getMessage());
				String message = root.getMessage().split("\n")[0];
				result.setResData(message);
			} else if (HttpMessageNotReadableException.class
					.isAssignableFrom(excep.getClass())) {
				result.setMsgCode(String.valueOf(10400L));
				HttpMessageNotReadableException exception = (HttpMessageNotReadableException) excep;
				Throwable root = null;
				if (exception.getRootCause() == null)
					root = exception;
				else {
					root = exception.getRootCause();
				}
				LOG.debug(root.getMessage());
				String message = root.getMessage().split("\n")[0];
				result.setResData(message);
			} else {
				Exception exception = excep;
				LOG.debug("处理用户请求中出现异常：" + exception.getMessage());
				LOG.error("请求出现的异常：" + excep.getClass(), excep);
				result.setMsgCode(String.valueOf(10401L));
				result.setResData(exception.getMessage());
			}
		} catch (Exception e) {
			LOG.error("公共异常所在的模块：" + request.getContextPath(), e);
		}
		result.setFlag(false);
		return result;
	}

	public void afterPropertiesSet() throws Exception {
		this.exceptionMappings = createDefaultExceptionMappingDefinitions();

		if ((this.exceptionMappingDefinitions != null)
				&& (!this.exceptionMappingDefinitions.isEmpty()))
			this.exceptionMappings.putAll(this.exceptionMappingDefinitions);
	}

	private Map<String, String> createDefaultExceptionMappingDefinitions() {
		Map m = new LinkedHashMap();

		applyDef(m, HttpMessageNotReadableException.class,
				HttpStatus.BAD_REQUEST);
		applyDef(m, MissingServletRequestParameterException.class,
				HttpStatus.BAD_REQUEST);
		applyDef(m, TypeMismatchException.class, HttpStatus.BAD_REQUEST);
		applyDef(m, "javax.validation.ValidationException",
				HttpStatus.BAD_REQUEST);

		applyDef(m, NoSuchRequestHandlingMethodException.class,
				HttpStatus.NOT_FOUND);
		applyDef(m, "org.hibernate.ObjectNotFoundException",
				HttpStatus.NOT_FOUND);

		applyDef(m, HttpRequestMethodNotSupportedException.class,
				HttpStatus.METHOD_NOT_ALLOWED);

		applyDef(m, HttpMediaTypeNotAcceptableException.class,
				HttpStatus.NOT_ACCEPTABLE);

		applyDef(m, "org.springframework.dao.DataIntegrityViolationException",
				HttpStatus.CONFLICT);

		applyDef(m, HttpMediaTypeNotSupportedException.class,
				HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		return m;
	}

	private void applyDef(Map<String, String> m, Class clazz, HttpStatus status) {
		applyDef(m, clazz.getName(), status);
	}

	private void applyDef(Map<String, String> m, String key, HttpStatus status) {
		m.put(key, status.value() + "");
	}

	public Map<String, String> getExceptionMappings() {
		return this.exceptionMappings;
	}

	public void setExceptionMappings(Map<String, String> exceptionMappings) {
		this.exceptionMappings = exceptionMappings;
	}

	public Map<String, String> getExceptionMappingDefinitions() {
		return this.exceptionMappingDefinitions;
	}

	public void setExceptionMappingDefinitions(
			Map<String, String> exceptionMappingDefinitions) {
		this.exceptionMappingDefinitions = exceptionMappingDefinitions;
	}

}
