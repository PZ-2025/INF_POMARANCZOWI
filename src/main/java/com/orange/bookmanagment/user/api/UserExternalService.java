package com.orange.bookmanagment.user.api;

import com.orange.bookmanagment.user.exception.UserAlreadyExistException;
import com.orange.bookmanagment.user.exception.UserNotFoundException;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import com.orange.bookmanagment.user.web.requests.ChangePasswordRequest;
import com.orange.bookmanagment.user.web.requests.UserRegisterRequest;

import java.util.List;

public interface UserExternalService {

    long getUserIdByEmail(String email) throws UserNotFoundException;

}
