package ru.users.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class UserDto {

    private Integer id;

    private String name;

    private String surname;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("registration_date")
    private LocalDate registrationDate;
}
