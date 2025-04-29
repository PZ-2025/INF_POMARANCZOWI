package com.orange.bookmanagment.user.service.impl;

import com.orange.bookmanagment.user.exception.IllegalAccountAccessException;
import com.orange.bookmanagment.user.exception.UserAlreadyExistException;
import com.orange.bookmanagment.user.exception.UserNotFoundException;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import com.orange.bookmanagment.user.repository.UserRepository;
import com.orange.bookmanagment.user.web.requests.ChangePasswordRequest;
import com.orange.bookmanagment.user.web.requests.UserRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User testUser;
    private static final String EMAIL = "test@example.com";
    private static final String FIRST_NAME = "Jan";
    private static final String LAST_NAME = "Kowalski";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User(ENCODED_PASSWORD, EMAIL, LAST_NAME, FIRST_NAME, UserType.READER);
        testUser.setId(USER_ID);
    }

    @Test
    void getUserByEmail_whenUserExists_shouldReturnUser() {
        // given
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(testUser));

        // when
        User result = userService.getUserByEmail(EMAIL);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        verify(userRepository).findUserByEmail(EMAIL);
    }

    @Test
    void getUserByEmail_whenUserDoesNotExist_shouldThrowException() {
        // given
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.getUserByEmail(EMAIL))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by email");
    }

    @Test
    void getUserIdByEmail_whenUserExists_shouldReturnUserId() {
        // given
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(testUser));

        // when
        long result = userService.getUserIdByEmail(EMAIL);

        // then
        assertThat(result).isEqualTo(USER_ID);
        verify(userRepository).findUserByEmail(EMAIL);
    }

    @Test
    void getUserIdByEmail_whenUserDoesNotExist_shouldThrowException() {
        // given
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.getUserIdByEmail(EMAIL))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by email");
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUser() {
        // given
        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(testUser));

        // when
        User result = userService.getUserById(USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(USER_ID);
        verify(userRepository).findUserById(USER_ID);
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldThrowException() {
        // given
        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.getUserById(USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by id");
    }

    @Test
    void getUserByFirstName_shouldReturnUsersList() {
        // given
        User user1 = new User(ENCODED_PASSWORD, "jan@example.com", "Nowak", FIRST_NAME, UserType.READER);
        User user2 = new User(ENCODED_PASSWORD, "jan2@example.com", "Kowalski", FIRST_NAME, UserType.READER);
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findUsersByFirstName(FIRST_NAME)).thenReturn(expectedUsers);

        // when
        List<User> result = userService.getUserByFirstName(FIRST_NAME);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedUsers);
        verify(userRepository).findUsersByFirstName(FIRST_NAME);
    }

    @Test
    void registerUser_whenUserDoesNotExist_shouldRegisterUser() {
        // given
        UserRegisterRequest registerRequest = new UserRegisterRequest(
                PASSWORD, EMAIL, LAST_NAME, FIRST_NAME
        );

        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.createUser(any(User.class))).thenReturn(testUser);

        // when
        User result = userService.registerUser(registerRequest, UserType.READER);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(EMAIL);

        verify(userRepository).findUserByEmail(EMAIL);
        verify(passwordEncoder).encode(PASSWORD);
        verify(userRepository).createUser(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getEmail()).isEqualTo(EMAIL);
        assertThat(capturedUser.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(capturedUser.getLastName()).isEqualTo(LAST_NAME);
        assertThat(capturedUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(capturedUser.getUserType()).isEqualTo(UserType.READER);
    }

    @Test
    void registerUser_whenUserAlreadyExists_shouldThrowException() {
        // given
        UserRegisterRequest registerRequest = new UserRegisterRequest(
                PASSWORD, EMAIL, LAST_NAME, FIRST_NAME
        );

        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(testUser));

        // when/then
        assertThatThrownBy(() -> userService.registerUser(registerRequest, UserType.READER))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessage("User already exists");

        verify(userRepository).findUserByEmail(EMAIL);
        verify(userRepository, never()).createUser(any(User.class));
    }

    @Test
    void updateUser_whenUserExists_shouldUpdateUser() {
        // given
        User updatedUser = new User(ENCODED_PASSWORD, EMAIL, "UpdatedLastName", "UpdatedFirstName", UserType.READER);
        updatedUser.setId(USER_ID);

        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(userRepository.updateUser(updatedUser)).thenReturn(updatedUser);

        // when
        User result = userService.updateUser(updatedUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(result.getLastName()).isEqualTo("UpdatedLastName");

        verify(userRepository).findUserByEmail(EMAIL);
        verify(userRepository).updateUser(updatedUser);
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldThrowException() {
        // given
        User updatedUser = new User(ENCODED_PASSWORD, EMAIL, "UpdatedLastName", "UpdatedFirstName", UserType.READER);

        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.updateUser(updatedUser))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by email");

        verify(userRepository).findUserByEmail(EMAIL);
        verify(userRepository, never()).updateUser(any(User.class));
    }

    @Test
    void changeUserPassword_whenOldPasswordMatches_shouldChangePassword() {
        // given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, "newPassword");

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        // when
        userService.changeUserPassword(USER_ID, changePasswordRequest);

        // then
        verify(userRepository).findUserById(USER_ID);
        verify(passwordEncoder).matches(PASSWORD, ENCODED_PASSWORD);
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).updateUser(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getPassword()).isEqualTo("newEncodedPassword");
    }

    @Test
    void changeUserPassword_whenOldPasswordDoesNotMatch_shouldThrowException() {
        // given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, "newPassword");

        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> userService.changeUserPassword(USER_ID, changePasswordRequest))
                .isInstanceOf(IllegalAccountAccessException.class)
                .hasMessage("Old password is not correct");

        verify(userRepository).findUserById(USER_ID);
        verify(passwordEncoder).matches(PASSWORD, ENCODED_PASSWORD);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).updateUser(any(User.class));
    }

    @Test
    void existsById_whenUserExists_shouldReturnTrue() {
        // given
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        // when
        boolean result = userService.existsById(USER_ID);

        // then
        assertThat(result).isTrue();
        verify(userRepository).existsById(USER_ID);
    }

    @Test
    void existsById_whenUserDoesNotExist_shouldReturnFalse() {
        // given
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        // when
        boolean result = userService.existsById(USER_ID);

        // then
        assertThat(result).isFalse();
        verify(userRepository).existsById(USER_ID);
    }
}