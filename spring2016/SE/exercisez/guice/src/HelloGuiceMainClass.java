import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

public class HelloGuiceMainClass {
    @Inject
    HelloGuiceService helloGuiceService;
    
    public static void main(String[] args) {
	HelloGuiceMainClass helloGuiceMainClass = new HelloGuiceMainClass();
	
	Module module = new HelloGuiceModule();
	Injector injector = Guice.createInjector(module);
	injector.injectMembers(helloGuiceMainClass);
	
	helloGuiceMainClass.helloGuice();
    }
    
    private void helloGuice() {
	String testStr = helloGuiceService.serviceMethod("Hello Guice!");
	System.out.println(testStr);
    }
}
