package com.gengzc.service.gzcService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gengzc.bean.Account;
import com.gengzc.dao.propertyFilterDao.HibernateDao;
import com.gengzc.dao.propertyFilterDao.PropertyFilter;
import com.gengzc.util.ErrorCode.ControllerErr;
import com.gengzc.util.exception.CloudBusinessException;

@Service("loginService")
public class LoginServiceImpl implements LoginService{

	private static Log log = LogFactory.getLog(LoginServiceImpl.class);
	
	@Autowired
	private HibernateDao<Account, String> loginDao;
	
	@Override
	public Account findByCondition(String username, String password)
			throws CloudBusinessException {
		
		String para1 = "username_EQ_" + username;
		String para2 = "password_EQ_" + password;
		
		PropertyFilter pro1 = PropertyFilter.getPropertyFilter(para1);
		PropertyFilter pro2 = PropertyFilter.getPropertyFilter(para2);
		
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		filters.add(pro1);
		filters.add(pro2);
		try{
			List<Account> listInfo = loginDao.find(filters);
			if(listInfo.size() >= 1){
				return  listInfo.get(0);
			}
			return null;
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new CloudBusinessException(
					ControllerErr.ERROR_GZC_DATABASE_ERROR.toString(), e.getMessage(), e);
		}
		
	}

}
