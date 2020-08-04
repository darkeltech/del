package ru.danikirillov.del.rest.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.danikirillov.del.data.DescriptionRepository;
import ru.danikirillov.del.domain.Description;
import ru.danikirillov.del.dto.description.DescriptionDTO;
import ru.danikirillov.del.validation.ValidationHandler;

import java.util.Arrays;

@Component
public class DescriptionHandler {

    @Autowired
    private DescriptionRepository repository;
    @Autowired
    private ValidationHandler validationHandler;

    public Mono<ServerResponse> getDescription(ServerRequest request) {
        Mono<Description> description = repository.findLatestDescription()
                .switchIfEmpty(Mono.just(new DescriptionDTO("Prj to help u", Arrays.asList("Danzello", "graf Vlad")).toDes()));
        return checkIfEmptyAndReturn(description, HttpStatus.OK);
    }

    public Mono<ServerResponse> postDescription(ServerRequest request) {
        return validationHandler.validateBodyAndApplyIfCorrect(
                this::postIfValid,
                request,
                DescriptionDTO.class
        );
    }

    private Mono<ServerResponse> postIfValid(Mono<DescriptionDTO> body) {
        Mono<Description> description = repository.saveAll(
                body.map(DescriptionDTO::toDes)
        ).single();

        return checkIfEmptyAndReturn(description, HttpStatus.CREATED);
    }

    public Mono<ServerResponse> putDescription(ServerRequest request) {
        return ServerResponse
                .status(HttpStatus.FORBIDDEN)
                .body(Mono.just("Описание можно изменить только post запросом."), String.class);
    }

    public Mono<ServerResponse> deleteDescription(ServerRequest request) {
        return ServerResponse
                .status(HttpStatus.FORBIDDEN)
                .body(Mono.just("Описание нельзя удалять, можно заменить post запросом."), String.class);
    }

    public Mono<ServerResponse> checkIfEmptyAndReturn(Mono<Description> description, HttpStatus status) {
        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(description, Description.class)
                //скорее всего часть ниже не нужна, стоит поменять
                .switchIfEmpty(
                        ServerResponse
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.APPLICATION_JSON)
                                .build()
                );
    }

}
