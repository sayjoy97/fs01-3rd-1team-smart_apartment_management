package com.jjld.domain.complaint.specification;

import com.jjld.domain.complaint.entity.Complaint;
import org.springframework.data.jpa.domain.Specification;

public class ComplaintSpecification {

    // 필터 카테고리 조회
    public static Specification<Complaint> equalCategory(String category){
        return (root, query, cb) ->
                category == null ? null :
                cb.equal(root.get("category"), category);
    }

    // 필터 상태 조건 조회
    public static Specification<Complaint> equalStatus(String status){
        return (root, query, cb) ->
                status == null ? null :
                cb.equal(root.get("status"), status);
    }
}
