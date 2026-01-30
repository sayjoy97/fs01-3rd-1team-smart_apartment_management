package com.jjld.domain.garden.controller;

import com.jjld.domain.garden.service.GardenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/garden/api")
@RequiredArgsConstructor
public class GardenController {
    private final GardenService gardenService;
}
