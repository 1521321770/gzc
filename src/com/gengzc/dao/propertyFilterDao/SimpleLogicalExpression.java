package com.gengzc.dao.propertyFilterDao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;

/**
 * È¥µôÁ½¶Ë Ô²À¨ºÅµÄLogicalExpression
 * 
 * @author Administrator
 * 
 */
public class SimpleLogicalExpression implements Criterion {
	private static final long serialVersionUID = 8924312928598715984L;
	private final Criterion lhs;
	private final Criterion rhs;
	private final String op;

	protected SimpleLogicalExpression(Criterion lhs, Criterion rhs, String op) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}

	@Override
	public TypedValue[] getTypedValues(Criteria criteria,
			CriteriaQuery criteriaQuery) throws HibernateException {

		TypedValue[] lhstv = lhs.getTypedValues(criteria, criteriaQuery);
		TypedValue[] rhstv = rhs.getTypedValues(criteria, criteriaQuery);
		TypedValue[] result = new TypedValue[lhstv.length + rhstv.length];
		System.arraycopy(lhstv, 0, result, 0, lhstv.length);
		System.arraycopy(rhstv, 0, result, lhstv.length, rhstv.length);
		return result;
	}

	@Override
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException {

		return ' ' + lhs.toSqlString(criteria, criteriaQuery) + ' ' + getOp()
				+ ' ' + rhs.toSqlString(criteria, criteriaQuery) + ' ';
	}

	public String getOp() {
		return op;
	}
	@Override
	public String toString() {
		return lhs.toString() + ' ' + getOp() + ' ' + rhs.toString();
	}
}
