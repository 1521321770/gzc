package com.gengzc.dao.propertyFilterDao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.lang.reflect.ParameterizedType;  

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

/**
 * ��װHibernateԭ��API��DAO���ͻ���.
 * 
 * ����Service��ֱ��ʹ��,Ҳ������չ����DAO����ʹ��.
 * �ο�Spring2.5�Դ���Petlinc����,ȡ����HibernateTemplate,ֱ��ʹ��Hibernateԭ��API.
 * 
 * @param <T>
 *            DAO�����Ķ�������
 * @param <PK>
 *            ��������
 * 
 * @author calvin
 */
@SuppressWarnings("unchecked")
public class SimpleHibernateDao<T, PK extends Serializable> extends HibernateDaoSupport{

	protected Log log = LogFactory.getLog(SimpleHibernateDao.class);

	@Autowired
	protected SessionFactory sessionFactory;

	protected Class<T> entityClass;

	/**
	 * ����Dao������ʹ�õĹ��캯��. ͨ������ķ��Ͷ���ȡ�ö�������Class. eg. public class UserDao extends
	 * SimpleHibernateDao<User, Long>
	 */
	public SimpleHibernateDao() {
		this.entityClass = (Class<T>)((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	/**
	 * ��������ʡ��Dao��, ��Service��ֱ��ʹ��ͨ��SimpleHibernateDao�Ĺ��캯��. �ڹ��캯���ж����������Class.
	 * eg. SimpleHibernateDao<User, Long> userDao = new SimpleHibernateDao<User,
	 * Long>(sessionFactory, User.class);
	 */
	public SimpleHibernateDao(final SessionFactory sessionFactory,
			final Class<T> entityClass) {
		this.sessionFactory = sessionFactory;
		this.entityClass = entityClass;
	}

	/**
	 * ȡ��sessionFactory.
	 */
//	public SessionFactory getSessionFactory() {
//		return sessionFactory;
//	}

	/**
	 * ����@Autowired������ע��SessionFactory, ���ж��SesionFactory��ʱ��Override������.
	 */
//	@Autowired
//	public void setSessionFactory(final SessionFactory sessionFactory) {
//		this.sessionFactory = sessionFactory;
//	}

	/**
	 * ȡ�õ�ǰSession.
	 */
//	public Session getSession() {
//		return sessionFactory.getCurrentSession();
//	}

	/**
	 * �����������޸ĵĶ���.
	 */
	public void save(final T entity) {
		Assert.notNull(entity, "entity����Ϊ��");
		getSession().saveOrUpdate(entity);
		log.debug("save entity: {}" + entity);
	}

	/**
	 * ɾ������.
	 * 
	 * @param entity
	 *            ���������session�еĶ����id���Ե�transient����.
	 */
	public void delete(final T entity) {
		Assert.notNull(entity, "entity����Ϊ��");
		getSession().delete(entity);
		log.debug("delete entity: {}" + entity);
	}

	/**
	 * ��idɾ������.
	 */
	public void delete(final PK id) {
		Assert.notNull(id, "id����Ϊ��");
		delete(get(id));
		log.debug("delete entity {},id is {}" + entityClass.getSimpleName() +
				id);
	}

	/**
	 * ��idɾ������.
	 */
	public void delete(final PK id, LockOptions lockOptions) {
		Assert.notNull(id, "id����Ϊ��");
		delete(get(id, lockOptions));
		log.debug("delete entity {},id is {}" + entityClass.getSimpleName() + id);
	}

	/**
	 * ��id��ȡ����.
	 */
	public T get(final PK id) {
		Assert.notNull(id, "id����Ϊ��");
		return (T) getSession().get(entityClass, id);
	}

	/**
	 * ��id��ȡ����.
	 */
	public T get(final PK id, LockOptions lockOption) {
		Assert.notNull(id, "id����Ϊ��");
		return (T) getSession().get(entityClass, id, lockOption);
	}

	/**
	 * ��id��ȡ����
	 * <p>
	 * ע�������صĴ�������
	 */
	public T load(final PK id) {
		Assert.notNull(id, "id����Ϊ��");
		return (T) getSession().load(entityClass, id);
	}

	/**
	 * ��id��ȡ����
	 * <p>
	 * ע�������صĴ�������
	 */
	public T load(final PK id, LockOptions lockOption) {
		Assert.notNull(id, "id����Ϊ��");
		return (T) getSession().load(entityClass, id, lockOption);
	}

	/**
	 * ��ȡȫ������.
	 */
	public List<T> getAll() {
		return find();
	}

	/**
	 * ��ȡȫ������,֧������.
	 */
	public List<T> getAll(String orderBy, boolean isAsc) {
		Criteria criteria = createCriteria();
		if (isAsc) {
			criteria.addOrder(Order.asc(orderBy));
		} else {
			criteria.addOrder(Order.desc(orderBy));
		}
		return criteria.list();
	}

	/**
	 * �����Բ��Ҷ����б�,ƥ�䷽ʽΪ���.
	 */
	public List<T> findBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName����Ϊ��");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return find(criterion);
	}

	/**
	 * �����Բ���Ψһ����,ƥ�䷽ʽΪ���.
	 */
	public T findUniqueBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName����Ϊ��");
		Criterion criterion = Restrictions.eq(propertyName, value);
		Criteria criteria = createCriteria(criterion);
		return (T) criteria.uniqueResult();
	}

	/**
	 * �����Բ���Ψһ����,ƥ�䷽ʽΪ���.
	 */
	public T findUniqueBy(final String propertyName, final Object value,
			LockOptions lockOptions) {
		Assert.hasText(propertyName, "propertyName����Ϊ��");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return (T) createCriteria(criterion).setLockMode(
				lockOptions.getLockMode()).uniqueResult();
	}

	/**
	 * ��id�б���ȡ����.
	 */
	public List<T> findByIds(List<PK> ids) {
		return find(Restrictions.in(getIdName(), ids));
	}

	/**
	 * ��HQL��ѯ�����б�.
	 * 
	 * @param values
	 *            �����ɱ�Ĳ���,��˳���.
	 */
	public <X> List<X> find(final String hql, final Object... values) {
		return createQuery(hql, values).list();
	}

	/**
	 * ��HQL��ѯ�����б�.
	 * 
	 * @param values
	 *            ��������,�����ư�.
	 */
	public <X> List<X> find(final String hql, final Map<String, ?> values) {
		return createQuery(hql, values).list();
	}

	/**
	 * ��HQL��ѯΨһ����.
	 * 
	 * @param values
	 *            �����ɱ�Ĳ���,��˳���.
	 */
	public <X> X findUnique(final String hql, final Object... values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * ��HQL��ѯΨһ����.
	 * 
	 * @param values
	 *            ��������,�����ư�.
	 */
	public <X> X findUnique(final String hql, final Map<String, ?> values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * ִ��HQL���������޸�/ɾ������.
	 */
	public int batchExecute(final String hql, final Object... values) {
		return createQuery(hql, values).executeUpdate();
	}

	/**
	 * ִ��HQL���������޸�/ɾ������.
	 * 
	 * @return ���¼�¼��.
	 */
	public int batchExecute(final String hql, final Map<String, ?> values) {
		return createQuery(hql, values).executeUpdate();
	}

	/**
	 * ���ݲ�ѯHQL������б�����Query����.
	 * 
	 * �����װ��find()����ȫ��Ĭ�Ϸ��ض�������ΪT,����ΪTʱʹ�ñ�����.
	 * 
	 * @param values
	 *            �����ɱ�Ĳ���,��˳���.
	 */
	public Query createQuery(final String queryString, final Object... values) {
		Assert.hasText(queryString, "queryString����Ϊ��");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	/**
	 * ���ݲ�ѯHQL������б�����Query����.
	 * 
	 * @param values
	 *            ��������,�����ư�.
	 */
	public Query createQuery(final String queryString,
			final Map<String, ?> values) {
		Assert.hasText(queryString, "queryString����Ϊ��");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			query.setProperties(values);
		}
		return query;
	}

	/**
	 * ��Criteria��ѯ�����б�.
	 * 
	 * @param criterions
	 *            �����ɱ��Criterion.
	 */
	public List<T> find(final Criterion... criterions) {
		return createCriteria(criterions).list();
	}

	/**
	 * ��Criteria��ѯΨһ����.
	 * 
	 * @param criterions
	 *            �����ɱ��Criterion.
	 */
	public T findUnique(final Criterion... criterions) {
		return (T) createCriteria(criterions).uniqueResult();
	}

	/**
	 * ����Criterion��������Criteria.
	 * 
	 * �����װ��find()����ȫ��Ĭ�Ϸ��ض�������ΪT,����ΪTʱʹ�ñ�����.
	 * 
	 * @param criterions
	 *            �����ɱ��Criterion.
	 */
	public Criteria createCriteria(final Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

	/**
	 * ��ʼ������. ʹ��load()�����õ��Ľ��Ƕ���Proxy, �ڴ���View��ǰ��Ҫ���г�ʼ��.
	 * ֻ��ʼ��entity��ֱ������,�������ʼ���ӳټ��صĹ������Ϻ�����. �����ʼ����������,��ʵ���µĺ���,ִ��:
	 * Hibernate.initialize(user.getRoles())����ʼ��User��ֱ�����Ժ͹�������.
	 * Hibernate.initialize
	 * (user.getDescription())����ʼ��User��ֱ�����Ժ��ӳټ��ص�Description����.
	 */
	public void initEntity(T entity) {
		Hibernate.initialize(entity);
	}

	/**
	 * @see #initEntity(Object)
	 */
	public void initEntity(List<T> entityList) {
		for (T entity : entityList) {
			Hibernate.initialize(entity);
		}
	}

	/**
	 * Flush��ǰSession.
	 */
	public void flush() {
		getSession().flush();
	}

	/**
	 * ΪQuery����distinct transformer.
	 */
	public Query distinct(Query query) {
		query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return query;
	}
	/**
	 * ΪCriteria����distinct transformer.
	 */
	public Criteria distinct(Criteria criteria) {
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria;
	}

	/**
	 * ȡ�ö����������.
	 */
	public String getIdName() {
		ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
		return meta.getIdentifierPropertyName();
	}
}