package chat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.lang.Integer;
public class MyClass implements Interface1, Interface2 {

	@Override
	public void method2() {
	}

	@Override
	public void method1(String str) {
	}

	//MyClass won't compile without having it's 
        //own log() implementation
	@Override
	public void log(String str)
        {
		System.out.println("MyClass logging::"+str);
		Interface1.print("abc");
	}
        
        
        
       
Runnable r = new Runnable()
{
    @Override
    public void run() 
    {
            System.out.println("My Runnable");
    }
};
//we can write above implementation using 
//lambda expression as
Runnable r1 = () -> {
			System.out.println("My Runnable");
		};
//creating sample Collection
List<Integer> myList = new ArrayList<Integer>();
myList.forEach(new Consumer<Integer>() {

			public void accept(Integer t) {
				System.out.println("forEach anonymous class Value::"+t);
			}

		});

}

