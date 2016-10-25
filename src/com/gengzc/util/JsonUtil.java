package com.gengzc.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Json��javaBean֮���ת��������.
 *
 * @author Lvgj
 * ��Ҫ
 * json-lib-2.4-jdk15.jar
 * ezmorph-1.0.6.jar
 * commons-collections-3.1.jar
 * commons-lang-2.0.jar
 * ֧��
 * }
 */
public class JsonUtil {

    /**
     * ��һ��JSON �����ַ���ʽ�еõ�һ��java����.
     *
     * @param <T> the generic type
     * @param jsonString the json string
     * @param beanCalss the bean calss
     * @param classMap the class map
     * @return the t
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T jsonToBean(String jsonString, Class<T> beanCalss, Map<String, Class> classMap) {

        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        T bean = (T) JSONObject.toBean(jsonObject, beanCalss, classMap);

        return bean;

    }

    /**
     * ��java����ת����json�ַ���.
     *
     * @param bean
     *            the bean
     * @return the string
     */
    public static String beanToJson(Object bean) {

        JSONObject json = JSONObject.fromObject(bean);

        return json.toString();

    }

    /**
     * ��java����ת����json�ַ���.
     *
     * @param bean
     *            the bean
     * @param noryChanges
     *            the noryChanges
     * @param nory
     *            the nory
     * @return the string
     */
    public static String beanToJson(Object bean, String[] noryChanges,
            boolean nory) {

        JSONObject json = null;

        if (nory) { // ת��noryChanges�������

            Field[] fields = bean.getClass().getDeclaredFields();
            String str = "";
            for (Field field : fields) {
                // System.out.println(field.getName());
                str += (":" + field.getName());
            }
            fields = bean.getClass().getSuperclass().getDeclaredFields();
            for (Field field : fields) {
                // System.out.println(field.getName());
                str += (":" + field.getName());
            }
            str += ":";
            for (String s : noryChanges) {
                str = str.replace(":" + s + ":", ":");
            }
            json = JSONObject.fromObject(bean, configJson(str.split(":")));
        } else {    // ת������nory_changes�������
            json = JSONObject.fromObject(bean, configJson(noryChanges));
        }
        return json.toString();

    }

    /**
     * Config json.
     *
     * @param excludes
     *            the excludes
     * @return the json config
     */
    private static JsonConfig configJson(String[] excludes) {

        JsonConfig jsonConfig = new JsonConfig();

        jsonConfig.setExcludes(excludes);
        //
        jsonConfig.setIgnoreDefaultExcludes(false);
        //
        // jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

        // jsonConfig.registerJsonValueProcessor(Date.class,
        //
        // new DateJsonValueProcessor(datePattern));

        return jsonConfig;

    }

    /**
     * ��java����List����ת����json�ַ���.
     *
     * @param beans
     *            the beans
     * @return the string
     */
    @SuppressWarnings({ "rawtypes" })
    public static String beanListToJson(List beans) {

        StringBuffer rest = new StringBuffer();

        rest.append("[");

        int size = beans.size();

        for (int i = 0; i < size; i++) {

            rest.append(beanToJson(beans.get(i)) + ((i < size - 1) ? "," : ""));

        }

        rest.append("]");

        return rest.toString();

    }

