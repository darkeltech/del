package ru.danikirillov.del.data;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.danikirillov.del.domain.Question;

public interface QuestionRepository  extends ReactiveCrudRepository<Question, Long> {

    @Query("select * from questions order by created_at desc limit :limit offset :offset")
    Flux<Question> getQuestions(int limit, int offset);

    Mono<Question> findByAskerIdAndId(Long askerId, Long id);
}