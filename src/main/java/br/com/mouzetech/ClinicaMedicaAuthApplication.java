package br.com.mouzetech;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.mouzetech.security.Base64ProtocolResolver;

@SpringBootApplication
public class ClinicaMedicaAuthApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		
		var app = new SpringApplication(ClinicaMedicaAuthApplication.class);
		app.addInitializers(context -> context.addProtocolResolver(new Base64ProtocolResolver()));
		app.run(args);
	}
}