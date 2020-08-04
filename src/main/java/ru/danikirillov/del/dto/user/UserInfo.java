package ru.danikirillov.del.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
public class UserInfo {

    @NotBlank
    private String nick;
    @NotBlank
    private String faculty;
    @NotBlank
    private String email;
    private String additionalInfo; // like Я в соц сетях:)))
    @PositiveOrZero
    private int points;
    private boolean subscribed;
}
