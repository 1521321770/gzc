package com.gengzc.controller.upload.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

/**
 *工具类.
 *
 *@author gengzhichao
 */
public final class Utils {
    /**
     *默认构造函数.
     */
    private Utils() {
    }

    /**
     *取得当前线程信息.
     *
     *@return 当前线程信息
     */
    public static String getCurrentThreadInfo() {
        String info = Thread.currentThread().toString();
        // String info = "(Thread " + Thread.currentThread().getId() + ")";
        return info;
    }

    /**
     *显示所有HTTP请求参数.
     *
     *@param req
     *           HTTP请求对象
     */
    public static void showAllRequestParams(HttpServletRequest req) {
        Enumeration<String> paramNames = (Enumeration<String>) req.getParameterNames();
        if (paramNames != null) {
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = req.getParameter(paramName);
                System.out.print(paramName + ":" + paramValue + ",");
            }
            System.out.println();
        }
    }

    /**
     *显示所有HTTP header.
     *
     *@param req
     *           HTTP请求对象
     */
    public static void showAllHttpHeaders(HttpServletRequest req) {
        @SuppressWarnings("unchecked")
        Enumeration<String> reqHeaderNames = (Enumeration<String>) req.getHeaderNames();
        if (reqHeaderNames != null) {
            System.out.println("HTTP request headers:");
            while (reqHeaderNames.hasMoreElements()) {
                String reqHeaderName = reqHeaderNames.nextElement();
                String reqValue = req.getParameter(reqHeaderName);
                System.out.println(reqHeaderName + ":" + reqValue);
            }
        }
    }

    /**
     *设置HTTP响应属性.
     *
     *@param res
     *           HTTP响应对象
     */
    public static void setResponseAttributes(HttpServletResponse res) {
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache");
        res.setHeader("Expires", "0");
        res.setHeader("Content-Type", "text/xml; charset=utf-8");
        res.setCharacterEncoding("utf-8");
    }

    /**
     *将字符串转换为整型(如果字符串为空或非数字，则返回0).
     *
     *@param str
     *           待转换字符串
     *@return 转换后的整型数字
     */
    public static int parseInt(String str) {
        if (str != null && !"".equals(str)) {
            str = str.trim();
            if (StringUtils.isNumeric(str)) {
                return Integer.parseInt(str);
            }
        }
        return 0;
    }

    /**
     *将字符串转换为长整型(如果字符串为空或非数字，则返回0L).
     *
     *@param str
     *           待转换字符串
     *@return 转换后的长整型数字
     */
    public static long parseLong(String str) {
        if (str != null && !"".equals(str)) {
            str = str.trim();
            if (StringUtils.isNumeric(str)) {
                return Long.parseLong(str);
            }
        }
        return 0L;
    }

    /**
     *取得HTTP请求中的指定键值的字符串参数(如果参数不存在或为空，则返回空字符串).
     *
     *@param req
     *           HTTP请求
     *@param key
     *           参数键值
     *@return 取得的字符串参数
     */
    public static String getStrFromRequest(HttpServletRequest req, String key) {
        String str = req.getParameter(key);
        if (str != null && !"".equals(str)) {
            try {
                str = URLDecoder.decode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return str.trim();
        }
        return "";
    }

    /**
     *取得Session中的指定键值的字符串参数(如果参数不存在或为空，则返回空字符串).
     *
     *@param req
     *           HTTP请求
     *@param key
     *           参数键值
     *@return 取得的字符串参数
     */
    public static String getStrFromSession(HttpServletRequest req, String key) {
        String str = (String) req.getSession().getAttribute(key);
        if (str != null && !"".equals(str)) {
            return str.trim();
        }
        return "";
    }

    /**
     *取得Session中的指定键值的字符串参数(如果参数不存在或为空，则返回空字符串).
     *
     *@param session
     *           session
     *@param key
     *           参数键值
     *@return 取得的字符串参数
     */
    public static String getStrFromSession(HttpSession session, String key) {
        String str = (String) session.getAttribute(key);
        if (str != null && !"".equals(str)) {
            return str.trim();
        }
        return "";
    }

    /**
     *取得HTTP请求中的指定键值的整型参数.
     *
     *@param req
     *           HTTP请求
     *@param key
     *           参数键值
     *@return 取得的整型参数
     */
    public static int getIntFromRequest(HttpServletRequest req, String key) {
        String str = req.getParameter(key);
        return Utils.parseInt(str);
    }

    /**
     *取得HTTP请求中的指定键值的长整型参数.
     *
     *@param req
     *           HTTP请求
     *@param key
     *           参数键值
     *@return 取得的长整型参数
     */
    public static long getLongFromRequest(HttpServletRequest req, String key) {
        String str = req.getParameter(key);
        return Utils.parseLong(str);
    }

    /**
     *取得HTTP请求中的指定键值的浮点型参数.
     *
     *@param req
     *           HTTP请求
     *@param key
     *           参数键值
     *@return 取得的浮点型参数
     */
    public static float getFloatFromRequest(HttpServletRequest req, String key) {
        String str = req.getParameter(key);
        return Float.parseFloat(str);
    }

    /**
     *取得HTTP请求中的指定键值的浮点型参数.
     *
     *@param req
     *           HTTP请求
     *@param key
     *           参数键值
     *@return 取得的浮点型参数
     */
    public static double getDoubleFromRequest(HttpServletRequest req, String key) {
        String str = req.getParameter(key);
        return Double.parseDouble(str);
    }

    /**
     *数字格式.
     */
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
}

