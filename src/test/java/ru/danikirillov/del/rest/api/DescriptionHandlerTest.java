package ru.danikirillov.del.rest.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.danikirillov.del.DelApplication;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DelApplication.class)
class DescriptionHandlerTest {

    WebTestClient client;

    @BeforeEach
    void setUp(ApplicationContext context) {
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void getDescription() {
        client.get().uri("/description")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                //.json()
        ;
    }

    @Test
    void postDescription() {
    }

    @Test
    void putDescription() {
    }

    @Test
    void deleteDescription() {
    }

    @Test
    void checkIfEmptyAndReturn() {
    }
}