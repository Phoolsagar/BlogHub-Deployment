package in.scalive.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import in.scalive.interceptor.SessionAuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	private SessionAuthInterceptor sessionAuthInterceptor;
	private final String allowedOrigin;
	
	@Autowired
	public WebConfig(SessionAuthInterceptor sessionAuthInterceptor,
			@Value("${app.cors.allowed-origin:http://localhost:5500}") String allowedOrigin) {
		this.sessionAuthInterceptor = sessionAuthInterceptor;
		this.allowedOrigin = allowedOrigin;
	}
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	
		registry.addInterceptor(sessionAuthInterceptor)
				.addPathPatterns("/api/**")
				.excludePathPatterns("/api/auth/**", "/error", "/favicon.ico");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(allowedOrigin)
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true);
	}

	
	
}
