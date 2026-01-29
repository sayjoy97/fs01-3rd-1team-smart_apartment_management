package com.jjld.domain.admin.specification;

import com.jjld.domain.admin.dto.HistorySearchCondition;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.History;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class HistorySpecification {
    public static Specification<History> withCondition(
            Admin admin,
            HistorySearchCondition cond
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("admin"), admin));

            if (cond.getSuccess() != null) {
                predicates.add(cb.equal(root.get("success"), cond.getSuccess()));
            }

            if (cond.getAccessType() != null) {
                predicates.add(cb.equal(root.get("accessType"), cond.getAccessType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}