package com.orange.bookmanagment.user.repository;

import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepository userRepository;

    private User testUser;
    private static final String EMAIL = "test@example.com";
    private static final String FIRST_NAME = "Jan";
    private static final String LAST_NAME = "Kowalski";
    private static final String PASSWORD = "encodedPassword";
    private static final long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User(PASSWORD, EMAIL, LAST_NAME, FIRST_NAME, UserType.READER);
        testUser.setId(USER_ID);
    }

    @Test
    void findUserByEmail_shouldDelegateToJpaRepository() {
        // given
        when(userJpaRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(testUser));

        // when
        Optional<User> result = userRepository.findUserByEmail(EMAIL);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userJpaRepository).findUserByEmail(EMAIL);
    }

    @Test
    void findUserByEmail_whenUserDoesNotExist_shouldReturnEmpty() {
        // given
        when(userJpaRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userRepository.findUserByEmail(EMAIL);

        // then
        assertThat(result).isEmpty();
        verify(userJpaRepository).findUserByEmail(EMAIL);
    }

    @Test
    void findUserById_shouldDelegateToJpaRepository() {
        // given
        when(userJpaRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        // when
        Optional<User> result = userRepository.findUserById(USER_ID);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userJpaRepository).findById(USER_ID);
    }

    @Test
    void findUserById_whenUserDoesNotExist_shouldReturnEmpty() {
        // given
        when(userJpaRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userRepository.findUserById(USER_ID);

        // then
        assertThat(result).isEmpty();
        verify(userJpaRepository).findById(USER_ID);
    }

    @Test
    void createUser_shouldDelegateToJpaRepository() {
        // given
        User newUser = new User(PASSWORD, EMAIL, LAST_NAME, FIRST_NAME, UserType.READER);
        when(userJpaRepository.save(newUser)).thenReturn(testUser);

        // when
        User result = userRepository.createUser(newUser);

        // then
        assertThat(result).isEqualTo(testUser);
        verify(userJpaRepository).save(newUser);
    }

    @Test
    void updateUser_shouldDelegateToJpaRepository() {
        // given
        User updatedUser = new User(PASSWORD, EMAIL, "UpdatedLastName", "UpdatedFirstName", UserType.READER);
        updatedUser.setId(USER_ID);

        when(userJpaRepository.save(updatedUser)).thenReturn(updatedUser);

        // when
        User result = userRepository.updateUser(updatedUser);

        // then
        assertThat(result).isEqualTo(updatedUser);
        verify(userJpaRepository).save(updatedUser);
    }

    @Test
    void findUsersByFirstName_shouldDelegateToJpaRepository() {
        // given
        User user1 = new User(PASSWORD, "jan@example.com", "Nowak", FIRST_NAME, UserType.READER);
        User user2 = new User(PASSWORD, "jan2@example.com", "Kowalski", FIRST_NAME, UserType.READER);
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userJpaRepository.findUsersByFirstName(FIRST_NAME)).thenReturn(expectedUsers);

        // when
        List<User> result = userRepository.findUsersByFirstName(FIRST_NAME);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedUsers);
        verify(userJpaRepository).findUsersByFirstName(FIRST_NAME);
    }

    @Test
    void findUsersByFirstName_whenNoUsersFound_shouldReturnEmptyList() {
        // given
        when(userJpaRepository.findUsersByFirstName(FIRST_NAME)).thenReturn(List.of());

        // when
        List<User> result = userRepository.findUsersByFirstName(FIRST_NAME);

        // then
        assertThat(result).isEmpty();
        verify(userJpaRepository).findUsersByFirstName(FIRST_NAME);
    }

    @Test
    void existsById_whenUserExists_shouldReturnTrue() {
        // given
        when(userJpaRepository.existsById(USER_ID)).thenReturn(true);

        // when
        boolean result = userRepository.existsById(USER_ID);

        // then
        assertThat(result).isTrue();
        verify(userJpaRepository).existsById(USER_ID);
    }

    @Test
    void existsById_whenUserDoesNotExist_shouldReturnFalse() {
        // given
        when(userJpaRepository.existsById(USER_ID)).thenReturn(false);

        // when
        boolean result = userRepository.existsById(USER_ID);

        // then
        assertThat(result).isFalse();
        verify(userJpaRepository).existsById(USER_ID);
    }
}