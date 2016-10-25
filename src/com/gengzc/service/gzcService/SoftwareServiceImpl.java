package com.gengzc.service.gzcService;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gengzc.bean.Software;
import com.gengzc.dao.propertyFilterDao.HibernateDao;
import com.gengzc.util.exception.CloudBusinessException;

@Service("softwareService")
public class SoftwareServiceImpl implements SoftwareService{

	private static Log log = LogFactory.getLog(SoftwareServiceImpl.class);
	
	@Autowired
	private HibernateDao<Software, String> softwareDao;
	
	@Override
	public Serializable save(Software software) throws CloudBusinessException{
		try{
			Serializable serializable = null;
			if (software != null) {
				System.out.println(software.getName());
				serializable = softwareDao.getHibernateTemplate().save(software);
			}
			return serializable;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("save failed!", e);
			throw new CloudBusinessException(e.getMessage(), e);
		}
		
	}

}
