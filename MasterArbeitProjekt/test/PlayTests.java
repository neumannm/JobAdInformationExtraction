import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

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
	
	@Test
	public void testWithSets(){
		Set<Integer> a = new TreeSet<>(Arrays.asList(new Integer[]{1,2,3}));
		Set<Integer> b = new TreeSet<>(Arrays.asList(new Integer[]{3,4,5}));

		System.out.println(a);
		System.out.println(b);
		
		method(a,b);
		
		System.out.println(a);
		System.out.println(b);
		
	}

	private void method(Set<Integer> x, Set<Integer> y) {
		System.out.println("In Method:");
		System.out.println(x);
		x.removeAll(y);		
		System.out.println("Leave method...");
	}

	@Test
	public void testSwap(){
		String var1 = "A"; // value "A"
		String var2 = "B"; // value "B"
		System.out.println("var1: " + var1);
		System.out.println("var2: " + var2);
		System.out.println("SWAP!");
		swap(var1, var2); // swaps their values!
		// now var1 has value "B" and var2 has value "A"
		System.out.println("var1: " + var1);
		System.out.println("var2: " + var2);
	}
	
	void swap(String arg1, String arg2) {
		String temp = arg1;
	    arg1 = arg2;
	    arg2 = temp;
	}
}
