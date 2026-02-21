package br.com.mouzetech.security;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import br.com.mouzetech.domain.Usuario;
import br.com.mouzetech.domain.UsuarioRepository;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {
	
	private static final String PATH_LOGOUT = "/logout";
	private static final String PATH_LOGIN = "/login";

	@Bean
	@Order(1)
	SecurityFilterChain securityFilterChainAuthorizationServer(HttpSecurity http) throws Exception {
		
		OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
				new OAuth2AuthorizationServerConfigurer();
		
		authorizationServerConfigurer.oidc(Customizer.withDefaults());
		authorizationServerConfigurer.authorizationEndpoint(customizer -> customizer.consentPage("/oauth2/consent"));
		
		RequestMatcher endpointsMatcher = authorizationServerConfigurer
				.getEndpointsMatcher();

		http
			.securityMatcher(endpointsMatcher)
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests.anyRequest().authenticated()
			)
			.csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
			.exceptionHandling(exceptions ->
					exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(PATH_LOGIN)))
			.apply(authorizationServerConfigurer);
		
		return http.build();
	}
	
	@Bean
	@Order(2)
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(PATH_LOGIN, "/css/**", "/js/**", PATH_LOGOUT).permitAll()
	            .anyRequest().authenticated()
	        )
	        .formLogin(form -> form
	            .loginPage(PATH_LOGIN)
	            .permitAll()
	        )
	        .logout(logout -> logout
	                .logoutRequestMatcher(new AntPathRequestMatcher(PATH_LOGOUT, "GET"))
	                .logoutSuccessUrl("http://192.168.3.42:4200/")
	                .invalidateHttpSession(true)
	                .deleteCookies("JSESSIONID")
	                .permitAll()
	        );

	    return http.build();
	}

	@Bean
	AuthorizationServerSettings authServerSettings(ClinicaMedicaSecurityProperties properties) {
		return AuthorizationServerSettings.builder().issuer(properties.getAuthServerUrl()).build();
	}

	@Bean
	RegisteredClientRepository registeredClientRepository(JdbcOperations jdbcOperations) {
		return new JdbcRegisteredClientRepository(jdbcOperations);
	}

	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(UsuarioRepository usuarioRepository) {
		return context -> {
			Authentication authentication = context.getPrincipal();
			if (authentication.getPrincipal() instanceof User user) {
				Usuario usuario = usuarioRepository.findByEmail(user.getUsername()).orElseThrow();
				Set<String> authorities = new HashSet<>();
				user.getAuthorities().forEach(authoritie -> authorities.add(authoritie.getAuthority()));
				context.getClaims().claim("usuario_id", usuario.getId().toString());
				context.getClaims().claim("username", usuario.getNome());
				context.getClaims().claim("authorities", authorities);
			}
		};
	}

	@Bean
	OAuth2AuthorizationService oAuth2AuthorizationService(JdbcOperations jdbcOperations,
			RegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
	}

	@Bean
	JWKSource<SecurityContext> jwkSourceBean(JwtKeyStoreProperties keySetProperties) throws Exception {
		char[] keyStorePass = keySetProperties.getJksStorePass().toCharArray();
		String keyPairAlias = keySetProperties.getKeyPairAlias();
		Resource jksLocation = keySetProperties.getJksResource();
		InputStream inputStream = jksLocation.getInputStream();
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(inputStream, keyStorePass);
		RSAKey rsaKey = RSAKey.load(keyStore, keyPairAlias, keyStorePass);
		return new ImmutableJWKSet<>(new JWKSet(rsaKey));
	}

	@Bean
	OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService(JdbcOperations jdbcOPerations,
			RegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationConsentService(jdbcOPerations, registeredClientRepository);
	}

	@Bean
	OAuth2AuthorizationQueryService oAuth2AuthorizationQueryService(JdbcOperations jdbcOperations,
			RegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationQueryService(jdbcOperations, registeredClientRepository);
	}
}