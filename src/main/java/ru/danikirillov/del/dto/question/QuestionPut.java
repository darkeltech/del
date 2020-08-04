package ru.danikirillov.del.dto.question;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class QuestionPut {
    @NotBlank
    @Size(max = 100)
    String theme;
    @NotBlank
    @Size(max = 50_000)
    String question;
}
