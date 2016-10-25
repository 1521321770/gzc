package com.gengzc.controller.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gengzc.util.OperationResult;

public class Result {

	public Result(){

	}

	private static Log log = LogFactory.getLog(Result.class);

	public static OperationResult sendResult(String msgCode, boolean flag, Object resData){
		OperationResult result = new OperationResult();
		result.setMsgCode(msgCode);
		result.setFlag(flag);
		result.setResData(resData);
		log.debug(result);
		return result;
	}

}
