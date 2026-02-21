package br.com.mouzetech.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
@Component
@ConfigurationProperties(prefix = "mouzetech-clinica.jwt.keystore")
public class JwtKeyStoreProperties {

	@NotNull
	private Resource jksResource;

	@NotBlank
	private String jksStorePass;

	@NotBlank
	private String keyPairAlias;

	public Resource getJksResource() {
		return jksResource;
	}

	public void setJksResource(Resource jksResource) {
		this.jksResource = jksResource;
	}

	public String getJksStorePass() {
		return jksStorePass;
	}

	public void setJksStorePass(String jksStorePass) {
		this.jksStorePass = jksStorePass;
	}

	public String getKeyPairAlias() {
		return keyPairAlias;
	}

	public void setKeyPairAlias(String keyPairAlias) {
		this.keyPairAlias = keyPairAlias;
	}
}