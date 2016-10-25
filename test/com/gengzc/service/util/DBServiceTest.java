package com.gengzc.service.util;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class DBServiceTest {

	/**
	 * ApplicationContext object.
	 */
	ApplicationContext cxt = null;

	/**
	 * DBService object.
	 */
	private DBService dbService;

	@Before
	public void setUp() throws Exception {
		if (cxt == null) {
			cxt = new FileSystemXmlApplicationContext(
					"/WebContent/WEB-INF/spring-configuration/applicationContext-junit.xml");
			dbService = (DBService) cxt.getBean("dbService");
		}
	}
	@Test
	public void testDefaultDB() {
		dbService.defaultDB();
	}

	@Test
	public void testSaveSoftware() {
		dbService.saveSoftware();
	}
}
