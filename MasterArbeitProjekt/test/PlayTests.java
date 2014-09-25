import org.junit.Test;


public class PlayTests {
	static int a = 0, b=0;

	@Test
	public void test() {
		
		calculate();
		
		System.out.println(a);
		System.out.println(b);
		
		String abc = "abc";
		String[] split = abc.split("d");
		for (int i = 0; i < split.length; i++) {
			System.out.println(">"+split[i]);
		}
	}

	private void calculate() {
		for (int i = 0; i < 10; i++) {
			a+=i;
			b++;
		}		
	}

}
