package com.gengzc.service.util;

import java.util.UUID;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import com.gengzc.bean.Model;
import com.gengzc.dao.util.DBDao;
import com.gengzc.util.Key;

public class DBService {

	/**
	 * baseDao object.
	 */
	@Autowired
	private DBDao<Model, String> dbDao;

	public void defaultDB() {
		try {
//			Model model = new Model("actual_name", 1);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("software_id", 2);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("description", 3);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("size", 4);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("deploy_type", 5);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("category", 6);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("file_id", 7);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("level_type", 8);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("file_type", 9);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("domain_id", 10);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("org_id", 11);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("name", 12);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("user_id", 13);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("type_id", 14);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("flag", 15);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("create_time", 16);
//			dbDao.getHibernateTemplate().save(model);
//			model = new Model("location", 17);
//			dbDao.getHibernateTemplate().save(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveSoftware() {
		try {
			String uuid = Key.getUUID();
			Session session = dbDao.getHibernateTemplate().getSessionFactory().getCurrentSession();
			Transaction t = session.beginTransaction();
			t.begin();
			String actual_value = "icm.war";
			String sql = "insert into model_software(uuid, map_value, actual_value)"
					+ " values ('" + uuid + "', 1, '" + actual_value + "')";
			Query query = session.createSQLQuery(sql);
			query.executeUpdate();
			t.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
