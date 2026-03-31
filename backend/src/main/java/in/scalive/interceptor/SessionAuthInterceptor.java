package in.scalive.interceptor;


import java.io.PrintWriter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		 HttpSession session = request.getSession(false);
		 
		 System.out.println("Path: "+request.getRequestURI());
		 System.out.println("Method: "+request.getMethod());
		 System.out.println("Session present?: "+(session != null));
		 
		 if(session != null) {
			 System.out.println("Session Id: "+session.getId());
			 System.out.println("userId: "+session.getAttribute("userId"));
		 }
		 
		 
		 //		 Main concept start from here
		 if(session == null || session.getAttribute("userId") == null) {
			 response.setStatus(401);// Unauthorized (means aap login nhi ho)
			 
			 response.setContentType("application/json");
			 PrintWriter pw = response.getWriter();
			 pw.write("{\"error\" : \"Please Login First!\"} ");
			 return false;
		 }
		 
		 Long userId = (Long)session.getAttribute("userId");
		 String userRole = (String)session.getAttribute("userRole");
		 
		 request.setAttribute("currentUserId", userId);
		 request.setAttribute("currentUserRole", userRole);
		 
		 String path = request.getRequestURI();
		 String method = request.getMethod();
		 
		 if(path.startsWith("/api/categories")) {
			 if(!method.equals("GET") && !userRole.equals("ADMIN")) {
				 response.setStatus(403);// Forbidden (means aap login ho but Jo aap karna chahte ho uske liye eligible nhi hai)
				 response.setContentType("application/json");
				 PrintWriter pw = response.getWriter();
				 pw.write("{\"error\" : \"Admin access required!\"} ");
				 return false;
			 }
		 }
		return true;
	}
	

	
}
