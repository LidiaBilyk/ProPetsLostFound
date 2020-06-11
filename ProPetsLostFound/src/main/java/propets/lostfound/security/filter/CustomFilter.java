package propets.lostfound.security.filter;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import propets.lostfound.configuration.LostFoundConfiguration;
import propets.lostfound.service.CustomSecurity;

@Service
@Order(20)
public class CustomFilter implements Filter {
	
	
	@Autowired
	CustomSecurity customSecurity;
	
	@Autowired
	LostFoundConfiguration lostFoundConfiguration;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod(); 					
				
		if (!checkPointCut(path, method)) {
			Principal principal = request.getUserPrincipal();
			if (checkPointCutLoginLost(path, method)) {
				UriTemplate templateLoginLost = new UriTemplate(lostFoundConfiguration.getTemplateLoginLost());
				String pathLogin = templateLoginLost.match(request.getRequestURI()).get("login");
				if (!customSecurity.checkAuthorityForPost(pathLogin, principal)) {
					response.sendError(403, "Access denied");
					return;
				}
			}
			if (checkPointCutLoginFound(path, method)) {
				UriTemplate templateLoginFound = new UriTemplate(lostFoundConfiguration.getTemplateLoginFound());
				String pathLogin = templateLoginFound.match(request.getRequestURI()).get("login");
				if (!customSecurity.checkAuthorityForPost(pathLogin, principal)) {
					response.sendError(403, "Access denied");
					return;
				}
			}
			if (checkPointCutId(path, method)) {
				UriTemplate templateId = new UriTemplate(lostFoundConfiguration.getTemplateId());
				String pathId = templateId.match(request.getRequestURI()).get("id");
				if (!customSecurity.checkAuthorityForDeletePost(pathId, principal)) {
					response.sendError(403, "Access denied");
					return;
				}
			}
			chain.doFilter(new WrapperRequest(request, principal.getName()), response);
			return;
		}	
		chain.doFilter(request, response);
	}
	
	private boolean checkPointCut(String path, String method) {
		boolean check = path.matches(".*/userdata");		
		return check;
	}
	
//	check updatePost & deletePost methods
	private boolean checkPointCutId(String path, String method) {
		boolean check = "Put".equalsIgnoreCase(method) || "Delete".equalsIgnoreCase(method);		
		return check;
	}
//  check post method
	private boolean checkPointCutLoginLost(String path, String method) {
		boolean check = "Post".equalsIgnoreCase(method) && path.matches(".*/lost/.*");		
		return check;
	}
	private boolean checkPointCutLoginFound(String path, String method) {
		boolean check = "Post".equalsIgnoreCase(method) && path.matches(".*/found/.*");		
		return check;
	}
	
	private class WrapperRequest extends HttpServletRequestWrapper {

		String user;

		public WrapperRequest(HttpServletRequest request, String user) {
			super(request);
			this.user = user;
		}

		@Override
		public Principal getUserPrincipal() {
			return new Principal() { // or return () -> user;

				@Override
				public String getName() {
					return user;
				}
			};
		}
	}

}
