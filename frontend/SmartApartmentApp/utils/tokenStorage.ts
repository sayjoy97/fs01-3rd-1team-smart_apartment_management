import AsyncStorage from "@react-native-async-storage/async-storage";

// 토큰 저장
export const saveToken = async (token: string) => {
  await AsyncStorage.setItem("accessToken", token);
};

// 토큰 조회
export const getToken = async () => {
  return await AsyncStorage.getItem("accessToken");
};

// 토큰 삭제
export const removeToken = async () => {
  await AsyncStorage.removeItem("accessToken");
};
