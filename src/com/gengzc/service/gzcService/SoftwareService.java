package com.gengzc.service.gzcService;

import java.io.Serializable;

import com.gengzc.bean.Software;
import com.gengzc.util.exception.CloudBusinessException;

public interface SoftwareService {
	
	Serializable save(Software software) throws CloudBusinessException;
}
