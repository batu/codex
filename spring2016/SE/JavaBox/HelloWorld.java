import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class HelloWorld{

	public int myWorld;
	public HelloWorld(int i){
		myWorld = i;
	}

	public static void main(String[] args){
		List<HelloWorld> list = new ArrayList<HelloWorld>();
		//Program to the interface not implementation. Here when you define class type list you  can use any list that implements that list interface. 
		// For example Array lust

		for (int i = 0; i < 10; i++){
			HelloWorld myObject = new HelloWorld(i);
			list.add(myObject);
		}
		
		// for (HelloWorld myObject : list){
		// 	System.out.println(myObject.myWorld);
		// }

		Iterator<HelloWorld> itr = list.iterator();
		while(itr.hasNext()){
			HelloWorld myObject = itr.next();
			if (myObject.myWorld < 5){
				itr.remove();
			}
			System.out.println(myObject.myWorld);		
		}

		

		// System.out.println(list.size());
	}
}
