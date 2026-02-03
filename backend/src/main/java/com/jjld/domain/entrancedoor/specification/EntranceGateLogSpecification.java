package com.jjld.domain.entrancedoor.specification;

import com.jjld.domain.entrancedoor.entity.EntranceGateLog;
import org.springframework.data.jpa.domain.Specification;

public class EntranceGateLogSpecification {

    // 세대 동으로 조회
    public static Specification<EntranceGateLog> equalHouseDong(Integer houseDong){
        return (root, query, cb) ->
                houseDong == null ? null:
                        cb.equal(root.join("house").get("houseDong"), houseDong);
    }

    // 출입 유형으로 조회
    public static Specification<EntranceGateLog> equalAccessType(String accessType){
        return(root, query, cb) ->
            accessType == null ? null:
            cb.equal(root.get("accessType"), accessType);
    }
}
