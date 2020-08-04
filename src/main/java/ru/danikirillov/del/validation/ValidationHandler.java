package ru.danikirillov.del.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.chrono.IsoEra;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * https://jeroenbellen.com/validating-springs-functional-endpoints/
 * thx for this handler
 */

@Component
public class ValidationHandler {

    @Autowired
    private Validator validator;

    public <BODY> Mono<ServerResponse> validateBodyAndApplyIfCorrect(
            Function<Mono<BODY>, Mono<ServerResponse>> block,
            ServerRequest request,
            Class<BODY> bodyClass) {

        return request
                .bodyToMono(bodyClass)
                .flatMap(
                        body -> {
                            Set<ConstraintViolation<BODY>> errors;
                            return (errors = validator.validate(body)).isEmpty()
                                    ? block.apply(Mono.just(body))
                                    : ServerResponse.unprocessableEntity()
                                    .body(
                                            Flux.fromIterable(errors)
                                                    .collectMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage)
                                            ,
                                            Map.class
                                    );
                        }
                );
    }
//TODO придумай как упростить данные методы
    public <BODY> Mono<ServerResponse> validateBodyAndApplyIfCorrectWithRequest(
            Function<ServerRequest, Mono<ServerResponse>> block,
            ServerRequest request,
            Class<BODY> bodyClass) {

        ServerRequest request1 = ServerRequest.from(request).build();//ЧТО ЭТО ЗВА ГОВНО?????

        return request
                .bodyToMono(bodyClass)
                .flatMap(
                        body -> {
                            Set<ConstraintViolation<BODY>> errors;
                            return (errors = validator.validate(body)).isEmpty()
                                    ? block.apply(request1)
                                    : ServerResponse.unprocessableEntity()
                                    .body(
                                            Flux.fromIterable(errors)
                                                    .collectMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage)
                                            ,
                                            Map.class
                                    );
                        }
                );
    }

}
