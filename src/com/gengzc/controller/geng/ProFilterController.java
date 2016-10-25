package com.gengzc.controller.geng;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gengzc.util.OperationResult;


@Controller
@RequestMapping("/gzc")
public class ProFilterController {
	
	private final Log log = LogFactory.getLog(ProFilterController.class);

	@ResponseBody
	@RequestMapping(value = "/property.do", method = RequestMethod.POST)  
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public OperationResult hello(HttpServletRequest request,
    		@RequestParam(value = "propertySQL") String propertySQL){  

		Map map = new HashMap();  
        Enumeration paramNames = request.getParameterNames();  
        while (paramNames.hasMoreElements()) {  
            String paramName = (String) paramNames.nextElement();  
  
            String[] paramValues = request.getParameterValues(paramName);  
            if (paramValues.length == 1) {  
                String paramValue = paramValues[0];  
                if (paramValue.length() != 0) {  
                    map.put(paramName, paramValue);  
                }  
            }  
        }  
  
        Set<Map.Entry<String, String>> set = map.entrySet();  
        System.out.println("------------------------------");  
        for (Map.Entry entry : set) {  
            System.out.println(entry.getKey() + ":" + entry.getValue());  
        }  
        System.out.println("------------------------------");  
		
        log.info("----»’÷æΩ· ¯----");
        
        OperationResult result = new OperationResult();
        String p = "22";
        result.setMsgCode("200");
        result.setFlag(true);
        result.setResData(p);
        
        return result;
	  
    }  
}
