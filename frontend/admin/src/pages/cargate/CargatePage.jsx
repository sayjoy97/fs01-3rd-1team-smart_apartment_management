import React, { useEffect, useState } from "react";
import { getCargateRecordList, getRegisCarList, last7TypeCountList } from "../../api/cargateAPI";

export function CargatePage() {
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지 번호
  const [typeCountList, setCountList] = useState([]); // 최근 7일 타입별 리스트
  const [cargateRecordList, setCargateRecordList] = useState([]);
  const [regisCarList, setRegisCarList] = useState([]);

  useEffect(() => {
    // 최근 7일 타입별 카운트 리스트
    last7TypeCountList()
      .then((res) => {
        console.log("최근 7일 타입별 카운트 리스트 조회성공: ", res.content || []);

        setCountList(res.content);
      })
      .catch((err) => console.log("최근 7일 타입별 카운트 호출 실패: ", err));

    // 차량 출입기록 리스트 조회
    getCargateRecordList({ size: 10, page: currentPage })
      .then((res) => {
        console.log("차량 출입기록 리스트 조회성공: ", res.content || []);
        setCargateRecordList(res.content);
      })
      .catch((err) => console.log("차량 출입기록 리스트 조회 실패: ", err));

    // 세대 등록차량 리스트
    getRegisCarList({ size: 10, page: currentPage })
      .then((res) => {
        console.log("세대 등록차량 리스트 조회성공: ", res.content || []);
        setRegisCarList(res.content);
      })
      .catch((err) => console.log("세대 등록차량 리스트 조회 실패: ", err));
  }, [currentPage]);

  return <div></div>;
}
