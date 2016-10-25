package exception;

import static org.junit.Assert.*;

public class Test {

	@org.junit.Test
	public void test() {
		String name = "";
		name = "domain_geng&domain_test&";
		if(!"".equals(name)){
			System.out.println(name.substring(0, name.length() - 1));
		}

		System.out.println(name);
	}
}
