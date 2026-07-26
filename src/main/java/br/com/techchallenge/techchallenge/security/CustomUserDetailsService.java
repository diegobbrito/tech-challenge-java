package br.com.techchallenge.techchallenge.security;

import br.com.techchallenge.techchallenge.controllers.UserController;
import br.com.techchallenge.techchallenge.repositories.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Tentando autenticar o usuário: {}", username);

        var appUser = userRepository.findByUserLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        String role = appUser.getUserType() != null ? appUser.getUserType().toUpperCase() : "USER";
        String prefixedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return new User(
                appUser.getUserLogin(),
                appUser.getPassword(),
                List.of(new SimpleGrantedAuthority(prefixedRole))
        );
    }
}