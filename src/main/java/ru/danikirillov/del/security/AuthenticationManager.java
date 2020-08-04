package ru.danikirillov.del.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static ru.danikirillov.del.security.Jwt.AUTHORITIES_KEY;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    /**
     * I definitely need help to deal with it, please specialists in reactive programming message on danikirillov@yandex.ru
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication.getCredentials().toString())
                .flatMap(token -> {
                    if (Jwt.isTokenExpired(token))
                        return Mono.empty();
                    return Mono.just(token)
                            .map(Jwt::getUsernameFromToken)
                            .onErrorResume(e -> Mono.empty())
                            .flatMap(username -> configure(token, username));
                });
    }

    private Mono<Authentication> configure(String token, String username) {
        List<String> roles = Jwt.getAllClaimsFromToken(token).get(AUTHORITIES_KEY, List.class);
        List<SimpleGrantedAuthority> authorities = null;
        if ( roles!= null)
            authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, username, authorities);

        SecurityContextHolder.getContext().setAuthentication(new AuthUser(username, authorities));

        return Mono.just(auth);
    }
}