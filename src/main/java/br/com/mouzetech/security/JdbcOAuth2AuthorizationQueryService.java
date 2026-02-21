package br.com.mouzetech.security;

import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

public class JdbcOAuth2AuthorizationQueryService implements OAuth2AuthorizationQueryService {

	private final JdbcOperations jdbcOperations;
	private final RowMapper<RegisteredClient> rowMapperRegisteredClient;
	private final RowMapper<OAuth2Authorization> rowMapperAuthorization;

	private static final String QUERY_CLIENTS_LIST = """
				select
					rc.*
				from oauth2_authorization_consent ac
				join oauth2_registered_client rc on rc.id = ac.registered_client_id
				where ac.principal_name = ?
			""";

	private static final String QUERY_AUTHORIZATION_LIST = """
				select
					a.*
				from oauth2_authorization a
				join oauth2_registered_client rc
					on rc.id = a.registered_client_id
				where a.principal_name = ?
				and a.registered_client_id = ?
			""";

	public JdbcOAuth2AuthorizationQueryService(JdbcOperations jdbcOperations,
			RegisteredClientRepository registeredClientRepository) {
		super();
		this.jdbcOperations = jdbcOperations;
		this.rowMapperRegisteredClient = new JdbcRegisteredClientRepository.RegisteredClientRowMapper();
		this.rowMapperAuthorization = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(
				registeredClientRepository);
	}

	@Override
	public List<RegisteredClient> listClientsWithConsent(String principalName) {
		return this.jdbcOperations.query(QUERY_CLIENTS_LIST, this.rowMapperRegisteredClient, principalName);
	}

	@Override
	public List<OAuth2Authorization> listAuthorizations(String principalName, String clientId) {
		return this.jdbcOperations.query(QUERY_AUTHORIZATION_LIST, this.rowMapperAuthorization, principalName,
				clientId);
	}
}