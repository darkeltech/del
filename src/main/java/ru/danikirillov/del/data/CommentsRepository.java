package ru.danikirillov.del.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.danikirillov.del.domain.Comment;
/**
 * ALARM!!!!
 *
 *
 * */
public interface CommentsRepository extends ReactiveCrudRepository<Comment, Comment> {
}
