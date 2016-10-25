package exception;

public class ExceptionTest {
	public static void main(String[] args) {
        try {
            int i = 10/0;
              System.out.println("i="+i); 
        } catch (ArithmeticException e) {
            System.out.println("e.getMessage(): " + e.getMessage()); 
            System.out.println("e.toString(): " + e.toString()); 
            System.out.println("e==>" + e);
            e.printStackTrace(); 
        }
    }
	
	public void test() throws Exception{
		try {
			int i = 10/0;
	        System.out.println("i="+i);
		} catch (ArithmeticException e) {
			String msg = "aaaaaaa";
           throw new Exception(msg, e);
      }
		
	}
}
