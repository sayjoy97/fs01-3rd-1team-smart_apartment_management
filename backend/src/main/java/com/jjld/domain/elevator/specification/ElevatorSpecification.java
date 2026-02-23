package com.jjld.domain.elevator.specification;

import com.jjld.domain.elevator.dto.ElevatorSearchCondition;
import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.Enum.ElevatorState;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ElevatorSpecification {
    public static Specification<Elevator> withCondition(ElevatorSearchCondition cond) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 동 필터
            if (cond.getDong() != null) {
                predicates.add(cb.equal(root.get("dong"), cond.getDong()));
            }

            // 호기 필터
            if (cond.getHogi() != null) {
                predicates.add(cb.equal(root.get("hogi"), cond.getHogi()));
            }

            // 상태 필터
            if (cond.getState() != null && !cond.getState().isBlank()) {

                switch (cond.getState()) {

                    case "ERROR":
                        predicates.add(
                                cb.equal(root.get("state"), ElevatorState.ERROR)
                        );
                        break;

                    case "REPAIR":
                        predicates.add(
                                cb.equal(root.get("state"), ElevatorState.REPAIR)
                        );
                        break;

                    case "NORMAL":
                        predicates.add(
                                root.get("state").in(
                                        ElevatorState.IDLE,
                                        ElevatorState.MOVING,
                                        ElevatorState.DOOR_OPEN
                                )
                        );
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
