package it.thera.thip.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ExceptionMapper;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;

import com.thera.thermfw.rs.config.RestApplicationListener;
import com.thera.thermfw.rs.errors.CatchAllExceptionMapper;
import com.thera.thermfw.rs.errors.PantheraApiExceptionMapper;
import com.thera.thermfw.rs.security.AuthenticationFilter;
import com.thera.thermfw.rs.security.AuthorizationFilter;

import it.thera.thip.api.config.RisorsaRestFilter;

/*
 * Revisions:
 * Fix     Date          Owner      Description
 * 35311   24/02/2022    PJ         Rilascio infrastruttura webservice su JAX-RS
 * 35629   02/05/2022    Pm         Gestione installazione in automatico dell fix
 * 35852   08/09/2022    PJ+FG      Nuovi servizi per nuova WPU
 * 35943   14/06/2022    HED        modifica IOF
 * 36824   19/10/2022    PJ         Riallineamento (nessuna modifica al codice)
 * 36867   25/10/2022    AF         Due nuovi servizi: pulizia e controllo autorizzazioni  
 * 36744   13/10/2022    GScarta    Gestione Web Service Generici
 * 37957   13/03/2023    PJ         Lettura resources da db, etc.
 * 38229   22/03/2023    PJ         Controllo licenza e autorizzazioni su webservice JAX-RS
 */

public class RestManager {

	public List<Class<? extends ExceptionMapper>> exceptionMappers() {
		List<Class<? extends ExceptionMapper>> mappers = new ArrayList<Class<? extends ExceptionMapper>>();
		mappers.add(PantheraApiExceptionMapper.class);
		mappers.add(CatchAllExceptionMapper.class);
		return mappers;
	}

	public List<Class<? extends ApplicationEventListener>> eventListeners() {
		List<Class<? extends ApplicationEventListener>> listeners = new ArrayList<Class<? extends ApplicationEventListener>>();
		listeners.add(RestApplicationListener.class);
		return listeners;
	}

	// 37957 - inizio
	public List<Class<? extends ContainerRequestFilter>> filters() {
		List<Class<? extends ContainerRequestFilter>> filters = new ArrayList<Class<? extends ContainerRequestFilter>>();
		filters.add(RisorsaRestFilter.class);
		filters.add(PantheraApiLoggingFilter.class);
		filters.add(AuthenticationFilter.class);

		// 38229 - inizio
		filters.add(AuthorizationFilter.class);
		// 38229 - fine

		return filters;
	}

	public List<Class<? extends Feature>> features() {
		List<Class<? extends Feature>> features = new ArrayList<Class<? extends Feature>>();
		features.add(MultiPartFeature.class);
		features.add(JacksonFeature.class);
		return features;
	}

	public List<Class> resources() {
		List<Class> resources = PantheraApiConfiguration.getInstance().restResources();
		try {
			Class c = Class.forName("it.test.portal.PortalAuthenticationResource");
			if (checkIsClassAValidResource(c)) {
				resources.add(c);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return resources;
	}

	private boolean checkIsClassAValidResource(Class resourceClass) {
		Path path = (Path) resourceClass.getAnnotation(Path.class);
		return path != null;
	}

	// 37957 - fine

}
