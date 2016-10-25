package com.gengzc.controller.geng;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gengzc.bean.Account;
import com.gengzc.controller.common.Result;
import com.gengzc.service.gzcService.LoginService;
import com.gengzc.util.OperationResult;

@Controller
@RequestMapping("/gzc")
public class LoginController {
	
	private final Log log = LogFactory.getLog(LoginController.class);
	
	@Autowired
	private LoginService loginService;
	
	@ResponseBody
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public OperationResult upload(HttpServletRequest request,
    		@RequestParam(value = "username", required = true) String username,
    		@RequestParam(value = "password") String password){
		
        try {
        	Account account = loginService.findByCondition(username, password);
        	return Result.sendResult("200", true, account);
    	} catch(Exception e) {
    		e.printStackTrace();
    		log.error(e.getMessage(), e);
    		return Result.sendResult(e.getMessage(), false, e.getMessage());
    	}
    }
}
