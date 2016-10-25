package com.gengzc.service.gzcService;

import com.gengzc.bean.Account;
import com.gengzc.util.exception.CloudBusinessException;

public interface LoginService {
	
	Account findByCondition(String username, String password) throws CloudBusinessException;
}
