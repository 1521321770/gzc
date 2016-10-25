package com.gengzc.dao.accountDao;

import com.gengzc.bean.Account;

public interface AccountDao {  

    Account findByName(String username);  

    Account findById(String id);  

}