package com.jjld.domain.house.repository;

import com.jjld.domain.house.dto.login.UserLoginResponse;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByHouseholderEmail(String householderEmail);

    Account findByHouse_HouseIdAndActiveTrue(Long houseId);
}
