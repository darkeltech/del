package ru.danikirillov.del.dto.description;

import lombok.Value;
import ru.danikirillov.del.domain.Description;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Value
public class DescriptionDTO {

    @NotBlank
    String description;

    @NotNull
    @Size(min = 1)
    List<String> authors;

    public Description toDes() {
        return new Description(null, getDescription(), getAuthors());
    }
}
