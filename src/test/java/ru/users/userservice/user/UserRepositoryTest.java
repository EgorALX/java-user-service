package ru.users.userservice.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.users.userservice.model.User;
import ru.users.userservice.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {


    @Autowired
    private UserRepository userRepository;

    @Test
    public void getUsersByParamsTest() {

        User firstUser = new User(1L, "Ivan", "Ivanov",
                LocalDate.of(2023, 1, 1));
        User secondUser = new User(2L, "Ivan2", "Ivanov2",
                LocalDate.of(2020, 1, 1));
        User thirdUser = new User(3L, "Ivan3", "Ivanov3",
                LocalDate.of(2024, 1, 1));

        userRepository.saveAll(List.of(firstUser, secondUser, thirdUser));

        Pageable pageable = PageRequest.of(0, 10);

        List<User> result = userRepository.getUsersByParams("Ivan", null,
                LocalDate.of(2023, 1, 1), pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(firstUser.getName(), result.get(0).getName());
        assertEquals(firstUser.getSurname(), result.get(0).getSurname());
        assertEquals(firstUser.getRegistrationDate(), result.get(0).getRegistrationDate());

        List<User> resultWithSurname = userRepository.getUsersByParams(null, "Ivanov2",
                null, pageable);

        assertNotNull(resultWithSurname);
        assertEquals(1, resultWithSurname.size());
        assertEquals(secondUser.getName(), resultWithSurname.get(0).getName());
        assertEquals(secondUser.getSurname(), resultWithSurname.get(0).getSurname());
        assertEquals(secondUser.getRegistrationDate(), resultWithSurname.get(0).getRegistrationDate());

        List<User> resultWithNullParams = userRepository.getUsersByParams(null, null,
                null, pageable);

        assertNotNull(resultWithNullParams);
        assertEquals(3, resultWithNullParams.size());
    }
}
