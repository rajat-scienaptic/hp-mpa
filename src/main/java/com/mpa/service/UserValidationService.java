package com.mpa.service;

import com.mpa.model.mpa.User;

public interface UserValidationService {
    User getUserService(String userName, String email);
    String getUserNameFromCookie(String cookie);
}
