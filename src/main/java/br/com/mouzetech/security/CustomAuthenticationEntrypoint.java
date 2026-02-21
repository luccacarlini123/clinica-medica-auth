package br.com.mouzetech.security;
import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class CustomAuthenticationEntrypoint implements AuthenticationEntryPoint {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");	
		
		Problem.ProblemBuilder problemBuilder = Problem.builder();
		problemBuilder.title("Sem autorização");
		problemBuilder.status(response.getStatus());
		problemBuilder.timeStamp(OffsetDateTime.now());
		problemBuilder.detail("Sem autorização para acessar o recurso");
		problemBuilder.userMessage("Sem autorização para acessar o recurso");
		problemBuilder.type("Acesso negado");
		
		response.getWriter().write(this.objectMapper.writeValueAsString(problemBuilder.build()));
	}
}