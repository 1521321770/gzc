package com.gengzc.dao.hibernateDaoSupport;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.gengzc.util.exception.CloudDBException;

public class BaseHibernateDao<T, PK extends Serializable> extends
		HibernateDaoSupport implements Dao<T, PK> {
	private static final Log LOG = LogFactory.getLog(BaseHibernateDao.class);
	
	@SuppressWarnings("unchecked")
	Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	public void flush() throws CloudDBException {
		try {
			getHibernateTemplate().flush();
			LOG.debug("flush successful");
		} catch (Exception re) {
			LOG.error("flush failed", re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public void saveOrUpdate(T object) throws CloudDBException {
		try {
			getHibernateTemplate().saveOrUpdate(object);
			LOG.debug("saveOrUpdate successful");
		} catch (Exception re) {
			LOG.error("saveOrUpdate failed", re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public void update(T object) throws CloudDBException {
		try {
			getHibernateTemplate().update(object);
			LOG.debug("update successful");
		} catch (Exception re) {
			LOG.error("update failed", re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public Serializable save(T object) throws CloudDBException {
		try {
			Serializable result = getHibernateTemplate().save(object);
			LOG.debug("save successful");
			return result;
		} catch (Exception re) {
			LOG.error("save failed", re);
			throw new CloudDBException(re.toString(), re);
		}

	}

	public void delete(T object) throws CloudDBException {
		LOG.debug("deleting instance");
		try {
			getHibernateTemplate().delete(object);
			LOG.debug("delete successful");
		} catch (Exception re) {
			LOG.error("delete failed", re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public void deleteAll(Collection<T> collection) throws CloudDBException {
		LOG.debug("deleteAll  instance");
		try {
			getHibernateTemplate().deleteAll(collection);
			LOG.debug("deleteAll successful");
		} catch (Exception re) {
			LOG.error("deleteAll failed", re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public void delete(Class<T> entityClass, PK id) throws CloudDBException {
		LOG.debug("deleting instance");
		try {
			delete(get(entityClass, id));
			LOG.debug("delete successful");
		} catch (Exception re) {
			LOG.error("delete failed", re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public int batchExecute(String hqlkey, Object[] values)
			throws CloudDBException {
		LOG.debug("batchExecute by hqlkey " + hqlkey);
		try {
			Query query = createQuery(hqlkey, values);
			return query.executeUpdate();
		} catch (Exception re) {
			LOG.error("batchExecute failed by hqlkey " + hqlkey, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public int batchExecute(String hqlkey, Map<String, ?> values)
			throws CloudDBException {
		LOG.debug("batchExecute by hqlkey " + hqlkey);
		try {
			Query query = createQuery(hqlkey, values);
			return query.executeUpdate();
		} catch (Exception re) {
			LOG.error("batchExecute failed by hqlkey " + hqlkey, re);
			throw new CloudDBException(re.toString(), re);
		}

	}

	public T get(Class<T> entityClass, PK id) throws CloudDBException {
		LOG.debug("get by id begin");
		try {
			return getHibernateTemplate().get(entityClass, id);
		} catch (Exception re) {
			LOG.error("get failed ", re);
			throw new CloudDBException(re.toString(), re);
		}

	}

	public <X> X findUnique(String hqlkey, Object[] values)
			throws CloudDBException {
		LOG.debug("findUnique by hqlkey " + hqlkey);
		try {
			Query query = createQuery(hqlkey, values);
			return (X) query.uniqueResult();
		} catch (Exception re) {
			LOG.error("findUnique failed by hqlkey " + hqlkey, re);
			throw new CloudDBException(re.toString(), re);
		}

	}

	public <X> X findOneByTemplate(String sql, Object[] values)
			throws CloudDBException {
		LOG.debug("findOneByTemplate by hqlkey ");
		try {
			List list = getHibernateTemplate().find(sql, values);
			if ((list == null) || (list.isEmpty())) {
				return null;
			}
			return (X) list.get(0);
		} catch (Exception re) {
			LOG.error("findUnique failed by hqlkey ", re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public <X> List<X> findListByTemplate(String sqlSentence, Object[] values)
			throws CloudDBException {
		LOG.debug("findOneByTemplate by hqlkey ");
		try {
			List list = getHibernateTemplate().find(sqlSentence, values);
			return list;
		} catch (Exception re) {
			LOG.error("findUnique failed by hqlkey ", re);
			throw new CloudDBException(re.toString(), re);
		}

	}

	public <X> X findUnique(String hqlkey, Map<String, ?> values)
			throws CloudDBException {
		LOG.debug("findUnique by hqlkey " + hqlkey);
		try {
			Query query = createQuery(hqlkey, values);
			return (X) query.uniqueResult();
		} catch (Exception re) {
			LOG.error("findUnique failed by hqlkey " + hqlkey, re);
			throw new CloudDBException(re.toString(), re);
		}

	}

	public <X> List<X> find(String hqlkey, Object[] values)
			throws CloudDBException {
		LOG.debug("find by hqlkey " + hqlkey);
		try {
			Query query = createQuery(hqlkey, values);
			return query.list();
		} catch (Exception re) {
			LOG.error("find failed by hqlkey " + hqlkey, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public <X> List<X> find(String hqlkey, Map<String, ?> values)
			throws CloudDBException {
		LOG.debug("find by hqlkey " + hqlkey);
		try {
			Query query = createQuery(hqlkey, values);
			return query.list();
		} catch (Exception re) {
			LOG.error("find failed by hqlkey " + hqlkey, re);
			throw new CloudDBException(re.toString(), re);
		}

	}

	public List<T> findAll(Class<T> entityClass) throws CloudDBException {
		LOG.debug("find all ");
		try {
			String name = entityClass.getName();
			return getHibernateTemplate().find("from " + name);
		} catch (Exception re) {
			LOG.error("find failed ", re);
			throw new CloudDBException(re.toString(), re);
		}

	}

	public Query createQuery(String hqlkey, Object[] values)
			throws CloudDBException {
		LOG.debug("createQuery by hqlkey " + hqlkey);
		try {
			Session session = getHibernateTemplate().getSessionFactory()
					.getCurrentSession();
			Query query = session.getNamedQuery(hqlkey);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
			}
			return query;
		} catch (Exception re) {
			LOG.error("createQuery by hqlkey " + hqlkey, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public Query createQuery(String hqlkey, Map<String, ?> values)
			throws CloudDBException {
		LOG.debug("createQuery by hqlkey " + hqlkey);
		try {
			Session session = getHibernateTemplate().getSessionFactory()
					.getCurrentSession();
			Query query = session.getNamedQuery(hqlkey);
			if (values != null) {
				query.setProperties(values);
			}
			return query;
		} catch (Exception re) {
			LOG.error("createQuery by hqlkey " + hqlkey, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public <X> X findUniqueBySqlSentence(String sqlSentence, Object[] values)
			throws CloudDBException {
		LOG.debug("findUniqueBySqlSentence by sqlSentence " + sqlSentence);
		try {
			Query query = createQuerySentence(sqlSentence, values);
			return (X) query.uniqueResult();
		} catch (Exception re) {
			LOG.error("findUniqueBySqlSentence by sqlSentence " + sqlSentence,
					re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public <X> X findUniqueBySqlSentence(String sqlSentence,
			Map<String, ?> values) throws CloudDBException {
		LOG.debug("findUniqueBySqlSentence by sqlSentence " + sqlSentence);
		try {
			Query query = createQuerySentence(sqlSentence, values);
			return (X) query.uniqueResult();
		} catch (Exception re) {
			LOG.error("findUniqueBySqlSentence by sqlSentence " + sqlSentence,
					re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public <X> List<X> findBySqlSentence(String sqlSentence, Object[] values)
			throws CloudDBException {
		LOG.debug("findBySqlSentence by sqlSentence " + sqlSentence);
		try {
			Query query = createQuerySentence(sqlSentence, values);
			return query.list();
		} catch (Exception re) {
			LOG.error("findBySqlSentence by sqlSentence " + sqlSentence, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public <X> List<X> findBySqlSentence(String sqlSentence,
			Map<String, ?> values) throws CloudDBException {
		LOG.debug("findBySqlSentence by sqlSentence " + sqlSentence);
		try {
			Query query = createQuerySentence(sqlSentence, values);
			return query.list();
		} catch (Exception re) {
			LOG.error("findBySqlSentence by sqlSentence " + sqlSentence, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public Query createQuerySentence(String sql, Object[] values)
			throws CloudDBException {
		LOG.debug("createQuerySentence by sql " + sql);
		try {
			Session session = getHibernateTemplate().getSessionFactory()
					.getCurrentSession();

			Query query = session.createQuery(sql);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
			}
			return query;
		} catch (Exception re) {
			LOG.error("createQuerySentence by sqlSentence " + sql, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public Query createQuerySentence(String sql, Map<String, ?> values)
			throws CloudDBException {
		LOG.debug("createQuerySentence by sql " + sql);
		try {
			Session session = getHibernateTemplate().getSessionFactory()
					.getCurrentSession();

			Query query = session.createQuery(sql);
			if (values != null) {
				query.setProperties(values);
			}
			return query;
		} catch (Exception re) {
			LOG.error("createQuerySentence by sqlSentence " + sql, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public SQLQuery createSqlQuery(String sql, Object[] values)
			throws CloudDBException {
		LOG.debug("createSqlQuery by sql " + sql);
		try {
			SQLQuery query = getSession().createSQLQuery(sql);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
			}
			return query;
		} catch (Exception re) {
			LOG.error("createSqlQuery by sqlSentence " + sql, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public SQLQuery createSqlQuery(String sql, Map<String, ?> values)
			throws CloudDBException {
		LOG.debug("createSqlQuery by sql " + sql);
		try {
			SQLQuery query = getSession().createSQLQuery(sql);
			if (values != null) {
				query.setProperties(values);
			}
			return query;
		} catch (Exception re) {
			LOG.error("createSqlQuery by sqlSentence " + sql, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public Query findBySql(String sql) throws CloudDBException {
		LOG.debug("findBySql by sql " + sql);
		try {
			Session session = getSessionFactory().getCurrentSession();
			Query query = session.createQuery(sql);
			return query;
		} catch (Exception re) {
			LOG.error("findBySql by sqlSentence " + sql, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public void deleteBySql(String sql) throws CloudDBException {
		LOG.debug("deleteBySql by sql " + sql);
		try {
			Session session = getSessionFactory().getCurrentSession();
			Query query = session.createQuery(sql);
			query.executeUpdate();
			LOG.debug("deleteBySql by sql " + sql + "successfully");
		} catch (Exception re) {
			LOG.error("deleteBySql by sqlSentence " + sql, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public int batchExecuteBySql(String sql, Object[] values)
			throws CloudDBException {
		LOG.debug("batchExecuteBySql by sql " + sql);
		try {
			Query query = createQuerySentence(sql, values);
			return query.executeUpdate();
		} catch (Exception re) {
			LOG.error("batchExecuteBySql by sqlSentence " + sql, re);
			throw new CloudDBException(re.toString(), re);
		}
	}

	public int batchExecuteBySql(String sql, Map<String, ?> values)
			throws CloudDBException {
		LOG.debug("batchExecuteBySql by sql " + sql);
		try {
			Query query = createQuerySentence(sql, values);
			return query.executeUpdate();
		} catch (Exception re) {
			LOG.error("batchExecuteBySql by sqlSentence " + sql, re);
			throw new CloudDBException(re.toString(), re);
		}

	}
}
