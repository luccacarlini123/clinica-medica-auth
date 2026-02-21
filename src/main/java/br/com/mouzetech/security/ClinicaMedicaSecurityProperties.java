package br.com.mouzetech.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Component
@Validated
@Getter
@Setter
@ConfigurationProperties("clinicamedica.security")
public class ClinicaMedicaSecurityProperties {

    @NotBlank
    private String authServerUrl;
}