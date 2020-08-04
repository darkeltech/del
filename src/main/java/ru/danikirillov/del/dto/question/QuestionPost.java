package ru.danikirillov.del.dto.question;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Value
public class QuestionPost {
    @NotBlank
    @Size(max = 100)
    String theme;
    @NotBlank
    @Size(max = 50_000)
    String question;
    @PositiveOrZero
    int price;
    @NotNull
    OffsetDateTime createdAt;
}
