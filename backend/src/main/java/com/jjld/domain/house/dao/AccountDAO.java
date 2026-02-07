package com.jjld.domain.house.dao;

import com.jjld.domain.house.entity.Account;

public interface AccountDAO {
    // 입주민 로그인
    Account login(String householderEmail);
}
