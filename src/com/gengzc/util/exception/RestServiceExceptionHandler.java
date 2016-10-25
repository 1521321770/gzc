package com.gengzc.util.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import com.gengzc.util.OperationResult;

public class RestServiceExceptionHandler extends
		DefaultHandlerExceptionResolver {
	protected static final Log LOGGER = LogFactory
			.getLog(RestServiceExceptionHandler.class);
	private RestErrorResolver errorResolver;

	public boolean isRestServiceException(HttpServletRequest request) {
		return true;
	}

	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		if (isRestServiceException(request)) {
			LOGGER.error("catch restful error:" + ex);
			OperationResult error = this.errorResolver.resolveError(request,
					handler, ex);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonGenerator jsonGenerator = mapper.getJsonFactory()
						.createJsonGenerator(response.getOutputStream(),
								JsonEncoding.UTF8);

				mapper.writeValue(jsonGenerator, error);
			} catch (JsonGenerationException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return new ModelAndView();
		}
		return super.doResolveException(request, response, handler, ex);
	}

	public RestErrorResolver getErrorResolver() {
		return this.errorResolver;
	}

	public void setErrorResolver(RestErrorResolver errorResolver) {
		this.errorResolver = errorResolver;
	}
}