import com.google.inject.AbstractModule;

public class HelloGuiceModule extends AbstractModule {
    protected void configure() {
	// add configuration logic here
	bind(HelloGuiceService.class).to(HelloGuiceServiceImpl.class);
    }
}
