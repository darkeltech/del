package ru.danikirillov.del.data;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Mono;
import ru.danikirillov.del.domain.User;
import ru.danikirillov.del.dto.user.UserInfo;

@CrossOrigin(origins = "*")
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    @Query("update users set faculty = :faculty, email = :email, additional_info = :additional_info where nick = :nick")
    Mono<Boolean> updateUserInfo(String nick,
                                 String faculty,
                                 String email,
                                 String additional_info);

    Mono<Void> deleteByNick(String nick);

    Mono<User> findByNick(Mono<String> nick);

    Mono<User> findByNick(String nick);

    @Query("select nick, faculty, email, additional_info, points, subscribed from users where nick = :nickname")
    Mono<UserInfo> findUserInfoByNick(String nickname);
/*
    @Query("exist (select * from users where nick = :nick);")
    Mono<Boolean> existsUserByNick(String nick);*/
}
