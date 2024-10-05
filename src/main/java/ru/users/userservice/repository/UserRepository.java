package ru.users.userservice.repository;

import ru.users.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User AS u WHERE " +
            "(LOWER(u.name) = LOWER(:name) OR :name IS NULL) AND " +
            "(LOWER(u.surname) = LOWER(:surname) OR :surname IS NULL) AND " +
            "(u.registrationDate = :registrationDate OR :registrationDate IS NULL)")
    List<User> getUsersByParams(@Param("name") String name,
                                @Param("surname") String surname,
                                @Param("registrationDate") LocalDate registrationDate,
                                Pageable pageable);
}
