package ru.users.userservice.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ParamsUserDto {

    @Parameter(description = "Номер страницы для пагинации")
    @PositiveOrZero
    private Integer page;

    @Parameter(description = "Количество записей на странице")
    @PositiveOrZero
    private Integer size;

    @Parameter(description = "Значение для фильтрации по имени пользователя")
    @Size(max = 256, message = "Name cannot exceed 256 characters")
    private String name;

    @Parameter(description = "Значение для фильтрации по фамилии пользователя")
    @Size(max = 256, message = "Surname cannot exceed 256 characters")
    private String surname;

    @Parameter(description = "Дата регистрации для фильтрации")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;

    public PageRequest getPageable() {
        return PageRequest.of(
                page != null ? page - 1: 0,
                size != null ? size : 10
        );
    }
}
