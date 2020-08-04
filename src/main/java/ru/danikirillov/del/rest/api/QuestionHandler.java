package ru.danikirillov.del.rest.api;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.danikirillov.del.data.QuestionRepository;
import ru.danikirillov.del.data.UserRepository;
import ru.danikirillov.del.domain.Question;
import ru.danikirillov.del.dto.question.QuestionPost;
import ru.danikirillov.del.dto.question.QuestionPut;
import ru.danikirillov.del.security.Jwt;
import ru.danikirillov.del.validation.ValidationHandler;

@AllArgsConstructor
@Component
public class QuestionHandler {

    private static final String FUCK_OFF_HACKER = "Пользователь не имеет доступа к изменению вопроса.";

    private final QuestionRepository repository;
    private final UserRepository userRepository;
    private final ValidationHandler validationHandler;
    private final TransactionalOperator rxtx;

    public Mono<ServerResponse> getQuestions(ServerRequest request) {
        int page = Integer.parseInt(request.pathVariable("page"));
        int limit = Integer.parseInt(request.pathVariable("amount"));
        int offset = (page - 1) * limit;

        return ServerResponse.ok().body(repository.getQuestions(limit, offset), Question.class);
    }

    public Mono<ServerResponse> getQuestion(ServerRequest request) {
        long id = Integer.parseInt(request.pathVariable("id"));

        return ServerResponse.ok().body(repository.findById(id), Question.class);
    }

    public Mono<ServerResponse> postQuestion(ServerRequest request) {
        return validationHandler.validateBodyAndApplyIfCorrectWithRequest(
                this::postIfValid,
                request,
                QuestionPost.class
        );
    }

    private Mono<ServerResponse> postIfValid(ServerRequest request) {
        return request
                .bodyToMono(Question.class)
                .flatMap(question ->
                        userRepository.findByNick(Jwt.getUsernameFromToken(request))
                                .flatMap(user -> {
                                    if (user == null)
                                        return ServerResponse.badRequest().body(Mono.just("User not found. :( "), String.class);

                                    if (user.getPoints() >= question.getPrice()) {

                                        question.setAskerId(user.getId());
                                        user.setPoints(user.getPoints() - question.getPrice());

                                        return repository.save(question)
                                                .then(userRepository.save(user))
                                                .then().as(rxtx::transactional)
                                                .flatMap(k -> ServerResponse.status(HttpStatus.CREATED).build());

                                    } else
                                        return ServerResponse.badRequest().body(Mono.just("Очков не хватает."), String.class);
                                })
                );
    }

    public Mono<ServerResponse> putQuestion(ServerRequest request) {
        return validationHandler.validateBodyAndApplyIfCorrectWithRequest(
                this::putIfValid,
                request,
                QuestionPut.class
        );
    }

    private Mono<ServerResponse> putIfValid(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));

        return request.bodyToMono(Question.class)
                .flatMap(question -> {
                    question.setId(id);
                    if (isAskerValid(Jwt.getIdFromToken(request), question.getId())) {
                        repository.save(question);
                        return ServerResponse.status(HttpStatus.CREATED).build();
                    } else
                        return ServerResponse.badRequest().body(Mono.just(FUCK_OFF_HACKER), String.class);
                });
    }

    public Mono<ServerResponse> deleteQuestion(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        if (isAskerValid(Jwt.getIdFromToken(request), id)) {
            repository.deleteById(id);
            return ServerResponse.notFound().build();
        } else
            return ServerResponse.badRequest().body(Mono.just(FUCK_OFF_HACKER), String.class);
    }

    private boolean isAskerValid(Long askerId, Long questionId) {
        Mono<Question> question = repository.findByAskerIdAndId(askerId, questionId);
        return question.block() != null;
    }
}
