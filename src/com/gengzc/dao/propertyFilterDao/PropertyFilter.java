package com.gengzc.dao.propertyFilterDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.util.Assert;

/**
 * ���Թ�����
 *
 * @author sun4love
 */
public class PropertyFilter {


	/** ���ԱȽ�����. */
	public enum MatchType {

		/**
		 * ����
		 */
		EQ,

		/**
		 * ������һ����
		 */
		EQF,

		/**
		 * like 'value'
		 */
		LIKE,

		/**
		 * like '%value'
		 */
		LIKE_START,

		/**
		 * like 'value%'
		 */
		LIKE_END,

		/**
		 * like '%value%'
		 */
		LIKE_ANYWHERE,

		/**
		 * С��
		 */
		LT,

		/**
		 * С����һ����
		 */
		LTF,

		/**
		 * ����
		 */
		GT,

		/**
		 * ������һ����
		 */		
		GTF,

		/**
		 * С�ڵ���
		 */
		LE,

		/**
		 * С�ڵ�����һ����
		 */
		LEF,

		/**
		 * ���ڵ���
		 */
		GE,

		/**
		 * ���ڵ�����һ����
		 */
		GEF,

		/**
		 * 
		 * ������֮��
		 * 
		 */	
		BETWEEN,

		/**
		 * 
		 * ������
		 * 
		 */
		NE,

		/**
		 *
		 * ��������һ����
		 *
		 */
		NEF
	}

	private String fieldName;
	private String otherField;
	private MatchType matchType;
	private boolean or;
	private boolean and;
	private boolean roundOr;
	private boolean roundAnd;
	private Object[] values;
	private List<PropertyFilter> filters = new ArrayList<PropertyFilter>();

	/**
	 * valuesΪ��������ֵ�Ĺ��캯��
	 * 
	 * @param fieldName
	 *            ������
	 * @param matchType
	 *            ƥ������ {@link MatchType}
	 * @param values
	 *            ֵ���飬MatchTypeΪBETWEEN����ʱ�����ȱ�����2������Ϊ1��ֵ�����Ǿ������͵�ֵ��
	 *            ������ַ�����Ҫת�����ͣ�����һ�����캯��
	 *            {@link #PropertyFilter(String, MatchType, FieldType, Object...)}
	 */
	public PropertyFilter(final String fieldName, MatchType matchType,
			Object... values) {
		this.fieldName = fieldName;
		this.matchType = matchType;
		if (this.matchType == MatchType.BETWEEN
				&& (values == null || values.length != 2)) {
			throw new IllegalArgumentException(String.format(
					"%s����ѡ��MatchType.BETWEEN����ʱ��values�������ȱ���Ϊ2", fieldName));
		}
		this.values = values;
		filters.add(this);
	}

