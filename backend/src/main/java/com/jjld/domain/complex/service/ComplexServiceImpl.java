package com.jjld.domain.complex.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.Enum.AdminRole;
import com.jjld.domain.complex.dao.ComplexDAO;
import com.jjld.domain.complex.dto.ComplexReq;
import com.jjld.domain.complex.dto.ComplexRes;
import com.jjld.domain.complex.entity.Complex;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.ConflictException;
import com.jjld.global.exception.businessexceptions.ForbiddenException;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComplexServiceImpl implements ComplexService {
    private final ComplexDAO complexDAO;
    private final AdminDAO adminDAO;
    private final ModelMapper modelMapper;

    // 단지 정보 생성
    @Override
    public void createComplex(Long adminId, ComplexReq complexReq) {
        if (complexDAO.existsComplex()) {
            throw new ConflictException(ErrorCode.APARTMENT_COMPLEX_ALREADY_EXISTS, "이미 아파트 단지 정보가 존재합니다.");
        }

        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "단지 정보를 생성할 관리자를 찾을 수 없습니다."));

        if (admin.getAdminRole().equals(AdminRole.ADMIN)) {
            throw new ForbiddenException(ErrorCode.SUPER_ADMIN_ONLY, "총 관리자만 접근할 수 있는 기능입니다.");
        }

        Complex complex = modelMapper.map(complexReq, Complex.class);

        complexDAO.updateComplex(complex);
    }

    // 단지 정보 조회
    @Override
    public ComplexRes getComplex() {
        Complex complex = complexDAO.getComplex().orElse(null);

        if (complex == null) {
            throw new NotFoundException(ErrorCode.APARTMENT_COMPLEX_NOT_FOUND, "아파트 단지 정보를 찾을 수 없습니다.");
        }

        ComplexRes response = modelMapper.map(complex, ComplexRes.class);

        return response;
    }

    // 단지 정보 수정
    @Override
    public void updateComplex(Long adminId, ComplexReq complexReq) {
        Complex currentComplex = complexDAO.getComplex().orElse(null);
        if (currentComplex == null) {
            throw new NotFoundException(ErrorCode.APARTMENT_COMPLEX_NOT_FOUND, "아파트 단지 정보를 찾을 수 없습니다.");
        }

        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "단지 정보를 수정할 관리자를 찾을 수 없습니다."));

        if (admin.getAdminRole().equals(AdminRole.ADMIN)) {
            throw new ForbiddenException(ErrorCode.SUPER_ADMIN_ONLY, "총 관리자만 접근할 수 있는 기능입니다.");
        }

        Complex complex = modelMapper.map(complexReq, Complex.class);
        complex.setId(currentComplex.getId());

        complexDAO.updateComplex(complex);
    }
}
