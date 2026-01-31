package com.jjld.domain.garden.specification;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.garden.dto.ScheduleSearchCondition;
import com.jjld.domain.garden.entity.Garden;
import com.jjld.domain.garden.entity.Schedule;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ScheduleSearchSpecification {
    // 관리자 필터 조회를 위한 동적 필터 쿼리
    public static Specification<Schedule> withCondition(ScheduleSearchCondition cond) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (cond.getName() != null && !cond.getName().isBlank()) {
                Join<Schedule, Garden> gardenJoin = root.join("garden", JoinType.INNER);
                predicates.add(
                        cb.like(gardenJoin.get("name"), "%" + cond.getName() + "%")
                );
            }

            if (cond.getAdminName() != null && !cond.getAdminName().isBlank()) {
                Join<Schedule, Admin> adminJoin = root.join("admin", JoinType.INNER);
                predicates.add(
                        cb.like(adminJoin.get("adminName"), "%" + cond.getAdminName() + "%")
                );
            }

            if (cond.getWorkTitle() != null && !cond.getWorkTitle().isBlank()) {
                predicates.add(
                        cb.like(root.<String>get("workTitle"), "%" + cond.getWorkTitle() + "%")
                );
            }

            if (cond.getWorkStartDate() != null && cond.getWorkEndDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("workStartDate"), cond.getWorkStartDate())
                );
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("workEndDate"), cond.getWorkEndDate())
                );
            }

            if (cond.getState() != null) {
                predicates.add(
                        cb.equal(root.get("state"), cond.getState())
                );
            }

            if (cond.getPriority() != null) {
                predicates.add(
                        cb.equal(root.get("priority"), cond.getPriority())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

