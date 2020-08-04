package ru.danikirillov.del.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import ru.danikirillov.del.domain.User;

import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class Jwt {
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 6 * 60 * 60 * 1000;// 6 hours
    public static final KeyPair KEY_PAIR = Keys.keyPairFor(SignatureAlgorithm.RS256);//пк не справляется с нагрузкой 512 токена, поэтому до релиза будет так
    public static final String AUTHORITIES_KEY = "roles";

    public static String getUsernameFromToken(ServerRequest request) {
        return getUsernameFromToken(getToken(request));
    }

    public static String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    public static Long getIdFromToken(ServerRequest request) {
        return getIdFromToken(getToken(request));
    }

    public static Long getIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("id", Long.class));
    }

    public static Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token)
                .before(new Date());
    }

    public static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromToken(token));
    }

    public static Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY_PAIR.getPublic())
                .build()
                .parseClaimsJws(tokenWithoutId(token))
                .getBody();
    }

    public static String generateToken(User user) {
        return user.getId() + " " +    // Добавление id пользователя для того, чтобы на фронте можно было определять может ли он менять вопрос
                Jwts.builder()
                        .setSubject(user.getNick())
                        .claim(AUTHORITIES_KEY, user.getRoles())
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS))
                        .claim( "id", user.getId())
                        .signWith(KEY_PAIR.getPrivate())
                        .compact();
    }

    public static boolean ifUserCorrect(ServerRequest request, String nick) {
        return getUsernameFromToken(getToken(request))
                .equals(nick);
    }

    public static String getToken(ServerRequest request) {
        return request.headers().firstHeader(HttpHeaders.AUTHORIZATION);
    }

    private static String tokenWithoutId(String token) {
        return token.replaceFirst("(\\d+ )", "");
    }
}
