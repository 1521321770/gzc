package com.gengzc.service.accountService;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gengzc.bean.Account;
import com.gengzc.dao.accountDao.AccountDaoImpl;
import com.gengzc.dao.propertyFilterDao.PropertyFilter;
  
@Service("accountService")
public class AccountServiceImpl implements AccountService {  
  
	private static Log log = LogFactory.getLog(AccountServiceImpl.class);
	
    @Autowired  
    private AccountDaoImpl accountDao;  

    @Override  
    public boolean findVerify(String username, String password) {  
        Account account = accountDao.findByName(username);  
  
        if (password.equals(account.getPassword())) { 
        	String info = "信息处理中" + "用户名、密码正确" ;
        	log.debug(info);
            return true;  
        } else {  
        	String info = "信息处理中" + "用户名或密码错误" ;
        	log.debug(info);
            return false;  
        }  
    }  
    
    @Override  
    public Account findRead(String username, String password) {  
        Account account = null;  
        if (username.equals("admin") && password.equals("admin")) {  
            account = new Account();  
            account.setId("11111");  
            account.setUsername(username);  
            account.setPassword(password);  
        }  
        return account;  
    }  
  
    @Override  
    public Account findRead(String id) {  
        Account account = new Account();  
        account.setId("1");  
        account.setUsername("snowolf1111");  
        account.setPassword("zlex1111");  
        return account; 
    }

	@Override
	public List<Account> find(List<PropertyFilter> filters) {
		List<Account> list = accountDao.find(filters);
		return list;
	}
} 