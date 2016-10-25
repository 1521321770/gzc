package com.gengzc.controller.propertyFilter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gengzc.bean.Account;
import com.gengzc.dao.accountDao.AccountDaoImpl;
import com.gengzc.dao.propertyFilterDao.PropertyFilter;
import com.gengzc.service.accountService.AccountServiceImpl;

@Controller
@RequestMapping("/geng")
public class PropertyFilterController {
	
	private final Log log = LogFactory.getLog(PropertyFilterController.class);
	
	@Autowired
	private AccountDaoImpl accountDao;
	
	@Autowired
	private AccountServiceImpl accountService;
	
	@ResponseBody
	@RequestMapping(value = "/property.do", method = RequestMethod.POST)  
    public String hello(HttpServletRequest request,
//    		@RequestBody  PropertyFilterBody propertyFilterBody,
    		@RequestParam(value = "propertySQL") String propertySQL
    		) throws Exception {  

		System.out.println(propertySQL);
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
		
//        PropertyFilter pro = new PropertyFilter("username", MatchType.EQ, FieldType.S, "geng");
//        PropertyFilter pro1 = new PropertyFilter("password", MatchType.EQ, "123");
        String para1 = "username_EQ_wang";
        String para2 = "password_EQ_S_123";
//        String para3 = "password_BETWEEN_S_123_456";
//        String para4 = "password_BETWEEN_123_456";
//        String para5 = "id_fileId_EQ";
        String key = "filter_EQ_id";
        String value = "123321";
        
        
        
        PropertyFilter pro2 = PropertyFilter.getPropertyFilter(para2);
        PropertyFilter pro1 = PropertyFilter.getPropertyFilter(para1);
        
        List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
        filters.add(pro1);
        filters.add(pro2);
        for(PropertyFilter p:filters){
        	System.out.println(p.getFieldName());
        	System.out.println(p.getClass());
        	System.out.println(p.getMatchType());
        	for(Object o:p.getValues()){
        		System.out.println(String.valueOf(o));
        	}
        	System.out.println(p.getOtherField());
        }
        List<Account> list = accountService.find(filters);
        for(Account a:list){
        	System.out.println(a.toString());
        }
        
        log.info("----»’÷æΩ· ¯----");
        return "zzzzzzzzzzz";
	  
    }  
}
