package com.gengzc.util.exception;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gengzc.util.OperationResult;

@Controller
@RequestMapping({"/exception"})
public class ExceptionController
{
  public static final long ERROR_SPRING_TYPE_MISMATCH = 10400L;
  public static final long ERROR_SPRING_UNKNOW = 10401L;
  public static final long ERROR_SPRING_UNKNOWURL = 10404L;
  private static final Log LOG = LogFactory.getLog(ExceptionController.class);

  @RequestMapping({"/error"})
  @ResponseBody
  public OperationResult getError(HttpServletRequest request, HttpServletResponse response)
    throws ServletException
  {
    return doError(request);
  }

  private OperationResult doError(HttpServletRequest request)
  {
    OperationResult result = new OperationResult();
    try
    {
      Object excep = request.getAttribute("exception");
      LOG.debug("公共异常所在的模块：" + request.getContextPath());
      if (excep == null) {
        result.setMsgCode(String.valueOf(10404L));
        result.setResData(null);
      }
      else if (TypeMismatchException.class.isAssignableFrom(excep.getClass())) {
        result.setMsgCode(String.valueOf(10400L));
        TypeMismatchException exception = (TypeMismatchException)excep;
        Throwable root = null;
        if (exception.getRootCause() == null)
          root = exception;
        else {
          root = exception.getRootCause();
        }
        LOG.debug(root.getMessage());
        String message = root.getMessage().split("\n")[0];
        result.setResData(message);
      } else if (HttpRequestMethodNotSupportedException.class.isAssignableFrom(excep.getClass()))
      {
        result.setMsgCode(String.valueOf(10400L));
        HttpRequestMethodNotSupportedException exception = (HttpRequestMethodNotSupportedException)excep;
        Throwable root = null;
        if (exception.getRootCause() == null)
          root = exception;
        else {
          root = exception.getRootCause();
        }
        LOG.debug(root.getMessage());
        String message = root.getMessage().split("\n")[0];
        result.setResData(message);
      } else if (MissingServletRequestParameterException.class.isAssignableFrom(excep.getClass()))
      {
        result.setMsgCode(String.valueOf(10400L));
        MissingServletRequestParameterException exception = (MissingServletRequestParameterException)excep;
        Throwable root = null;
        if (exception.getRootCause() == null)
          root = exception;
        else {
          root = exception.getRootCause();
        }
        LOG.debug(root.getMessage());
        String message = root.getMessage().split("\n")[0];
        result.setResData(message);
      } else if (HttpMessageNotReadableException.class.isAssignableFrom(excep.getClass()))
      {
        result.setMsgCode(String.valueOf(10400L));
        HttpMessageNotReadableException exception = (HttpMessageNotReadableException)excep;
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
        Exception exception = (Exception)excep;
        LOG.debug("处理用户请求中出现异常：" + exception.getMessage());
        LOG.debug("出现的异常：" + excep.getClass());
        result.setMsgCode(String.valueOf(10401L));
        result.setResData(exception.getMessage());
      }
    }
    catch (Exception e) {
      LOG.error("公共异常所在的模块：" + request.getContextPath(), e);
    }
    result.setFlag(false);
    return result;
  }
}
