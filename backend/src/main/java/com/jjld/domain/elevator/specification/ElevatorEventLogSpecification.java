package com.jjld.domain.elevator.specification;

import com.jjld.domain.elevator.dto.ElevatorEventLogSearchCondition;
import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.ElevatorEventLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ElevatorEventLogSpecification {
    public static Specification<ElevatorEventLog> withCondition(Elevator elevator, ElevatorEventLogSearchCondition cond) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("elevator"), elevator));

            // 상태 필터
            if (cond.getEventType() != null) {

                predicates.add(
                        cb.equal(root.get("eventType"), cond.getEventType())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
