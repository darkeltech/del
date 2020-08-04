package ru.danikirillov.del.rest.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.danikirillov.del.data.UserRepository;
import ru.danikirillov.del.dto.user.LoginRequest;
import ru.danikirillov.del.dto.user.LoginResponse;
import ru.danikirillov.del.dto.user.UserRegistration;
import ru.danikirillov.del.security.Jwt;
import ru.danikirillov.del.validation.ValidationHandler;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class AuthHandler {

    @Autowired
    private UserRepository repository;
    @Autowired
    private ValidationHandler validationHandler;
    @Autowired
    private PasswordEncoder encoder;


    public Mono<ServerResponse> register(ServerRequest request) {
        return validationHandler.validateBodyAndApplyIfCorrect(
                this::registerIfValid,
                request,
                UserRegistration.class
        );
    }

    private Mono<ServerResponse> registerIfValid(Mono<UserRegistration> body) {
        return body
                .map(userRegistration -> {
                    userRegistration.setPassword(encoder.encode(userRegistration.getPassword()));
                    return userRegistration.toUser();
                })
                .flatMap(user -> repository.findByNick(user.getNick())
                        .flatMap(gotUser -> ServerResponse
                                .badRequest()
                                .body(Mono.just("Ало, этот ник уже занят, прояви фантазию_99. С уважением коллеги."), String.class)
                        ).switchIfEmpty(
                                repository
                                        .save(user)
                                        .flatMap(savedUser -> ServerResponse.status(HttpStatus.CREATED).contentType(APPLICATION_JSON).build())
                        )
                );
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return validationHandler.validateBodyAndApplyIfCorrect(
                this::loginIfValid,
                request,
                LoginRequest.class
        );
    }

    private Mono<ServerResponse> loginIfValid(Mono<LoginRequest> body) {
        return body
                .flatMap(login -> repository.findByNick(login.getUsername())
                        .flatMap(user -> {
                            if (encoder.matches(login.getPassword(), user.getPassword())) {
                                return ServerResponse
                                        .ok()
                                        .contentType(APPLICATION_JSON)
                                        .body(Mono.just(new LoginResponse(Jwt.generateToken(user))), LoginResponse.class);
                            } else
                                return ServerResponse.badRequest().body(Mono.just("Пароль введен неправильно. Досада."), String.class);

                        })
                        .switchIfEmpty(ServerResponse.badRequest().body(Mono.just("Увы, но вы ошиблись в написании ника."), String.class)));
    }

}
