package com.gengzc.util;

public class ErrorCode {
	
	public enum ControllerErr{
		
		/**
		 * �û������������
		 */
		ERROR_GZC_USER_NOT_EXIST(10000),
		
		/**
		 * ���ݿ����ʧ�ܣ������ݿ���и�����ɾ�Ĳ�ʱ�����쳣
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
