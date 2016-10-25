package com.gengzc.util;

import java.net.URL;

public class PathLocation {
	
	/**
     * �õ����·��������E:/workspace/JavaGUI/bin/com/util
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
            //���ص�ǰ���·�������Ҵ���·���еĿո���Ϊ��·���г��ֵĿո����������Ļ���
            //�ڷ���ʱ�ͻ�ӿո񴦶Ͽ�����ôҲ��ȡ������������Ϣ�ˣ����������web����������Ҫע��
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
