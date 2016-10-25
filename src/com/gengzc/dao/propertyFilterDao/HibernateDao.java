package com.gengzc.dao.propertyFilterDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.Assert;

import com.gengzc.dao.propertyFilterDao.PropertyFilter.MatchType;


/**
 * ��װSpringSide��չ���ܵ�Hibernat DAO���ͻ���.
 * 
 * ��չ���ܰ�����ҳ��ѯ,�����Թ��������б��ѯ. ����Service��ֱ��ʹ��,Ҳ������չ����DAO����ʹ��,���������캯����ע��.
 * 
 * @param <T>
 *            DAO�����Ķ�������
 * @param <PK>
 *            ��������
 * 
 * @author calvin
 */
public class HibernateDao<T, PK extends Serializable> extends
		SimpleHibernateDao<T, PK> {
	/**
	 * ����Dao������ʹ�õĹ��캯��. ͨ������ķ��Ͷ���ȡ�ö�������Class. eg. public class UserDao extends
	 * HibernateDao<User, Long>{ }
	 */
	public HibernateDao() {
		super();
	}

	/**
	 * ����ʡ��Dao��, Service��ֱ��ʹ��ͨ��HibernateDao�Ĺ��캯��. �ڹ��캯���ж����������Class. eg.
	 * HibernateDao<User, Long> userDao = new HibernateDao<User,
	 * Long>(sessionFactory, User.class);
	 */
	public HibernateDao(final SessionFactory sessionFactory,
			final Class<T> entityClass) {
		super(sessionFactory, entityClass);
	}

	// -- ��ҳ��ѯ���� --//
	/**
	 * ��ҳ��ȡȫ������.
	 */
	public Page<T> getAll(final Page<T> page) {
		return findPage(page);
	}

	/**
	 * ��HQL��ҳ��ѯ.
	 * 
	 * @param page
	 *            ��ҳ����.��֧�����е�orderBy����.
	 * @param hql
	 *            hql���.
	 * @param values
	 *            �����ɱ�Ĳ�ѯ����,��˳���.
	 * 
	 * @return ��ҳ��ѯ���, ��������б����в�ѯʱ�Ĳ���.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Page<T> findPage(final Page<T> page, final String hql,
			final Object... values) {
		Assert.notNull(page, "page����Ϊ��");

		Query q = createQuery(hql, values);

		if (page.isAutoCount()) {
			long totalCount = countHqlResult(hql, values);
			page.setTotalCount(totalCount);
		}

		setPageParameter(q, page);
		List result = q.list();
		page.setResult(result);
		return page;
	}

	/**
	 * ��HQL��ҳ��ѯ.
	 * 
	 * @param page
	 *            ��ҳ����.
	 * @param hql
	 *            hql���.
	 * @param values
	 *            ��������,�����ư�.
	 * 
	 * @return ��ҳ��ѯ���, ��������б����в�ѯʱ�Ĳ���.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Page<T> findPage(final Page<T> page, final String hql,
			final Map<String, ?> values) {
		Assert.notNull(page, "page����Ϊ��");

		Query q = createQuery(hql, values);

		if (page.isAutoCount()) {
			long totalCount = countHqlResult(hql, values);
			page.setTotalCount(totalCount);
		}

		setPageParameter(q, page);

		List result = q.list();
		page.setResult(result);
		return page;
	}

	/**
	 * ��Criteria��ҳ��ѯ.
	 * 
	 * @param page
	 *            ��ҳ����.
	 * @param criterions
	 *            �����ɱ��Criterion.
	 * 
	 * @return ��ҳ��ѯ���.��������б����в�ѯʱ�Ĳ���.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Page<T> findPage(final Page<T> page, final Criterion... criterions) {
		Assert.notNull(page, "page����Ϊ��");

		Criteria c = createCriteria(criterions);

		if (page.isAutoCount()) {
			int totalCount = countCriteriaResult(c);
			page.setTotalCount(totalCount);
		}

		setPageParameter(c, page);
		List result = c.list();
		page.setResult(result);
		return page;
	}

	/**
	 * ���÷�ҳ������Query����,��������.
	 */
	protected Query setPageParameter(final Query q, final Page<T> page) {
		// hibernate��firstResult����Ŵ�0��ʼ
		q.setFirstResult(page.getFirst() - 1);
		q.setMaxResults(page.getPageSize());
		return q;
	}

	/**
	 * ���÷�ҳ������Criteria����,��������.
	 */
	protected Criteria setPageParameter(final Criteria c, final Page<T> page) {
		// hibernate��firstResult����Ŵ�0��ʼ
		c.setFirstResult(page.getFirst() - 1);
		c.setMaxResults(page.getPageSize());

		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');

			Assert.isTrue(orderByArray.length == orderArray.length,
					"��ҳ�������������,�����ֶ���������ĸ��������");

			for (int i = 0; i < orderByArray.length; i++) {
				if (Page.ASC.equals(orderArray[i])) {
					c.addOrder(Order.asc(orderByArray[i]));
				} else {
					c.addOrder(Order.desc(orderByArray[i]));
				}
			}
		}
		return c;
	}

	/**
	 * ִ��count��ѯ��ñ���Hql��ѯ���ܻ�õĶ�������.
	 * 
	 * ������ֻ���Զ�����򵥵�hql���,���ӵ�hql��ѯ�����б�дcount����ѯ.
	 */
	protected long countHqlResult(final String hql, final Object... values) {
		String fromHql = hql;
		// select�Ӿ���order by�Ӿ��Ӱ��count��ѯ,���м򵥵��ų�.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;

		try {
			Long count = findUnique(countHql, values);
			return count;
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:"
					+ countHql, e);
		}
	}

	/**
	 * ִ��count��ѯ��ñ���Hql��ѯ���ܻ�õĶ�������.
	 * 
	 * ������ֻ���Զ�����򵥵�hql���,���ӵ�hql��ѯ�����б�дcount����ѯ.
	 */
	protected long countHqlResult(final String hql, final Map<String, ?> values) {
		String fromHql = hql;
		// select�Ӿ���order by�Ӿ��Ӱ��count��ѯ,���м򵥵��ų�.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;

		try {
			Long count = findUnique(countHql, values);
			return count;
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:"
					+ countHql, e);
		}
	}

	/**
	 * ִ��count��ѯ��ñ���Criteria��ѯ���ܻ�õĶ�������.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected int countCriteriaResult(final Criteria c) {
		CriteriaImpl impl = (CriteriaImpl) c;

		// �Ȱ�Projection��ResultTransformer��OrderByȡ����,������ߺ���ִ��Count����
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		try {
			orderEntries = (List) ReflectionUtils.getFieldValue(impl,
					"orderEntries");
			ReflectionUtils
					.setFieldValue(impl, "orderEntries", new ArrayList());
		} catch (Exception e) {
			log.error("�������׳����쳣:{}" + e.getMessage());
		} 

		// ִ��Count��ѯ
		int totalCount = ((Long) c.setProjection(Projections.rowCount())
				.uniqueResult()).intValue();

		// ��֮ǰ��Projection,ResultTransformer��OrderBy�����������ȥ
		c.setProjection(projection);

		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			c.setResultTransformer(transformer);
		}
		try {
			ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
		} catch (Exception e) {
			log.error("�������׳����쳣:{}" + e.getMessage());
		}

		return totalCount;
	}

	// -- ���Թ�������(FieldFilter)��ѯ���� --//

	/**
	 * �����Բ��Ҷ����б�,֧�ֶ���ƥ�䷽ʽ.
	 * 
	 * @param matchType
	 *            ƥ�䷽ʽ,Ŀǰ֧�ֵ�ȡֵ��FieldFilter��MatcheType enum.
	 */
	public List<T> findBy(final String fieldName, final Object value,
			final MatchType matchType) {
		Criterion criterion = buildFieldFilterCriterion(fieldName,
				new Object[] { value }, null, matchType);
		return find(criterion);
	}

	/**
	 * �����Թ��������б���Ҷ����б�.
	 */
	public List<T> find(List<PropertyFilter> filters) {
		Criterion[] criterions = buildFieldFilterCriterions(filters);
		return find(criterions);
	}

	/**
	 * �����Թ��������б��ҳ���Ҷ���.
	 */
	public Page<T> findPage(final Page<T> page,
			final List<PropertyFilter> filters) {
		Criterion[] criterions = buildFieldFilterCriterions(filters);
		return findPage(page, criterions);
	}

	/**
	 * �����������б���Criterion����,��������.
	 */
	protected Criterion[] buildFieldFilterCriterions(
			final List<PropertyFilter> filters) {
		List<Criterion> criterionList = new ArrayList<Criterion>();
		for (PropertyFilter filter : filters) {
			if (!filter.isMulti()) {
				criterionList.add(buildFieldFilterCriterion(
						filter.getFieldName(), filter.getValues(),
						filter.getOtherField(), filter.getMatchType()));
			} else {
				criterionList.add(buildMultiFieldFilter(filter));
			}
		}
		return criterionList.toArray(new Criterion[criterionList.size()]);
	}

	protected Criterion buildMultiFieldFilter(PropertyFilter filter) {
		Criterion criterion = null;
		for (Iterator<PropertyFilter> it = filter.iterator(); it.hasNext();) {
			PropertyFilter ff = it.next();
			Criterion cri = buildFieldFilterCriterion(ff);
			if (ff.isAnd()) {
				criterion = new SimpleLogicalExpression(criterion, cri, "and");
			} else if (ff.isOr()) {
				criterion = new SimpleLogicalExpression(criterion, cri, "or");
			} else if (ff.isRoundAnd()) {
				criterion = Restrictions.and(criterion, cri);
			} else if (ff.isRoundOr()) {
				criterion = Restrictions.or(criterion, cri);
			} else {
				criterion = cri;
			}
		}
		return criterion;
	}

	/**
	 * ������������������Criterion,��������.
	 */
	protected Criterion buildFieldFilterCriterion(PropertyFilter filter) {
		return buildFieldFilterCriterion(filter.getFieldName(),
				filter.getValues(), filter.getOtherField(),
				filter.getMatchType());
	}
	
	/**
	 * ������������������Criterion,��������.
	 */
	protected Criterion buildFieldFilterCriterion(final String fieldName,
			final Object[] fieldValues, String otherField,
			final MatchType matchType) {
		Assert.hasText(fieldName, "fieldName����Ϊ��");
		Criterion criterion = null;
		try {
			// ����MatchType����criterion
			if (MatchType.EQ.equals(matchType)) {
				criterion = Restrictions.eq(fieldName, fieldValues[0]);
			} else if (MatchType.LE.equals(matchType)) {
				criterion = Restrictions.le(fieldName, fieldValues[0]);
			} else if (MatchType.LT.equals(matchType)) {
				criterion = Restrictions.lt(fieldName, fieldValues[0]);
			} else if (MatchType.GE.equals(matchType)) {
				criterion = Restrictions.ge(fieldName, fieldValues[0]);
			} else if (MatchType.GT.equals(matchType)) {
				criterion = Restrictions.gt(fieldName, fieldValues[0]);
			} else if (MatchType.NE.equals(matchType)) {
				criterion = Restrictions.ne(fieldName, fieldValues[0]);
			} else if (MatchType.EQF.equals(matchType)) {
				criterion = Restrictions.eqProperty(fieldName, otherField);
			} else if (MatchType.LEF.equals(matchType)) {
				criterion = Restrictions.leProperty(fieldName, otherField);
			} else if (MatchType.LTF.equals(matchType)) {
				criterion = Restrictions.ltProperty(fieldName, otherField);
			} else if (MatchType.GEF.equals(matchType)) {
				criterion = Restrictions.geProperty(fieldName, otherField);
			} else if (MatchType.GTF.equals(matchType)) {
				criterion = Restrictions.gtProperty(fieldName, otherField);
			} else if (MatchType.NEF.equals(matchType)) {
				criterion = Restrictions.neProperty(fieldName, otherField);
			} else if (MatchType.LIKE.equals(matchType)) {
				criterion = Restrictions.like(fieldName,
						(String) fieldValues[0], MatchMode.EXACT);
			} else if (MatchType.LIKE_START.equals(matchType)) {
				criterion = Restrictions.like(fieldName,
						(String) fieldValues[0], MatchMode.START);
			} else if (MatchType.LIKE_END.equals(matchType)) {
				criterion = Restrictions.like(fieldName,
						(String) fieldValues[0], MatchMode.END);
			} else if (MatchType.LIKE_ANYWHERE.equals(matchType)) {
				criterion = Restrictions.like(fieldName,
						(String) fieldValues[0], MatchMode.ANYWHERE);
			} else if (MatchType.BETWEEN.equals(matchType)) {
				criterion = Restrictions.between(fieldName, fieldValues[0],
						fieldValues[1]);
			}
		} catch (Exception e) {
//			throw ReflectionUtils.handleReflectionException(e);
			e.printStackTrace();
		}
		return criterion;
	}

	/**
	 * �ж϶��������ֵ�����ݿ����Ƿ�Ψһ.
	 * 
	 * ���޸Ķ�����龰��,����������޸ĵ�ֵ(value)��������ԭ����ֵ(orgValue)�����Ƚ�.
	 */
	public boolean isFieldUnique(final String fieldName, final Object newValue,
			final Object oldValue) {
		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}
		Object object = findUniqueBy(fieldName, newValue);
		return (object == null);
	}
}


