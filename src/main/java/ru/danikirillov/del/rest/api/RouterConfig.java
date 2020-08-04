package ru.danikirillov.del.rest.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class RouterConfig {

    private static final String DESCRIPTION_ENDPOINT = "/description";
    private static final String AUTH_ENDPOINT = "/auth";
    private static final String USER_ENDPOINT = "/user";
    private static final String QUESTION_ENDPOINT = "/questions";

    @Bean
    public RouterFunction<ServerResponse> descriptionRouter(final DescriptionHandler handler) {
        return RouterFunctions
                .route()
                .before(this::requestLog)
                .path(DESCRIPTION_ENDPOINT,
                        builder -> builder
                                .nest(
                                        accept(MediaType.APPLICATION_JSON),
                                        nestBuilder -> nestBuilder
                                                .GET("", handler::getDescription)
                                                .POST("", handler::postDescription)
                                                .PUT("", handler::putDescription)
                                                .DELETE("", handler::deleteDescription)
                                )
                )
                .after(this::responseLog)
                .build();
    }

    //todo разберись с константами, вынеси их в конфиг
    @Bean
    public RouterFunction<ServerResponse> authRouter(final AuthHandler handler) {
        return RouterFunctions
                .route()
                .before(this::requestLog)
                .path(AUTH_ENDPOINT,
                        builder -> builder
                                .nest(
                                        accept(MediaType.APPLICATION_JSON),
                                        nestBuilder -> nestBuilder
                                                .POST("/register", handler::register)
                                                .POST("/login", handler::login)
                                )
                )
                .after(this::responseLog)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userRouter(final UserHandler handler) {
        return RouterFunctions
                .route()
                .before(this::requestLog)
                .path(USER_ENDPOINT,
                        builder -> builder
                                .nest(
                                        accept(MediaType.APPLICATION_JSON),
                                        nestBuilder -> nestBuilder
                                                .GET("", handler::getUserInfo)
                                                .POST("", handler::postUserInfo)
                                                .PUT("/{nick}", handler::putUserInfo)
                                                .DELETE("/{nick}", handler::deleteUser)
                                )
                )
                .after(this::responseLog)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> questionRouter(final QuestionHandler handler) {
        return RouterFunctions
                .route()
                .before(this::requestLog)
                .path(QUESTION_ENDPOINT,
                        builder -> builder
                                .nest(
                                        accept(MediaType.APPLICATION_JSON),
                                        nestBuilder -> nestBuilder
                                                .GET("/{page}/{amount}", handler::getQuestions)
                                                .GET("/{id}", handler::getQuestion)
                                                .POST("", handler::postQuestion)
                                                .PUT("/{id}", handler::putQuestion)
                                                .DELETE("/{id}", handler::deleteQuestion)
                                )
                )
                .after(this::responseLog)
                .build();
    }

    private ServerRequest requestLog(ServerRequest request) {
        log.info(request.methodName() + " request from IP: " + request.remoteAddress().get() + " to " + request.uri().getPath());
        return request;
    }

    private ServerResponse responseLog(ServerRequest request, ServerResponse response) {
        log.info(request.methodName() + " response  to IP: " + request.remoteAddress().get() + " to " + request.uri().getPath() + " with status: " + response.statusCode().toString());
        return response;
    }

}
