package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.login.UserLoginRequest;
import com.jjld.domain.house.dto.login.UserLoginResponse;

public interface AccountService {
    UserLoginResponse login(UserLoginRequest request);
}
