package ru.users.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class UpdateUserDto {

    @Size(max = 256, message = "Invalid request format")
    @Schema(description = "Имя пользователя", maxLength = 256)
    private String name;

    @Size(max = 256, message = "Invalid request format")
    @Schema(description = "Фамилия пользователя", maxLength = 256)
    private String surname;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("registration_date")
    @Schema(description = "Дата регистрации пользователя (в формате yyyy-MM-dd)")
    private LocalDate registrationDate;
}