	/**
	 * 
	 * valuesֵ��Ҫת�����͵Ĺ��캯��
	 * 
	 * @param fieldName
	 *            ������
	 * @param matchType
	 *            ƥ������ {@link MatchType}
	 * @param fieldType
	 *            ���Ե����ͣ�value����ת����������
	 * @param values
	 *            ֵ����,BETWEEN����ʱ�����ȱ�����2������Ϊ1��ֵ�����Ǿ������͵�ֵ�� ������ַ�����Ҫת�����ͣ�����һ�����캯��
	 *            {@link #FieldFilter(String, MatchType, FieldType, Object...)}
	 */
	public PropertyFilter(final String fieldName, MatchType matchType,
			FieldType fieldType, Object... values) {
		this.fieldName = fieldName;
		this.matchType = matchType;
		Assert.notEmpty(values);
		if (this.matchType == MatchType.BETWEEN
				&& (values == null || values.length != 2)) {
			throw new IllegalArgumentException(String.format(
					"%s����ѡ��MatchType.BETWEEN����ʱ��values�������ȱ���Ϊ2", fieldName));
		}
		this.values = new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			this.values[i] = ConvertUtils.convert(values[i],
					fieldType.getValue());
		}
		filters.add(this);
	}

	/**
	 * ���ԱȽϹ��캯��
	 * 
	 * @param fieldName
	 *            ������
	 * @param matchType
	 *            ��������
	 * @param otherField
	 *            ��������
	 */
	public PropertyFilter(final String fieldName, String otherField,
			MatchType matchType) {
		this.fieldName = fieldName;
		this.matchType = matchType;
		this.otherField = otherField;
		filters.add(this);
	}

	/**
	 * ��ȡ������
	 * @return fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * ��ǰfilter���һ��or���Ϲ�������
	 * @param filter
	 * @return
	 */
	public PropertyFilter addOrFilter(PropertyFilter filter) {
		filter.or = true;
		filters.add(filter);
		return this;
	}

	/**
	 * ��ǰfilter���һ��or���Ϲ���������
	 * ������������Ϊһ������,����������������������
	 * @param filter
	 * @return
	 */
	public PropertyFilter addRoundOrFilter(PropertyFilter filter) {
		Assert.isTrue(filter == this, "PropertyFilter�������������");
		filter.roundOr = true;
		filters.add(filter);
		return this;
	}

	/**
	 * ��ǰfilter���һ��and���Ϲ���������
	 * @param filter
	 * @return
	 */
	public PropertyFilter addAndFilter(PropertyFilter filter) {
		Assert.isTrue(filter == this, "PropertyFilter�������������");
		filter.and = true;
		filters.add(filter);
		return this;
	}

	/**
	 * 
	 * ��ǰfilter���һ��and���Ϲ���������
	 * ������������Ϊһ������,����������������������
	 * @param filter
	 * @return
	 */
	public PropertyFilter addRoundAndFilter(PropertyFilter filter) {
		Assert.isTrue(filter == this, "PropertyFilter�������������");
		filter.roundAnd = true;
		filters.add(filter);
		return this;
	}

	/**
	 * �жϸ�filter�Ƿ���һ��or���Ϲ��ˣ���{@link #addOrFilter(PropertyFilter)}
	 * @return
	 */
	public boolean isOr() {
		return or;
	}

	/**
	 * �жϸ�filter�Ƿ���һ��and���Ϲ��ˣ���{@link #addAndFilter(PropertyFilter)}
	 * @return
	 */
	public boolean isAnd() {
		return and;
	}

	/**
	 * �жϸ�filter�Ƿ���һ��or���Ϲ���, �� {@link #addRoundOrFilter(PropertyFilter)}
	 * @return
	 */
	public boolean isRoundOr() {
		return roundOr;
	}

	/**
	 * �жϸ�filter�Ƿ���һ��and���Ϲ���, �� {@link #addRoundAndFilter(PropertyFilter)}
	 * @return
	 */
	public boolean isRoundAnd() {
		return roundAnd;
	}
	
	/**
	 * �жϸ�filter�Ƿ���һ�����Ϲ���
	 * @return
	 */
	public boolean isMulti() {
		return !filters.isEmpty();
	}
	
	/**
	 * ��ȡ���ԵıȽ�����
	 * @return matchType
	 */
	public MatchType getMatchType() {
		return matchType;
	}
	
	/**
	 * ��ȡ���ԱȽϲ���ֵ����
	 * @return values
	 */
	public Object[] getValues() {
		return values;
	}

	/**
	 * ����filter������
	 * ��֧��ɾ������
	 * @return
	 */
	public Iterator<PropertyFilter> iterator() {
		return new Iterator<PropertyFilter>() {
			private Iterator<PropertyFilter> it = filters.iterator();

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public PropertyFilter next() {
				return it.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * ����filter��Ϊһ����������
	 * @param filter
	 * @return
	 */
	public PropertyFilter joinFilter(PropertyFilter filter) {
		Assert.isTrue(filter == this, "PropertyFilter�������������");
		filters.add(filter);
		return this;
	}
	
	/**
	 * ����field���������ԱȽ�ʱ
	 * @return
	 */
	public String getOtherField() {
		return otherField;
	}
	
	/**
	 * ��������
	 */
	public enum FieldType {
		S(String.class), D(Date.class), I(Integer.class), DO(
				Double.class), L(Long.class), B(Boolean.class);
		private Class<?> clazz;

		private FieldType(Class<?> clazz) {
			this.clazz = clazz;
		}

		public Class<?> getValue() {
			return clazz;
		}
	}

	public static PropertyFilter getPropertyFilter(String para){
		
//		String para1 = "username_EQ_wang";
//        String para2 = "password_EQ_S_123";
//        String para3 = "password_BETWEEN_S_123_456";
//        String para4 = "password_BETWEEN_123_456";
//        String para5 = "id_fileId_EQ";
        
        PropertyFilter pro = null;
        FieldType ft = null;
        MatchType mt = null;
		String[] paras = para.split("_");
		
		if(paras.length >= 3){
			ft = getFieldType(paras);
			switch(paras[1]){			
			case "EQ":
				mt = MatchType.EQ;
				break;
			case "EQF":
				mt = MatchType.EQF;
				break;
			case "LIKE":
				mt = MatchType.LIKE;
				break;
			case "LIKE_START":
				mt = MatchType.LIKE_START;
				break;
			case "LIKE_END":
				mt = MatchType.LIKE_END;
				break;
			case "LIKE_ANYWHERE":
				mt = MatchType.LIKE_ANYWHERE;
				break;
			case "LT":
				mt = MatchType.LT;
				break;
			case "LTF":
				mt = MatchType.LTF;
				break;
			case "GT":
				mt = MatchType.GT;
				break;
			case "GTF":
				mt = MatchType.GTF;
				break;
			case "LE":
				mt = MatchType.LE;
				break;
			case "LEF":
				mt = MatchType.LEF;
				break;
			case "GE":
				mt = MatchType.GE;
				break;
			case "GEF":
				mt = MatchType.GEF;
				break;
			case "NE":
				mt = MatchType.NE;
				break;
			case "NEF":
				mt = MatchType.NEF;
				break;
			case "BETWEEN":
				mt = MatchType.BETWEEN;
				if(ft != null && paras.length >= 5){
					pro = new PropertyFilter(paras[0], mt, ft, paras[3], paras[4]);
				}else if(paras.length >= 4){
					pro = new PropertyFilter(paras[0], mt, paras[2], paras[3]);
				}
				return pro;
			default:
				System.out.println("�����쳣");
				return pro;
			}
			
			if(ft != null && paras.length >= 4){
				pro = new PropertyFilter(paras[0], mt, ft, paras[3]);
			}else{
				pro = new PropertyFilter(paras[0], mt, paras[2]);
			}
			return pro;
		}
		return pro;
			
	}

	public static FieldType getFieldType(String[] paras){
		FieldType ft = null;
		switch(paras[2]){		
		case "S":
			ft = FieldType.S;
			break;
		case "D":
			ft = FieldType.D;
			break;
		case "I":
			ft = FieldType.I;
			break;
		case "DO":
			ft = FieldType.DO;
			break;
		case "L":
			ft = FieldType.L;
			break;
		case "B":
			ft = FieldType.B;
			break;
		}
		return ft;
	}
	
}
