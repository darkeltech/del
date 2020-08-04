package ru.danikirillov.del.rest.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.danikirillov.del.data.UserRepository;
import ru.danikirillov.del.dto.user.UserInfo;
import ru.danikirillov.del.security.Jwt;
import ru.danikirillov.del.validation.ValidationHandler;

@Component
public class UserHandler {

    @Autowired
    private UserRepository repository;
    @Autowired
    private ValidationHandler validationHandler;

    public Mono<ServerResponse> getUserInfo(ServerRequest request) {
        Mono<UserInfo> userInfo = getUserInfo(Mono.just(Jwt.getUsernameFromToken(request)));
        return ServerResponse.ok().body(userInfo, UserInfo.class);
    }

    private Mono<UserInfo> getUserInfo(Mono<String> nick) {
        return nick.flatMap(n -> repository
                .findUserInfoByNick(n));
    }

    public Mono<ServerResponse> postUserInfo(ServerRequest request) {
        return ServerResponse
                .status(HttpStatus.FORBIDDEN)
                .body(Mono.just("Пользователя можно изменить put запросом. Для создания нового пользователя используй регистрацию."), String.class);
    }

    public Mono<ServerResponse> putUserInfo(ServerRequest request) {
        return Jwt.ifUserCorrect(request, request.pathVariable("nick"))
                ?
                validationHandler.validateBodyAndApplyIfCorrect(
                        this::putIfValid,
                        request,
                        UserInfo.class
                )
                : ServerResponse.badRequest().build();
    }

    private Mono<ServerResponse> putIfValid(Mono<UserInfo> body) {
        return body.map(userInfo -> repository.findByNick(userInfo.getNick())).block() == null
                ?
                body
                        .flatMap(userInfo -> repository.updateUserInfo(userInfo.getNick(), userInfo.getFaculty(), userInfo.getEmail(), userInfo.getAdditionalInfo()))
                        .flatMap(updated -> checkIfEmptyAndReturn(getUserInfo(body.map(UserInfo::getNick)), HttpStatus.CREATED))
                :
                ServerResponse.badRequest().body(Mono.just("Ало, этот ник уже занят, прояви фантазию_99. С уважением коллеги."), String.class);
    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        if (Jwt.ifUserCorrect(request, request.pathVariable("nick")))
            repository.deleteByNick(request.pathVariable("nick"));
        return ServerResponse.status(HttpStatus.NOT_FOUND).build();
    }

    public Mono<ServerResponse> checkIfEmptyAndReturn(Mono<UserInfo> userInfo, HttpStatus status) {
        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userInfo, UserInfo.class)
                .switchIfEmpty(
                        ServerResponse
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.APPLICATION_JSON)
                                .build()
                );
    }
}
