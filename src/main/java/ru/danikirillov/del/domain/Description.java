package ru.danikirillov.del.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@AllArgsConstructor
@Table("descriptions")
public class Description {
    private @Id Long version;
    private String description;
    private List<String> authors;
}
