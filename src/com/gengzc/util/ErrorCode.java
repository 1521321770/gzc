package com.gengzc.util;

public class ErrorCode {
	
	public enum ControllerErr{
		
		/**
		 * 用户名或密码错误
		 */
		ERROR_GZC_USER_NOT_EXIST(10000),
		
		/**
		 * 数据库操作失败，对数据库进行各种增删改查时出现异常
		 */
		ERROR_GZC_DATABASE_ERROR(19000);

		private int value;
		
		private ControllerErr(int value){
			this.value = value;
		}
		
		public int getValue(){
			return value;
		}
		
		public String toString(){
			return this.value + "";
		}
	}
}
