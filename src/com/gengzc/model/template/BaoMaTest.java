package com.gengzc.model.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class BaoMaTest {

	@Test
	public void testBaoMa() {
		System.out.println("--------------------------");
		//���Ա���1
		BaoMaTemplate BMW1 = new BaoMa1();
		BMW1.run();
		
		System.out.println("\n==========================\n");
//		System.out.println("\n***********����H1�����߼���*********\n");
//		BMW1.run2();
		//���Ա���2
		BaoMaTemplate BMW2 = new BaoMa2();
		BMW2.run();
//		System.out.println("\n***********����H2�����߼���*********\n");
//		BMW2.run1();
		System.out.println("--------------------------\n");
	}
	
	@Test
	public void testBaoMa1ByClient() throws IOException {
		System.out.println("------------------------------------");
		//���Ա���1
		BaoMa1 BMW1 = new BaoMa1();
		System.out.println("����H1�ͺŵĺ����Ƿ���Ҫ�������죿0-����Ҫ 1-��Ҫ");
		String type = (new BufferedReader(new InputStreamReader(System.in))).readLine();
		if (type.equals("0")) {
			BMW1.setAlarm(false);
		} else {
			BMW1.setAlarm(true);
		}
		BMW1.run();
		
		System.out.println("\n===================================\n");
		
		BaoMa2 BMW2 = new BaoMa2();
		System.out.println("����H2�ͺŵĺ����Ƿ���Ҫ�������죿0-����Ҫ 1-��Ҫ");
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
