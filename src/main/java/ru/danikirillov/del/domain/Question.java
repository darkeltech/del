package ru.danikirillov.del.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@Table("questions")
public class Question {
    @Id
    private Long id;

    private Long askerId;

    private String theme;
    private String question;
    private int price;
    private OffsetDateTime createdAt;//TODO Изменить тип

    private Long answererId;

    private Integer amountOfWatches;
    private Integer amountOfComments;
}
