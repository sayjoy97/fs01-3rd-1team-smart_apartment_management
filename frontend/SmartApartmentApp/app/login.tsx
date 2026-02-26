import AsyncStorage from "@react-native-async-storage/async-storage";
import { router } from "expo-router";
import { Building2, X } from "lucide-react";
import { useState } from "react";
import { Modal, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { changePasswordApi, loginApi } from "../api/authApi";

export default function Login() {
  // 비밀번호 변경 모달
  const [showPasswordChangeModal, setShowPasswordChangeModal] = useState(false);

  // 비밀번호 변경 값
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [loginData, setLoginData] = useState({
    houseDong: "",
    houseHo: "",
    householderEmail: "",
    password: "",
  });

  // 로그인 버튼 클릭 함수
  const runLogin = async () => {
    try {
      const payload = {
        houseDong: Number(loginData.houseDong),
        houseHo: Number(loginData.houseHo),
        householderEmail: loginData.householderEmail,
        password: loginData.password,
      };

      const res = await loginApi(payload);
      const data = res.data || res;
      // 토큰이 있으면 저장
      if (data && data.accessToken) {
        await AsyncStorage.setItem("accessToken", data.accessToken);
      } else {
        console.log("토큰 없음", data);
      }

      // 사용자 정보 저장
      await AsyncStorage.setItem(
        "userInfo",
        JSON.stringify({
          email: data.householderEmail,
          houseDong: data.houseDong,
          houseHo: data.houseHo,
        }),
      );

      // 최초 로그인의 경우
      if (data.firstLogin) {
        setShowPasswordChangeModal(true);
        return;
      }

      router.replace("/(tabs)");
    } catch (err: any) {
      alert(`로그인 실패: ${err.message}`);
    }
  };

  // 비밀번호 변경 함수
  const runPasswordChange = async () => {
    if (!newPassword || !confirmPassword) {
      alert("값을 입력하세요");
      return;
    }

    if (newPassword !== confirmPassword) {
      alert("비밀번호가 불일치 합니다");
      return;
    }

    try {
      const message = await changePasswordApi({
        newPassword,
      });

      alert(message);
      setShowPasswordChangeModal(false);
      router.replace("/(tabs)");
    } catch (error) {
      alert("비밀번호 변경을 실패했습니다");
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <View style={styles.iconWrapper}>
          <Building2 color="white" size={32} />
        </View>
        <Text style={styles.title}>아파트 입주민 앱</Text>
        <Text style={styles.subtitle}>
          계정이 등록되지 않은 경우, 관리사무소에서 등록신청을 하세요
        </Text>
      </View>

      <View style={styles.form}>
        <View style={styles.row}>
          <View style={styles.inputGroup}>
            <Text style={styles.label}>동</Text>
            <TextInput
              style={styles.input}
              value={loginData.houseDong}
              placeholder="ex: 101"
              placeholderTextColor="#999"
              keyboardType="numeric"
              onChangeText={(text) => setLoginData({ ...loginData, houseDong: text })}
            />
          </View>
          <View style={styles.inputGroup}>
            <Text style={styles.label}>호수</Text>
            <TextInput
              style={styles.input}
              placeholder="ex: 101"
              placeholderTextColor="#999"
              value={loginData.houseHo}
              keyboardType="numeric"
              onChangeText={(text) => setLoginData({ ...loginData, houseHo: text })}
            />
          </View>
        </View>
        <View style={styles.inputGroup}>
          <Text style={styles.label}>이메일</Text>
          <TextInput
            style={styles.input}
            placeholder="ex: xxxxx@gmail.com"
            placeholderTextColor="#999"
            value={loginData.householderEmail}
            keyboardType="numeric"
            onChangeText={(text) => setLoginData({ ...loginData, householderEmail: text })}
          />
        </View>
        <View style={styles.inputGroup}>
          <Text style={styles.label}>비밀번호</Text>
          <TextInput
            secureTextEntry={true}
            style={styles.input}
            placeholderTextColor="#999"
            placeholder="비밀번호를 입력하세요"
            value={loginData.password}
            keyboardType="numeric"
            onChangeText={(text) => setLoginData({ ...loginData, password: text })}
          />
        </View>

        <TouchableOpacity style={styles.loginButton} onPress={runLogin}>
          <Text style={styles.loginButtonText}>로그인</Text>
        </TouchableOpacity>
      </View>

      {/* 최초 로그인 비밀번호 변경 모달 */}
      <Modal visible={showPasswordChangeModal} transparent animationType="slide">
        <View style={styles.modalOverlay}>
          <View style={styles.modal}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>최초 로그인 비밀번호 변경</Text>
              <TouchableOpacity onPress={() => setShowPasswordChangeModal(false)}>
                <X color="gray" size={24} />
              </TouchableOpacity>
            </View>

            <View style={styles.modalBody}>
              <View style={styles.inputGroup}>
                <Text style={styles.label}>현재 비밀번호</Text>
                <TextInput
                  secureTextEntry={true}
                  value={loginData.password}
                  editable={false}
                  style={[styles.input, styles.disabledInput]}
                />
              </View>
              <View style={styles.inputGroup}>
                <Text style={styles.label}>새 비밀번호</Text>
                <TextInput
                  secureTextEntry={true}
                  style={styles.input}
                  placeholder="새 비밀번호"
                  value={newPassword}
                  onChangeText={setNewPassword}
                />
              </View>
              <View style={styles.inputGroup}>
                <Text style={styles.label}>새 비밀번호 확인</Text>
                <TextInput
                  secureTextEntry={true}
                  style={styles.input}
                  placeholder="비밀번호 확인"
                  value={confirmPassword}
                  onChangeText={setConfirmPassword}
                />
              </View>
              <TouchableOpacity style={styles.confirmButton} onPress={runPasswordChange}>
                <Text style={styles.confirmButtonText}>비밀번호 변경하기</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </View>
  );
}

// 로그인 화면 스타일입니다.
const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 16,
    backgroundColor: "#eff6ff",
  },
  header: {
    alignItems: "center",
    marginBottom: 32,
  },
  iconWrapper: {
    backgroundColor: "#2563eb",
    padding: 16,
    borderRadius: 50,
    marginBottom: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: "700",
    color: "#1f2937",
  },
  subtitle: {
    fontSize: 12,
    color: "#6b7280",
    textAlign: "center",
    marginTop: 4,
  },
  form: {
    width: "100%",
    maxWidth: 360,
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
  },
  inputGroup: {
    flex: 1,
    marginBottom: 16,
    marginRight: 8,
  },
  label: {
    fontSize: 14,
    fontWeight: "500",
    marginBottom: 4,
    color: "#374151",
  },
  input: {
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 12,
    paddingVertical: 10,
    paddingHorizontal: 12,

    backgroundColor: "white",
  },
  disabledInput: {
    backgroundColor: "#f3f4f6",
  },
  loginButton: {
    backgroundColor: "#2563eb",
    paddingVertical: 14,
    borderRadius: 12,
    alignItems: "center",
    marginTop: 8,
  },
  loginButtonText: {
    color: "white",
    fontSize: 16,
    fontWeight: 600,
  },
  modalOverlay: {
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    justifyContent: "center",
    alignItems: "center",
    padding: 16,
  },
  modal: {
    width: "100%",
    maxWidth: 360,
    backgroundColor: "white",
    borderRadius: 16,
    padding: 16,
  },
  modalHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 16,
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: "700",
  },
  modalBody: {
    marginBottom: 16,
  },
  confirmButton: {
    backgroundColor: "#2563eb",
    paddingVertical: 14,
    borderRadius: 12,
    alignItems: "center",
  },
  confirmButtonText: {
    color: "white",
    fontSize: 16,
    fontWeight: "600",
  },
});
