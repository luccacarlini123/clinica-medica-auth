package br.com.mouzetech.security;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mouzetech.domain.Usuario;
import br.com.mouzetech.domain.UsuarioRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = this.usuarioRepository.findByEmail(username)
				.orElseThrow(() -> new AccessDeniedException("Usuário ou senha inválidos"));

		var permissoes = usuario.getGrupos().stream().flatMap(grupo -> grupo.getPermissoes().stream())
				.map(permissao -> new SimpleGrantedAuthority(permissao.getNome())).collect(Collectors.toList());

		return new User(usuario.getEmail(), usuario.getSenha(), permissoes);
	}
}