package com.gengzc.model.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class BaoMaTest {

	@Test
	public void testBaoMa() {
		System.out.println("--------------------------");
		//测试宝马1
		BaoMaTemplate BMW1 = new BaoMa1();
		BMW1.run();
		
		System.out.println("\n==========================\n");
//		System.out.println("\n***********宝马H1奇葩逻辑法*********\n");
//		BMW1.run2();
		//测试宝马2
		BaoMaTemplate BMW2 = new BaoMa2();
		BMW2.run();
//		System.out.println("\n***********宝马H2奇葩逻辑法*********\n");
//		BMW2.run1();
		System.out.println("--------------------------\n");
	}
	
	@Test
	public void testBaoMa1ByClient() throws IOException {
		System.out.println("------------------------------------");
		//测试宝马1
		BaoMa1 BMW1 = new BaoMa1();
		System.out.println("宝马H1型号的悍马是否需要喇叭声响？0-不需要 1-需要");
		String type = (new BufferedReader(new InputStreamReader(System.in))).readLine();
		if (type.equals("0")) {
			BMW1.setAlarm(false);
		} else {
			BMW1.setAlarm(true);
		}
		BMW1.run();
		
		System.out.println("\n===================================\n");
		
		BaoMa2 BMW2 = new BaoMa2();
		System.out.println("宝马H2型号的悍马是否需要喇叭声响？0-不需要 1-需要");
		type = (new BufferedReader(new InputStreamReader(System.in))).readLine();
		if (type.equals("0")) {
			BMW2.setAlarm(false);
		} else {
			BMW2.setAlarm(true);
		}
		BMW2.run();
		System.out.println("------------------------------------\n");
	}
}
