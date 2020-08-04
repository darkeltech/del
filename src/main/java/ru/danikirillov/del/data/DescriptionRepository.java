package ru.danikirillov.del.data;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Mono;
import ru.danikirillov.del.domain.Description;

@CrossOrigin(origins = "*")
public interface DescriptionRepository extends ReactiveCrudRepository<Description, Long> {

    @Query("select * from descriptions where version = (select max(version) from descriptions);")
    Mono<Description> findLatestDescription();
}
