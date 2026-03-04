package com.jjld.domain.admin.specification;

import com.jjld.domain.admin.dto.AdminSearchCondition;
import com.jjld.domain.admin.entity.Admin;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AdminSpecification {
    // 관리자 필터 조회를 위한 동적 필터 쿼리
    public static Specification<Admin> withCondition(AdminSearchCondition cond) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (cond.getAdminLoginId() != null && !cond.getAdminLoginId().isBlank()) {
                predicates.add(cb.like(root.<String>get("adminLoginId"), "%" + cond.getAdminLoginId() + "%"));
            }

            if (cond.getAdminName() != null && !cond.getAdminName().isBlank()) {
                predicates.add(cb.like(root.<String>get("adminName"), "%" + cond.getAdminName() + "%"));
            }

            if (cond.getState() != null) {
                predicates.add(cb.equal(root.get("state"), cond.getState()));
            }

            if (cond.getAdminRole() != null) {
                predicates.add(cb.equal(root.get("adminRole"), cond.getAdminRole()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
