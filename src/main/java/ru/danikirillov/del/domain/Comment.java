package ru.danikirillov.del.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
public class Comment {
    private Long questionId;
    private Long userId;
    private String comment;
    private Date createdAt;
}
