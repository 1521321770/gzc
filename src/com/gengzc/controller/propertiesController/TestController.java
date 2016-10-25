package com.gengzc.controller.propertiesController;

/**
 * 
 * @author Administrator
 *
 */
public class TestController {
	public static void main(String[] args) {
		String path = PropertiesIOUtil.getValue("path");
		System.out.println(path);
	}
}
