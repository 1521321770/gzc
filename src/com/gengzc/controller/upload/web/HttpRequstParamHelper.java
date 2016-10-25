package com.gengzc.controller.upload.web;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

public final class HttpRequstParamHelper {
	
	private HttpRequstParamHelper() {
	}

	public static JSONObject getParams(HttpServletRequest req) {
		JSONObject result = new JSONObject();
		Map<String, String[]> paramMap = (Map<String, String[]>) req.getParameterMap();
		Set<String> paramNameSet = paramMap.keySet();
		for (String paramName : paramNameSet) {
			result.put(paramName, req.getParameter(paramName));
		}
		if (result.containsKey("data")) {
			String strData = result.getString("data");
			JSONObject jsonData = JSONObject.fromObject(strData);
			if (jsonData != null && !jsonData.isEmpty()) {
				result.put("data", jsonData);
			}
		}
		return result;
	}
}