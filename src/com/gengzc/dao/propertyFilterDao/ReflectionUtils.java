package com.gengzc.dao.propertyFilterDao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
	021
 * ���乤����.

 *

 * �ṩ����˽�б���,��ȡ��������Class, ��ȡ������Ԫ�ص�����, ת���ַ����������Util����.

 *

 */

public class ReflectionUtils {
 
    private static Log log = LogFactory.getLog(ReflectionUtils.class);
 
    static {
        DateConverter dc = new DateConverter();
        dc.setUseLocaleFormat(true);
        dc.setPatterns(new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" });
        ConvertUtils.register(dc, Date.class);
    }
 
    /**
     * ����Getter����.
     */
    public static Object invokeGetterMethod(Object target, String propertyName) {
        String getterMethodName = "get" + StringUtils.capitalize(propertyName);
        return invokeMethod(target, getterMethodName, new Class[] {}, new Object[] {});
    }
 
    /**
     * ����Setter����.ʹ��value��Class������Setter����.
     */
    public static void invokeSetterMethod(Object target, String propertyName, Object value) {
        invokeSetterMethod(target, propertyName, value, null);
    }
 
    /**
     * ����Setter����.
     *
     * @param propertyType ���ڲ���Setter����,Ϊ��ʱʹ��value��Class���.
     */
    public static void invokeSetterMethod(Object target, String propertyName, Object value, Class<?> propertyType) {
        Class<?> type = propertyType != null ? propertyType : value.getClass();
        String setterMethodName = "set" + StringUtils.capitalize(propertyName);
        invokeMethod(target, setterMethodName, new Class[] { type }, new Object[] { value });
    }
 
    /**
     * ֱ�Ӷ�ȡ��������ֵ, ����private/protected���η�, ������getter����.
     */
    public static Object getFieldValue(final Object object, final String fieldName) {

        Field field = getDeclaredField(object, fieldName);

        if (field == null) {

            throw new IllegalArgumentException("Could not find field [" + fieldName +"] on target [" + object + "]");

        }
        makeAccessible(field);

 

        Object result = null;

        try {

            result = field.get(object);

        } catch (IllegalAccessException e) {

            log.error("�������׳����쳣{}" + e.getMessage());

        }

        return result;

    }
    /**

     * ֱ�����ö�������ֵ, ����private/protected���η�, ������setter����.

     */
    public static void setFieldValue(final Object object, final String fieldName, final Object value) {

        Field field = getDeclaredField(object, fieldName);

        if (field == null) {

            throw new IllegalArgumentException("Could not find field [" + fieldName +"] on target [" + object + "]");

        }
        makeAccessible(field);
        try {

            field.set(object, value);

        } catch (IllegalAccessException e) {

        	log.error("�������׳����쳣:{}" + e.getMessage());

        }

    }
    /**

     * ֱ�ӵ��ö��󷽷�, ����private/protected���η�.

     */
    public static Object invokeMethod(final Object object, final String methodName,final Class<?>[] parameterTypes,

            final Object[] parameters) {

        Method method = getDeclaredMethod(object, methodName, parameterTypes);

        if (method == null) {

            throw new IllegalArgumentException("Could not find method [" + methodName +"] on target [" + object + "]");

        }
        method.setAccessible(true);
        try {

            return method.invoke(object, parameters);

        } catch (Exception e) {

            throw convertReflectionExceptionToUnchecked(e);

        }

    }
    /**

     * ѭ������ת��, ��ȡ�����DeclaredField.

     *

     * ������ת�͵�Object���޷��ҵ�, ����null.

     */
    protected static Field getDeclaredField(final Object object, final String fieldName) {

        Assert.notNull(object, "object����Ϊ��");

        Assert.hasText(fieldName, "fieldName");

        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass

                .getSuperclass()) {

            try {

                return superClass.getDeclaredField(fieldName);

            } catch (NoSuchFieldException e) {//NOSONAR

                // Field���ڵ�ǰ�ඨ��,��������ת��

            }

        }

        return null;
    }
    /**
     * ǿ������Field�ɷ���.
     */

    protected static void makeAccessible(final Field field) {

        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {

            field.setAccessible(true);
        }
    }
    /**

     * ѭ������ת��, ��ȡ�����DeclaredMethod.

     *

     * ������ת�͵�Object���޷��ҵ�, ����null.

     */

    protected static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {

        Assert.notNull(object, "object����Ϊ��");
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass

                .getSuperclass()) {

            try {

                return superClass.getDeclaredMethod(methodName, parameterTypes);

            } catch (NoSuchMethodException e) {//NOSONAR

                // Method���ڵ�ǰ�ඨ��,��������ת��

            }

        }

        return null;

    }
    /**

     * ͨ������, ���Class�����������ĸ���ķ��Ͳ���������.

     * ���޷��ҵ�, ����Object.class.

     * eg.

     * public UserDao extends HibernateDao<User>

     *

     * @param clazz The class to introspect

     * @return the first generic declaration, or Object.class if cannot be determined

     */

    @SuppressWarnings("unchecked")

    public static <T> Class<T> getSuperClassGenricType(final Class clazz) {

        return getSuperClassGenricType(clazz, 0);

    }
    /**

     * ͨ������, ��ö���Classʱ�����ĸ���ķ��Ͳ���������.

     * ���޷��ҵ�, ����Object.class.

     *

     * ��public UserDao extends HibernateDao<User,Long>

     *

     * @param clazz clazz The class to introspect

     * @param index the Index of the generic ddeclaration,start from 0.

     * @return the index generic declaration, or Object.class if cannot be determined

     */

    @SuppressWarnings("unchecked")

    public static Class getSuperClassGenricType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");

            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {

        	log.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "

                    + params.length);

            return Object.class;

        }

        if (!(params[index] instanceof Class)) {

        	log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");

            return Object.class;
        }
        return (Class) params[index];
    }
    /**
     * ��ȡ�����еĶ��������(ͨ��getter����), ��ϳ�List.
     *
     * @param collection ��Դ����.
     * @param propertyName Ҫ��ȡ��������.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List convertElementPropertyToList(final Collection collection, final String propertyName) {

        List list = new ArrayList();
        try {
            for (Object obj : collection) {
                list.add(PropertyUtils.getProperty(obj, propertyName));
            }
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
        return list;
    }
    /**
239
     * ��ȡ�����еĶ��������(ͨ��getter����), ��ϳ��ɷָ���ָ����ַ���.
240
     *
241
     * @param collection ��Դ����.
242
     * @param propertyName Ҫ��ȡ��������.
243
     * @param separator �ָ���.
244
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String convertElementPropertyToString(final Collection collection,final String propertyName,

            final String separator) {
        List list = convertElementPropertyToList(collection, propertyName);
        return StringUtils.join(list, separator);

    }
    
    /**
     * ת���ַ�������Ӧ����.
     *
     * @param value ��ת�����ַ���
     * @param toType ת��Ŀ������
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertStringToObject(String value, Class<T> toType) {
        try {
            return (T) ConvertUtils.convert(value, toType);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }
    /**

     * ������ʱ��checked exceptionת��Ϊunchecked exception.

     */

    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {

        return convertReflectionExceptionToUnchecked(null, e);

    }

 

    public static RuntimeException convertReflectionExceptionToUnchecked(String desc, Exception e) {

        desc = (desc == null) ? "Unexpected Checked Exception." : desc;

        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException

                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(desc, e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(desc, ((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException(desc, e);
    }
    public static final <T> T getNewInstance(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
