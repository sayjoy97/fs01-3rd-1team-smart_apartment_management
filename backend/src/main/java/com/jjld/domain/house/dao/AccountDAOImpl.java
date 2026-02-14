package com.jjld.domain.house.dao;

import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountDAOImpl implements AccountDAO{
    private final AccountRepository repository;

    @Override
    public Account login(String householderEmail) {
        return repository.findByHouseholderEmail(householderEmail);
    }
}
