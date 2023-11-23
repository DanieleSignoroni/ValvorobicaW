package it.test.portal;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;

import org.glassfish.jersey.server.ResourceConfig;

import com.thera.thermfw.persist.Factory;

import it.thera.thip.api.RestManager;

@ApplicationPath("portal/api")
public class PortalRestApplication extends ResourceConfig {

	private RestManager restManager = (RestManager) Factory.createObject(RestManager.class);

	// 37891 - inizio
	private Class factory(Class originalClass) {
		return Factory.getClass(originalClass);
	}

	// 37957 - inizio
	private void registerInternal() {
		// for (Class<? extends ExceptionMapper> mapper :
		// restManager.exceptionMappers()) {
		// register(factory(mapper));
		// }
		//
		// for (Class<? extends ApplicationEventListener> listener :
		// restManager.eventListeners()) {
		// register(factory(listener));
		// }
		//
		// for (Class<? extends ContainerRequestFilter> filter : restManager.filters())
		// {
		// register(factory(filter));
		// }
		//
		// for (Class<? extends Feature> feature : restManager.features()) {
		// register(factory(feature));
		// }
		//
		// for (Class resource : restManager.resources()) {
		// register(resource);
		// }
		Class c;
		try {
			c = Class.forName("it.test.portal.PortalAuthenticationResource");
			if (checkIsClassAValidResource(c)) {
				register(c);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private boolean checkIsClassAValidResource(Class resourceClass) {
		Path path = (Path) resourceClass.getAnnotation(Path.class);
		return path != null;
	}

	// 37957 - fine

	// 37891 - fine
	public PortalRestApplication() {
		super();
		this.registerInternal();
	}
}