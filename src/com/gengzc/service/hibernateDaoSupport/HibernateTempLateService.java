package com.gengzc.service.hibernateDaoSupport;

import java.io.Serializable;
import java.util.List;

import com.gengzc.bean.Account;
import com.gengzc.dao.accountDao.SupportDao;
import com.gengzc.util.exception.CloudDBException;

public class HibernateTempLateService {


	private SupportDao supportDao;

	public Serializable saveOrUpdate(Account account) throws CloudDBException{
		try {
			/**
			 * id ÊÇÖ÷¼ü
			 */
			Serializable id = supportDao.save(account);
			supportDao.saveOrUpdate(account);
			return id;
		} catch (CloudDBException e) {
			e.printStackTrace();
			throw new CloudDBException(e.toString(), e);
		}
	}

	public void update(Account account) {
		try {
			supportDao.update(account);
		} catch (CloudDBException e) {
			e.printStackTrace();
		}
	}


	public void delete(List<Account> collection, Account account) throws CloudDBException {
		try {
			String sql = "FROM orderlineitem in class OrderLineItem WHERE "
					+ "orderlineitem.orderId =" + "id";
			String id = "123";
			supportDao.delete(account);
			supportDao.delete(Account.class, id);
			supportDao.deleteAll(collection);
			supportDao.deleteBySql(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CloudDBException(e.toString(), e);
		}
	}

}
