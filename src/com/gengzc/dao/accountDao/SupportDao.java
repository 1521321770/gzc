package com.gengzc.dao.accountDao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.gengzc.bean.Account;
import com.gengzc.dao.hibernateDaoSupport.BaseHibernateDao;
import com.gengzc.util.PageListBean;
import com.gengzc.util.exception.CloudDBException;

public class SupportDao extends BaseHibernateDao<Account, String> {
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int createSQLQuery() {
		int totalNum = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(final Session session){
				Query q = session.createSQLQuery("from Student where id = ?");
				q.setParameter(0, "1");
				return q.list();
			}
		});
		return totalNum;
	}
	
	@SuppressWarnings("rawtypes")
	public Serializable save(List<Account> entities, Account entity) throws CloudDBException {
		try {
			/**
			 * id 是主键的值
			 */
			java.util.List temp = getHibernateTemplate().find("sql");
			Serializable id = getHibernateTemplate().save(entity);
			id = getHibernateTemplate().save(Account.class.toString(), entity);
			getHibernateTemplate().saveOrUpdate(entity);
			getHibernateTemplate().saveOrUpdate(entity.getClass().toString(), entity);
			getHibernateTemplate().saveOrUpdateAll(entities);
			getHibernateTemplate().saveOrUpdateAll(temp);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CloudDBException(e.toString(), e);
		}

	}
	
	public void update(List<Account> entities, Account entity, LockMode lockMode) throws CloudDBException {
		try {
			getHibernateTemplate().update(entity);
			getHibernateTemplate().update(entity, lockMode);
			getHibernateTemplate().update(entity.getClass().toString(), entity);
			getHibernateTemplate().update(Account.class.toString(), entity, lockMode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CloudDBException(e.toString(), e);
		}

	}
	
	@SuppressWarnings("rawtypes")
	public void delete(List<Account> entities, Account account, String sql, LockMode lockMode) throws CloudDBException {
		java.util.List temp = getHibernateTemplate().find(sql);
		try {
			getHibernateTemplate().delete(account, lockMode);
			getHibernateTemplate().flush(); // 立即刷新，否则锁不会生效。

			getHibernateTemplate().delete("username", account);
			getHibernateTemplate().delete("username", account, lockMode);
			getHibernateTemplate().flush(); // 立即刷新，否则锁不会生效。

			getHibernateTemplate().delete(account);
			getHibernateTemplate().deleteAll(temp);
			getHibernateTemplate().deleteAll(entities);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CloudDBException(e.toString(), e);
		}

	}
		@SuppressWarnings("unchecked")
	public List<Account> find(String hqlkey, Object[] values, PageListBean<Account> pageListBean) throws CloudDBException{
		try {
			List<Account> list = null;
			String queryString = null;
			String sql1 = "FROM orderlineitem in class OrderLineItem WHERE "
					+ "orderlineitem.id = ?";
			String sql2 = "FROM orderlineitem in class OrderLineItem WHERE "
					+ "orderlineitem.id = ? and username = ?";
			String id = "123";
			String username = "geng";
			DetachedCriteria criteria = DetachedCriteria.forClass(Account.class);
			criteria.add(Restrictions.eq("id", id));
			
			list = getHibernateTemplate().find(sql1);
			list = getHibernateTemplate().find(sql2, id);
			list = getHibernateTemplate().find(sql2, id, username);
			list = getHibernateTemplate().findByCriteria(criteria);
			list = getHibernateTemplate().findByCriteria(criteria, pageListBean.getCurrentPage(), pageListBean.getPageSize());
			
			Account account = new Account();
			account.setId("123");			
			list = getHibernateTemplate().findByExample(account);
			list = getHibernateTemplate().findByExample(Account.class.toString(), account);
			list = getHibernateTemplate().findByExample(account, pageListBean.getCurrentPage(), pageListBean.getPageSize());
			list = getHibernateTemplate().findByExample(Account.class.toString(), account, pageListBean.getCurrentPage(), pageListBean.getPageSize());
			
			queryString = "select count(*) from bean.User u where u.name=:myName";
	        String paramName= "myName";
	        String value= "xiyue";
			list = getHibernateTemplate().findByNamedParam(queryString, paramName, value);
			
			queryString = "select count(*) from bean.User u where u.name=:myName and u.password=:myPassword";
		    String[] paramNames= new String[]{"myName", "myPassword"};
		    String[] values1= new String[]{"xiyue", "123"};
			list = getHibernateTemplate().findByNamedParam(queryString, paramNames, values1);
			
			list = getHibernateTemplate().findByNamedQuery("queryAllAccount");
			
			list = getHibernateTemplate().findByNamedQuery("queryByUsername", "username");
			
			Object[] values2 = new String[]{"username", "password"};
			list = getHibernateTemplate().findByNamedQuery("queryByUsername_Password", values2);
			
			list = getHibernateTemplate().findByNamedQueryAndNamedParam("queryByUsernameAndNamedParam", "username", "123");
			
			String[] names= new String[]{"username", "password"};
            String[] values3= new String[]{"test", "123"};
			list = getHibernateTemplate().findByNamedQueryAndNamedParam("queryByUsername_passwordAndNamedParam", names, values3);
			
//			list = getHibernateTemplate().findByNamedQueryAndValueBean(queryName, valueBean);
//			list = getHibernateTemplate().findByValueBean(queryString, valueBean);
			
			return list;
		} catch (Exception e) {			
			e.printStackTrace();
			throw new CloudDBException(e.toString(), e);
		}
	}
	
	public void get() throws CloudDBException{
		try{
			String id = "235sdf34rd";
			getHibernateTemplate().get(Account.class, id);
			getHibernateTemplate().get(Account.class.toString(), id);
			getHibernateTemplate().get(Account.class, id, LockMode.READ);
			getHibernateTemplate().get(Account.class.toString(), id, LockMode.UPGRADE_NOWAIT);
		}catch(Exception e){
			e.printStackTrace();
			throw new CloudDBException(e.toString(), e);
		}
	}
	
}
