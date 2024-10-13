package ru.users.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class NewUserDto {

    @NotBlank
    @Size(max = 256, message = "Invalid request format")
    @Schema(description = "Имя пользователя")
    private String name;

    @NotBlank
    @Size(max = 256, message = "Invalid request format")
    @Schema(description = "Фамилия пользователя")
    private String surname;

    @NotNull(message = "Invalid request format")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("registration_date")
    @Schema(description = "Дата регистрации пользователя (в формате yyyy-MM-dd)")
    private LocalDate registrationDate;
}
