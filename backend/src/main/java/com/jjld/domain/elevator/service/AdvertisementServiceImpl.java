package com.jjld.domain.elevator.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.elevator.dao.AdvertisementDAO;
import com.jjld.domain.elevator.dto.AdvertisementReq;
import com.jjld.domain.elevator.entity.Advertisement;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private final AdvertisementDAO advertisementDAO;
    private final AdminDAO adminDAO;
    private final ModelMapper modelMapper;

    // 광고 등록
    @Override
    public void createAdvertisement(Long adminId, AdvertisementReq advertisementReq) {
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "광고를 등록할 관리자를 찾을 수 없습니다."));

        Advertisement advertisement = Advertisement.builder()
                .admin(admin)
                .advertisementTitle(advertisementReq.getAdvertisementTitle())
                .advertisementContent(advertisementReq.getAdvertisementContent())
                .advertisementStartDate(advertisementReq.getAdvertisementStartDate())
                .advertisementEndDate(advertisementReq.getAdvertisementEndDate())
                .build();

        advertisementDAO.save(advertisement);
    }


}
