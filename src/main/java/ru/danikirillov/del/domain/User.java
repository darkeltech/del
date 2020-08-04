package ru.danikirillov.del.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@AllArgsConstructor
@Table("users")
public class User {

    @Id
    private Long id;

    private String nick;
    private String password;
    private String faculty;
    private String email;

    private int points;
    private boolean subscribed;
    private String additionalInfo; // like Я в соц сетях:)))

    private List<String> roles;//todo заменить на енум:))))
}
