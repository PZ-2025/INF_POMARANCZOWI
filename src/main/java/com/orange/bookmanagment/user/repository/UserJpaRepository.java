package com.orange.bookmanagment.user.repository;

import com.orange.bookmanagment.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User,Long> {
    Optional<User> findUserByEmail(String email);

    List<User> findUsersByFirstName(String firstName);

    @Query(value = "SELECT * FROM users WHERE user_type = 'LIBRARIAN' ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<User> findRandomLibrarian();
}
