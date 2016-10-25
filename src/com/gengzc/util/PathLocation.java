package com.gengzc.util;

import java.net.URL;

public class PathLocation {
	
	/**
     * 得到类的路径，例如E:/workspace/JavaGUI/bin/com/util
     * @return
     * @throws java.lang.Exception
     */
    public String getClassPath() throws Exception {
        try {
            String strClassName = getClass().getName();
            String strPackageName = "";
            if (getClass().getPackage() != null) {
                strPackageName = getClass().getPackage().getName();
            }
            String strClassFileName = "";
            if (!"".equals(strPackageName)) {
                strClassFileName = strClassName.substring(strPackageName.length() + 1,
                        strClassName.length());
            } else {
                strClassFileName = strClassName;
            }
            URL url = null;
            url = getClass().getResource(strClassFileName + ".class");
            String strURL = url.toString();
            strURL = strURL.substring(strURL.indexOf('/') + 1, strURL
                    .lastIndexOf('/'));
            //返回当前类的路径，并且处理路径中的空格，因为在路径中出现的空格如果不处理的话，
            //在访问时就会从空格处断开，那么也就取不到完整的信息了，这个问题在web开发中尤其要注意
            return strURL.replaceAll("%20", " ");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

	public static void main(String[] args) {
		 PathLocation pl = new PathLocation();
		 try {
			String path = pl.getClassPath();
			System.out.println(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
