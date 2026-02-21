package br.com.mouzetech.security;
import java.security.Principal;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthorizedClientController {

	private final OAuth2AuthorizationQueryService oAuth2AuthorizationQueryService;
	private final RegisteredClientRepository clientRepository;
	private final OAuth2AuthorizationConsentService consentService;
	private final OAuth2AuthorizationService oAuth2AuthorizationService;
	
	@GetMapping("/oauth2/authorized-clients")
	public String getAuthorizedClients(Principal principal, Model model) {
		
		if(principal == null) {
			throw new AccessDeniedException(String.format("Usuário não encontrado"));
		}
		
		List<RegisteredClient> clients = this.oAuth2AuthorizationQueryService.listClientsWithConsent(principal.getName());
		
		model.addAttribute("clients", clients);
		
		return "pages/authorized-clients";	
	}
	
	@PostMapping("/oauth2/authorized-clients/revoke")
	public String revoke(Principal principal, Model model,
			@RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId) {
		
		RegisteredClient registeredClient = this.clientRepository.findByClientId(clientId);
		
		if(registeredClient == null || principal == null) {
			throw new AccessDeniedException(String.format("Cliente %s não encontrado", clientId));
		}

		var consent = this.consentService.findById(registeredClient.getId(), principal.getName());

		var authorizationsClient = this.oAuth2AuthorizationQueryService.listAuthorizations(principal.getName(),
				registeredClient.getId());
		
		
		if (consent != null) {
			this.consentService.remove(consent);
		}
		
		for(OAuth2Authorization authorization : authorizationsClient) {
			this.oAuth2AuthorizationService.remove(authorization);
		}
		
		return "redirect:/oauth2/authorized-clients";
	}
	
}