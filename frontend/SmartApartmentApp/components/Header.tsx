import { View, Text, StyleSheet, Dimensions, TouchableOpacity, Modal } from "react-native";
import React, { useEffect, useState } from "react";
import { getMyHouseApi } from "../api/UserApi";
import { LogOut } from "lucide-react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";
import { toast } from "sonner";

interface HeaderProps {
  title?: string;
}

export default function Header({ title }: HeaderProps) {
  const router = useRouter();
  const [userInfo, setUserInfo] = useState<{ houseDong: string; houseHo: string }>({
    houseDong: "",
    houseHo: "",
  });
  const [showLogoutModal, setShowLogoutModal] = useState(false);

  useEffect(() => {
    const fetchHouse = async () => {
      try {
        // 캐시 정보
        const cachedUser = await AsyncStorage.getItem("currentUser");
        if (cachedUser) setUserInfo(JSON.parse(cachedUser));

        const data = await getMyHouseApi();
        setUserInfo(data);

        await AsyncStorage.setItem("currentUser", JSON.stringify(data));
      } catch (err: any) {
        console.log("로그인 사용자 정보 가져오기 실패: ", err.message);
        router.replace("/login");
      }
    };

    fetchHouse();
  }, []);

  const handleLogout = () => {
    setShowLogoutModal(true);
  };

  const confirmLogout = async () => {
    try {
      await AsyncStorage.removeItem("currentUser");
      await AsyncStorage.removeItem("accessToken");
      toast.success("로그아웃되었습니다.");
      router.replace("/login");
    } catch (err) {
      toast.error("로그아웃중 오류가 발생했습니다");
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.headerRow}>
        <View>
          {title && <Text style={styles.title}>{title}</Text>}
          <Text style={styles.userText}>
            {userInfo.houseDong}동 {userInfo.houseHo}호
          </Text>
        </View>
        <TouchableOpacity style={styles.logoutBtn} onPress={handleLogout}>
          <LogOut size={20} color="#fff" />
        </TouchableOpacity>
      </View>
      <Modal transparent visible={showLogoutModal} animationType="fade">
        <View style={styles.modalBackground}>
          <View style={styles.modalBox}>
            <View style={styles.modalTitle}>
              <Text>로그아웃</Text>
            </View>
            <Text style={styles.modalText}>
              <Text>정말 로그아웃 하시겠습니까?</Text>
            </Text>
            <View style={styles.modalBtns}>
              <TouchableOpacity
                style={[styles.modalBtn, styles.cancelBtn]}
                onPress={() => setShowLogoutModal(false)}
              >
                <Text style={styles.cancelText}>취소</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.modalBtn, styles.confirmBtn]}
                onPress={confirmLogout}
              >
                <Text style={styles.confirmText}>확인</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#1E40AF",
    padding: 16,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    elevation: 3,
  },
  headerRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  title: {
    fontSize: 16,
    fontWeight: "bold",
    color: "white",
  },
  userText: {
    fontSize: 16,
    color: "#fff",
    marginTop: 2,
  },
  logoutBtn: {
    padding: 8,
  },
  modalBackground: {
    flex: 1,
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    justifyContent: "center",
    alignItems: "center",
  },
  modalBox: {
    backgroundColor: "white",
    borderRadius: 12,
    padding: 24,
    width: "80%",
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 8,
  },
  modalText: {
    fontSize: 14,
    marginBottom: 16,
  },
  modalBtns: {
    flexDirection: "row",
    justifyContent: "space-between",
  },
  modalBtn: {
    flex: 1,
    padding: 12,
    borderRadius: 8,
    alignItems: "center",
  },
  cancelBtn: {
    backgroundColor: "#E5E7EB",
    marginRight: 8,
  },
  confirmBtn: {
    backgroundColor: "#1E40AF",
  },
  cancelText: {
    color: "#374151",
    fontWeight: "bold",
  },
  confirmText: {
    color: "white",
    fontWeight: "bold",
  },
});
