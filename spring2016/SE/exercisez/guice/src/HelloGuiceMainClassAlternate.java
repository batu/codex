import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class HelloGuiceMainClassAlternate {

    public static void main(String[] args) {
	HelloGuiceMainClassAlternate helloGuiceMainClass =
	    new HelloGuiceMainClassAlternate();
	
	Module module = new HelloGuiceModule();
	Injector injector = Guice.createInjector(module);
	HelloGuiceService helloGuiceService =
	    injector.getInstance(HelloGuiceService.class);
	
	helloGuiceMainClass.helloGuice(helloGuiceService);
    }
    
    private void helloGuice(HelloGuiceService helloGuiceService) {
	String testStr = helloGuiceService.serviceMethod("Hello Guice!");
	System.out.println(testStr);
    }
}
