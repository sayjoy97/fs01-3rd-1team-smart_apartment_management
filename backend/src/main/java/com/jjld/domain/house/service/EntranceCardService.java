package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.EntranceCardResponse;

import java.util.List;

public interface EntranceCardService {
    List<EntranceCardResponse> findAll();
}
