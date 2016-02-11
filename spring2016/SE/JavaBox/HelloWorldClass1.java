import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.lang.Comparable;

public class HelloWorldClass1 implements Comparable<HelloWorldClass1>{

	public int myWorld;
	public HelloWorldClass1(int i){
		myWorld = i;
	}


	public int comprateTo(HelloWorldClass1 o){
		Integer a = new Integer(this.myWorld);
		Integer b = new Integer(o.myWorld);

		return a.compareTo(b);
	}

	public static void main(String[] args){
		List<HelloWorldClass1> list = new ArrayList<HelloWorldClass1>();
		//Program to the interface not implementation. Here when you define class type list you  can use any list that implements that list interface. 
		// For example Array lust


		for (int i = 0; i < 10; i++){
			HelloWorldClass1 myObject = new HelloWorldClass1(i);
			list.add(myObject);
		}

		HelloWorldClass1 key = new HelloWorldClass1(5);
		// in order to make sure you can compare you extend hello world into compareable.
		int found = Collections.binarySearch(list,key);
		System.out.println(found);
		
		// for (HelloWorldClass1 myObject : list){
		// 	System.out.println(myObject.myWorld);
		// }

		Iterator<HelloWorldClass1> itr = list.iterator();
		while(itr.hasNext()){
			HelloWorldClass1 myObject = itr.next();
			if (myObject.myWorld < 5){
				itr.remove();
			}
			System.out.println(myObject.myWorld);		
		}



		// System.out.println(list.size());
	}
}
