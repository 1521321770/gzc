package com.gengzc.util;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OperationResult {
	
	private boolean flag;
	private String msgCode;
	private Object resData;

	public boolean isFlag() {
		return this.flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getMsgCode() {
		return this.msgCode;
	}

	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}

	@XmlElement
	public Object getResData() {
		return this.resData;
	}

	public void setResData(Object resData) {
		this.resData = resData;
	}
}