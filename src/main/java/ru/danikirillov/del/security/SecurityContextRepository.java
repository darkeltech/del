package ru.danikirillov.del.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        throw new UnsupportedOperationException("Save is not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
        return Mono.just(swe.getRequest())
                .map(request -> {
                    String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                    return token == null ? "SickMockOffsException" : token;
                })
                .flatMap(token ->
                        token.equals("SickMockOffsException")
                                ? Mono.empty()
                                : authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(token, token))
                                .map(SecurityContextImpl::new)
                );
    }
}
