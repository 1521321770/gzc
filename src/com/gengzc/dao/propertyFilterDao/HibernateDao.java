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
 * 封装SpringSide扩展功能的Hibernat DAO泛型基类.
 * 
 * 扩展功能包括分页查询,按属性过滤条件列表查询. 可在Service层直接使用,也可以扩展泛型DAO子类使用,见两个构造函数的注释.
 * 
 * @param <T>
 *            DAO操作的对象类型
 * @param <PK>
 *            主键类型
 * 
 * @author calvin
 */
public class HibernateDao<T, PK extends Serializable> extends
		SimpleHibernateDao<T, PK> {
	/**
	 * 用于Dao层子类使用的构造函数. 通过子类的泛型定义取得对象类型Class. eg. public class UserDao extends
	 * HibernateDao<User, Long>{ }
	 */
	public HibernateDao() {
		super();
	}

	/**
	 * 用于省略Dao层, Service层直接使用通用HibernateDao的构造函数. 在构造函数中定义对象类型Class. eg.
	 * HibernateDao<User, Long> userDao = new HibernateDao<User,
	 * Long>(sessionFactory, User.class);
	 */
	public HibernateDao(final SessionFactory sessionFactory,
			final Class<T> entityClass) {
		super(sessionFactory, entityClass);
	}

	// -- 分页查询函数 --//
	/**
	 * 分页获取全部对象.
	 */
	public Page<T> getAll(final Page<T> page) {
		return findPage(page);
	}

	/**
	 * 按HQL分页查询.
	 * 
	 * @param page
	 *            分页参数.不支持其中的orderBy参数.
	 * @param hql
	 *            hql语句.
	 * @param values
	 *            数量可变的查询参数,按顺序绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Page<T> findPage(final Page<T> page, final String hql,
			final Object... values) {
		Assert.notNull(page, "page不能为空");

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
	 * 按HQL分页查询.
	 * 
	 * @param page
	 *            分页参数.
	 * @param hql
	 *            hql语句.
	 * @param values
	 *            命名参数,按名称绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Page<T> findPage(final Page<T> page, final String hql,
			final Map<String, ?> values) {
		Assert.notNull(page, "page不能为空");

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
	 * 按Criteria分页查询.
	 * 
	 * @param page
	 *            分页参数.
	 * @param criterions
	 *            数量可变的Criterion.
	 * 
	 * @return 分页查询结果.附带结果列表及所有查询时的参数.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Page<T> findPage(final Page<T> page, final Criterion... criterions) {
		Assert.notNull(page, "page不能为空");

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
	 * 设置分页参数到Query对象,辅助函数.
	 */
	protected Query setPageParameter(final Query q, final Page<T> page) {
		// hibernate的firstResult的序号从0开始
		q.setFirstResult(page.getFirst() - 1);
		q.setMaxResults(page.getPageSize());
		return q;
	}

	/**
	 * 设置分页参数到Criteria对象,辅助函数.
	 */
	protected Criteria setPageParameter(final Criteria c, final Page<T> page) {
		// hibernate的firstResult的序号从0开始
		c.setFirstResult(page.getFirst() - 1);
		c.setMaxResults(page.getPageSize());

		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');

			Assert.isTrue(orderByArray.length == orderArray.length,
					"分页多重排序参数中,排序字段与排序方向的个数不相等");

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
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	protected long countHqlResult(final String hql, final Object... values) {
		String fromHql = hql;
		// select子句与order by子句会影响count查询,进行简单的排除.
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
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	protected long countHqlResult(final String hql, final Map<String, ?> values) {
		String fromHql = hql;
		// select子句与order by子句会影响count查询,进行简单的排除.
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
	 * 执行count查询获得本次Criteria查询所能获得的对象总数.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected int countCriteriaResult(final Criteria c) {
		CriteriaImpl impl = (CriteriaImpl) c;

		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		try {
			orderEntries = (List) ReflectionUtils.getFieldValue(impl,
					"orderEntries");
			ReflectionUtils
					.setFieldValue(impl, "orderEntries", new ArrayList());
		} catch (Exception e) {
			log.error("不可能抛出的异常:{}" + e.getMessage());
		} 

		// 执行Count查询
		int totalCount = ((Long) c.setProjection(Projections.rowCount())
				.uniqueResult()).intValue();

		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
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
			log.error("不可能抛出的异常:{}" + e.getMessage());
		}

		return totalCount;
	}

	// -- 属性过滤条件(FieldFilter)查询函数 --//

	/**
	 * 按属性查找对象列表,支持多种匹配方式.
	 * 
	 * @param matchType
	 *            匹配方式,目前支持的取值见FieldFilter的MatcheType enum.
	 */
	public List<T> findBy(final String fieldName, final Object value,
			final MatchType matchType) {
		Criterion criterion = buildFieldFilterCriterion(fieldName,
				new Object[] { value }, null, matchType);
		return find(criterion);
	}

	/**
	 * 按属性过滤条件列表查找对象列表.
	 */
	public List<T> find(List<PropertyFilter> filters) {
		Criterion[] criterions = buildFieldFilterCriterions(filters);
		return find(criterions);
	}

	/**
	 * 按属性过滤条件列表分页查找对象.
	 */
	public Page<T> findPage(final Page<T> page,
			final List<PropertyFilter> filters) {
		Criterion[] criterions = buildFieldFilterCriterions(filters);
		return findPage(page, criterions);
	}

	/**
	 * 按属性条件列表创建Criterion数组,辅助函数.
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
	 * 按属性条件参数创建Criterion,辅助函数.
	 */
	protected Criterion buildFieldFilterCriterion(PropertyFilter filter) {
		return buildFieldFilterCriterion(filter.getFieldName(),
				filter.getValues(), filter.getOtherField(),
				filter.getMatchType());
	}
	
	/**
	 * 按属性条件参数创建Criterion,辅助函数.
	 */
	protected Criterion buildFieldFilterCriterion(final String fieldName,
			final Object[] fieldValues, String otherField,
			final MatchType matchType) {
		Assert.hasText(fieldName, "fieldName不能为空");
		Criterion criterion = null;
		try {
			// 根据MatchType构造criterion
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
	 * 判断对象的属性值在数据库内是否唯一.
	 * 
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
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


