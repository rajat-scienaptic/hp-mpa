package com.mpa.service;

import com.mpa.model.User;

public interface UserValidationService {
    User getUserService(String userName, String email);
    String getUserNameFromCookie(String cookie);
}
