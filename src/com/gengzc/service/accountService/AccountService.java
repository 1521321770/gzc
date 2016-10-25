package com.gengzc.service.accountService;

import java.util.List;

import com.gengzc.bean.Account;
import com.gengzc.dao.propertyFilterDao.PropertyFilter;

public interface AccountService {  
	  
    boolean findVerify(String username, String password);  
    
    Account findRead(String id); 
    
    List<Account> find(List<PropertyFilter> filters);
  
    Account findRead(String username, String password);  
}  