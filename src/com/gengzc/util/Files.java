package com.gengzc.util;

import org.apache.log4j.Logger;


public class Files {
	
	private static final Logger LOGGER = Logger.getLogger(Files.class);
	
	public static String toPath(String... args) {
		StringBuilder strBuilder = new StringBuilder();
		if (Params.isArray(args)) {
			for(String arg:args) {
				if(arg != null) {
					strBuilder.append(arg + "/");
				}
			}
		}
		String str = strBuilder.toString();
		if (str.length() > 0) {
			str = str.substring(0, str.length()-1);
		}
		LOGGER.info("File.toPath(String... args): " + str);
		return str;
	}
	
}
