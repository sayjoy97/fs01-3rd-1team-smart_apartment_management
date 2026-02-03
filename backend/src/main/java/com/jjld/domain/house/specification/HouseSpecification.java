package com.jjld.domain.house.specification;

import com.jjld.domain.house.entity.House;
import org.springframework.data.jpa.domain.Specification;

public class HouseSpecification {

    // 동 필터
    public static Specification<House> equalHouseDong(Integer houseDong){
        return (root, query, cb) ->
                houseDong == null ? null:
                        cb.equal(root.get("houseDong"), houseDong);
    }

    // 호수 필터
    public static Specification<House> equalHouseHo(Integer houseHo){
        return (root, query, cb) ->
                houseHo == null ? null:
                    cb.equal(root.get("houseHo"), houseHo);
    }

    public static Specification<House> equalHouseholderName(String householderName){
        return (root, query, cb) ->
                householderName == null ? null:
                    cb.like(root.get("householderName"), "%" + householderName + "%");
    }
}
