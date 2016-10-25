package com.gengzc.dao.accountDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.gengzc.bean.Account;
import com.gengzc.dao.propertyFilterDao.HibernateDao;

@Repository("accountDao")
public class AccountDaoImpl extends HibernateDao<Account, String> implements AccountDao {  


	@Autowired
	private SessionFactory sessionFactory;

//	public Session getSession(){
//		System.out.println("sessionFactory:" + sessionFactory);
//		return sessionFactory.getCurrentSession();
//	}

	@Override  
	@SuppressWarnings("unchecked")
    public Account findByName(String username){
        String sql = "from Account where username = ?";
        Session session = sessionFactory.openSession();
        Query query = session.createQuery(sql);
        query.setParameter(0, username);
        List<Account> accountList = query.list();
        session.close();
        return accountList.get(0);
    }

	@Override
    @SuppressWarnings("unchecked")
    public Account findById(String id) {
        String sql = "SELECT * From account WHERE id = ?";
        Session session = getSession();
        Query query = session.createQuery(sql);
        query.setParameter(0, id);
        List<Account> accountList = query.list();
        return accountList.get(0);
    }

    @SuppressWarnings("rawtypes")
	protected RowMapper accountRowMap = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Account account = new Account();
            account.setUsername(rs.getString("username"));
            account.setPassword(rs.getString("password"));
            account.setBirthday(rs.getDate("birthday"));
            account.setEmail(rs.getString("email"));
            return account;
        }
    };
}