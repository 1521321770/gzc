package com.gengzc.controller.propertiesController;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * PropertiesIOUtil.
 * @author Administrator
 * @version v1
 *
 */
public class PropertiesIOUtil {
	/**
	 * 132.
	 */
	private static Properties props;

	private static String path = "gzc.properties";
	
	static{
		props = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		try{
			props.load(in);
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private PropertiesIOUtil(){

	}

	public static String getValue(String key) {
		String value = props.getProperty(key);
		String pathValue = "";
		if (value != null) {
			pathValue = value;
		}
		return pathValue;
	}

	public static void setValue(String key, String value) throws Exception {
		props.setProperty(key, value);
		try {
			FileOutputStream fos = new FileOutputStream(Thread.currentThread().getContextClassLoader().getResource(path).getPath());
			props.store(fos, "set" + key + "=" + value);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new Exception("fail to set " + key + "=" + value);
		}
	}
}