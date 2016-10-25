package com.gengzc.util;

public class Params {
	
	public static <T> boolean isArray(T[] t) {
		if(t == null || t.length == 0){
			return false;
		}
		return true;
	}
}
