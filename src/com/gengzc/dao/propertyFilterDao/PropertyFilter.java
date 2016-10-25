package com.gengzc.dao.propertyFilterDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.util.Assert;

/**
 * 属性过滤器
 *
 * @author sun4love
 */
public class PropertyFilter {


	/** 属性比较类型. */
	public enum MatchType {

		/**
		 * 等于
		 */
		EQ,

		/**
		 * 等于另一属性
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
		 * 小于
		 */
		LT,

		/**
		 * 小于另一属性
		 */
		LTF,

		/**
		 * 大于
		 */
		GT,

		/**
		 * 大于另一属性
		 */		
		GTF,

		/**
		 * 小于等于
		 */
		LE,

		/**
		 * 小于等于另一属性
		 */
		LEF,

		/**
		 * 大于等于
		 */
		GE,

		/**
		 * 大于等于另一属性
		 */
		GEF,

		/**
		 * 
		 * 在两者之间
		 * 
		 */	
		BETWEEN,

		/**
		 * 
		 * 不等于
		 * 
		 */
		NE,

		/**
		 *
		 * 不等于另一属性
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
	 * values为具体类型值的构造函数
	 * 
	 * @param fieldName
	 *            属性名
	 * @param matchType
	 *            匹配类型 {@link MatchType}
	 * @param values
	 *            值数组，MatchType为BETWEEN类型时，长度必须是2，其他为1，值必须是具体类型的值，
	 *            如果是字符串需要转换类型，见另一个构造函数
	 *            {@link #PropertyFilter(String, MatchType, FieldType, Object...)}
	 */
	public PropertyFilter(final String fieldName, MatchType matchType,
			Object... values) {
		this.fieldName = fieldName;
		this.matchType = matchType;
		if (this.matchType == MatchType.BETWEEN
				&& (values == null || values.length != 2)) {
			throw new IllegalArgumentException(String.format(
					"%s属性选择MatchType.BETWEEN类型时，values参数长度必须为2", fieldName));
		}
		this.values = values;
		filters.add(this);
	}

	/**
	 * 
	 * values值需要转换类型的构造函数
	 * 
	 * @param fieldName
	 *            属性名
	 * @param matchType
	 *            匹配类型 {@link MatchType}
	 * @param fieldType
	 *            属性的类型，value将被转换到此类型
	 * @param values
	 *            值数组,BETWEEN类型时，长度必须是2，其他为1，值必须是具体类型的值， 如果是字符串需要转换类型，见另一个构造函数
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
					"%s属性选择MatchType.BETWEEN类型时，values参数长度必须为2", fieldName));
		}
		this.values = new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			this.values[i] = ConvertUtils.convert(values[i],
					fieldType.getValue());
		}
		filters.add(this);
	}

	/**
	 * 属性比较构造函数
	 * 
	 * @param fieldName
	 *            属性名
	 * @param matchType
	 *            条件类型
	 * @param otherField
	 *            其他属性
	 */
	public PropertyFilter(final String fieldName, String otherField,
			MatchType matchType) {
		this.fieldName = fieldName;
		this.matchType = matchType;
		this.otherField = otherField;
		filters.add(this);
	}

	/**
	 * 获取属性名
	 * @return fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * 向当前filter添加一个or联合过滤条件
	 * @param filter
	 * @return
	 */
	public PropertyFilter addOrFilter(PropertyFilter filter) {
		filter.or = true;
		filters.add(filter);
		return this;
	}

	/**
	 * 向当前filter添加一个or联合过滤条件，
	 * 过滤条件将作为一个整体,即将所有条件放入括号内
	 * @param filter
	 * @return
	 */
	public PropertyFilter addRoundOrFilter(PropertyFilter filter) {
		Assert.isTrue(filter == this, "PropertyFilter不允许添加自身");
		filter.roundOr = true;
		filters.add(filter);
		return this;
	}

	/**
	 * 向当前filter添加一个and联合过滤条件，
	 * @param filter
	 * @return
	 */
	public PropertyFilter addAndFilter(PropertyFilter filter) {
		Assert.isTrue(filter == this, "PropertyFilter不允许添加自身");
		filter.and = true;
		filters.add(filter);
		return this;
	}

	/**
	 * 
	 * 向当前filter添加一个and联合过滤条件，
	 * 过滤条件将作为一个整体,即将所有条件放入括号内
	 * @param filter
	 * @return
	 */
	public PropertyFilter addRoundAndFilter(PropertyFilter filter) {
		Assert.isTrue(filter == this, "PropertyFilter不允许添加自身");
		filter.roundAnd = true;
		filters.add(filter);
		return this;
	}

	/**
	 * 判断该filter是否是一个or联合过滤，见{@link #addOrFilter(PropertyFilter)}
	 * @return
	 */
	public boolean isOr() {
		return or;
	}

	/**
	 * 判断该filter是否是一个and联合过滤，见{@link #addAndFilter(PropertyFilter)}
	 * @return
	 */
	public boolean isAnd() {
		return and;
	}

	/**
	 * 判断该filter是否是一个or联合过滤, 见 {@link #addRoundOrFilter(PropertyFilter)}
	 * @return
	 */
	public boolean isRoundOr() {
		return roundOr;
	}

	/**
	 * 判断该filter是否是一个and联合过滤, 见 {@link #addRoundAndFilter(PropertyFilter)}
	 * @return
	 */
	public boolean isRoundAnd() {
		return roundAnd;
	}
	
	/**
	 * 判断该filter是否是一个联合过滤
	 * @return
	 */
	public boolean isMulti() {
		return !filters.isEmpty();
	}
	
	/**
	 * 获取属性的比较类型
	 * @return matchType
	 */
	public MatchType getMatchType() {
		return matchType;
	}
	
	/**
	 * 获取属性比较参数值集合
	 * @return values
	 */
	public Object[] getValues() {
		return values;
	}

	/**
	 * 联合filter迭代器
	 * 不支持删除操作
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
	 * 联合filter作为一个过滤条件
	 * @param filter
	 * @return
	 */
	public PropertyFilter joinFilter(PropertyFilter filter) {
		Assert.isTrue(filter == this, "PropertyFilter不允许添加自身");
		filters.add(filter);
		return this;
	}
	
	/**
	 * 其他field，两个属性比较时
	 * @return
	 */
	public String getOtherField() {
		return otherField;
	}
	
	/**
	 * 属性类型
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
				System.out.println("参数异常");
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
