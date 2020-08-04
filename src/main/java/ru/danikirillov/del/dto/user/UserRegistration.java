package ru.danikirillov.del.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.danikirillov.del.domain.User;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserRegistration {
    @NotBlank
    private String nick;

    private String password;
    @NotBlank
    private String faculty;
    @NotBlank
    private String email;

    public User toUser() {
        return new User(null, nick, password, faculty, email, 0, false, null, null);
    }
}