    /**
     * Bean list to json.
     *
     * @param beans
     *            the beans
     * @param noryChanges
     *            the noryChanges
     * @param nory
     *            the nory
     * @return the string
     */
    @SuppressWarnings({ "rawtypes" })
    public static String beanListToJson(List beans, String[] noryChanges,
            boolean nory) {

        StringBuffer rest = new StringBuffer();

        rest.append("[");

        int size = beans.size();

        for (int i = 0; i < size; i++) {
            try {
                rest.append(beanToJson(beans.get(i), noryChanges, nory));
                if (i < size - 1) {
                    rest.append(",");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        rest.append("]");

        return rest.toString();

    }

    /**
     * ��json HASH���ʽ�л�ȡһ��map����map֧��Ƕ�׹���.
     *
     * @param jsonString
     *            the json string
     * @return the map
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map jsonToMap(String jsonString) {

        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        Iterator keyIter = jsonObject.keys();
        String key;
        Object value;
        Map valueMap = new HashMap();

        while (keyIter.hasNext()) {

            key = (String) keyIter.next();
            value = jsonObject.get(key).toString();
            valueMap.put(key, value);

        }

        return valueMap;
    }

    /**
     * map����ת����json��ʽ����.
     *
     * @param map
     *            the map
     * @param noryChanges
     *            the noryChanges
     * @param nory
     *            the nory
     * @return the string
     */
    public static String mapToJson(Map<String, ?> map, String[] noryChanges,
            boolean nory) {

        String sJson = "{";

        Set<String> key = map.keySet();
        for (Iterator<?> it = key.iterator(); it.hasNext();) {
            String s = (String) it.next();
            if (map.get(s) == null) {
                System.out.println("");
            } else if (map.get(s) instanceof List<?>) {
                sJson += (s + ":" + JsonUtil.beanListToJson(
                        (List<?>) map.get(s), noryChanges, nory));

            } else {
                JSONObject json = JSONObject.fromObject(map);
                sJson += (s + ":" + json.toString());
            }
            if (it.hasNext()) {
                sJson += ",";
            }
        }
        sJson += "}";
        return sJson;
    }

    /**
     * ��json�����еõ���Ӧjava����.
     *
     * @param jsonString
     *            the json string
     * @return the object[]
     */
    public static Object[] jsonToObjectArray(String jsonString) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        return jsonArray.toArray();

    }

    /**
     * List to json.
     *
     * @param list
     *            the list
     * @return the string
     */
    public static String listToJson(List<?> list) {

        JSONArray jsonArray = JSONArray.fromObject(list);

        return jsonArray.toString();

    }

    /**
     * ��json���󼯺ϱ��ʽ�еõ�һ��java�����б�.
     *
     * @param <T>
     *            the generic type
     * @param jsonString
     *            the json string
     * @param beanClass
     *            the bean class
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> jsonToBeanList(String jsonString,
            Class<T> beanClass) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        JSONObject jsonObject;
        T bean;
        int size = jsonArray.size();
        List<T> list = new ArrayList<T>(size);
        for (int i = 0; i < size; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            bean = (T) JSONObject.toBean(jsonObject, beanClass);
            list.add(bean);

        }
        return list;

    }

    /**
     * ��json�����н�����java�ַ�������.
     *
     * @param jsonString
     *            the json string
     * @return the string[]
     */
    public static String[] jsonToStringArray(String jsonString) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        String[] stringArray = new String[jsonArray.size()];
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            stringArray[i] = jsonArray.getString(i);
        }
        return stringArray;
    }

    /**
     * ��json�����н�����javaLong�Ͷ�������.
     *
     * @param jsonString
     *            the json string
     * @return the long[]
     */
    public static Long[] jsonToLongArray(String jsonString) {

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        int size = jsonArray.size();
        Long[] longArray = new Long[size];
        for (int i = 0; i < size; i++) {
            longArray[i] = jsonArray.getLong(i);
        }
        return longArray;
    }

    /**
     * ��json�����н�����java Integer�Ͷ�������.
     *
     * @param jsonString
     *            the json string
     * @return the integer[]
     */
    public static Integer[] jsonToIntegerArray(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        int size = jsonArray.size();
        Integer[] integerArray = new Integer[size];
        for (int i = 0; i < size; i++) {
            integerArray[i] = jsonArray.getInt(i);
        }
        return integerArray;

    }

    /**
     * ��json�����н�����java Double�Ͷ�������.
     *
     * @param jsonString
     *            the json string
     * @return the double[]
     */
    public static Double[] jsonToDoubleArray(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        int size = jsonArray.size();
        Double[] doubleArray = new Double[size];
        for (int i = 0; i < size; i++) {
            doubleArray[i] = jsonArray.getDouble(i);
        }
        return doubleArray;
    }

}
