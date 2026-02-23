import React from "react";
import backendServer from "./backendServer";
import requests from "./requests";

// 차량 출입관리 페이지 간단 요금저보 조회
export const getSimpleCharge = async () => {
  try {
    const response = await backendServer.get(requests.getSimpleCharge);

    console.log("API응답: ", response.data);

    return response.data;
  } catch (error) {
    console.error("간단 요금 정보 호출 도중 에러발생: ", error);
    return [];
  }
};

// 요금관련 종합정보
export const getTotalChargeInfo = async () => {
  try {
    const response = await backendServer.get(requests.getTotalList);

    console.log("API응답: ", response.data);

    return response.data;
  } catch (error) {
    console.error("전체 요금 정보 호출 도중 에러발생: ", error);
    return [];
  }
};

// 최근 30일 일별 요금정보 호출
export const getDailyList = async () => {
  try {
    const response = await backendServer.get(requests.getDaily30TotalList);

    console.log("API응답: ", response.data);

    return response.data;
  } catch (error) {
    console.error("일별 요금 정보 호출 도중 에러발생: ", error);
    return [];
  }
};

// 최근 12개월 월별 요금정보 호출
export const getMonthlyList = async () => {
  try {
    const response = await backendServer.get(requests.getMonthlyTotalList);

    console.log("API응답: ", response.data);

    return response.data;
  } catch (error) {
    console.error("월별 요금 정보 호출 도중 에러발생: ", error);
    return [];
  }
};

// 최근 3년 연간 요금정보 호출
export const getYearlyList = async () => {
  try {
    const response = await backendServer.get(requests.getYearlyTotalList);

    console.log("API응답: ", response.data);

    return response.data;
  } catch (error) {
    console.error("연별 요금 정보 호출 도중 에러발생: ", error);
    return [];
  }
};

// 요금 설정정보 호출
export const getChargeSettingInfo = async () => {
  try {
    const response = await backendServer.get(requests.getChargeSetting);

    console.log("API응답: ", response.data);

    return response.data;
  } catch (error) {
    console.error("요금 설정 정보 호출 도중 에러발생: ", error);
    return [];
  }
};

// 요금 설정정보 수정
export const chargeSettingUpdate = async (updateDate) => {
  try {
    const response = await backendServer.put(requests.updateChargeSetting, {
      data: updateDate,
    });

    return response;
  } catch (error) {
    console.error("요금 설정 업데이트 도중 에러발생: ", error);
  }
};
