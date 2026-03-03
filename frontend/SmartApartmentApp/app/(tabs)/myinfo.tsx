import { changePasswordApi } from "@/api/authApi";
import Header from "@/components/Header";
import { useUser } from "@/contexts/UserContext";
import { Building2, Lock } from "lucide-react";
import { useState } from "react";
import { ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import Toast from "react-native-toast-message";

export default function MyInfoScreen() {
  const { userInfo } = useUser();
  const phone = userInfo?.householderPhone.replace(/(\d{3})(\d{4})(\d+)/, "$1-$2-$3");

  const [showPasswordChange, setShowPasswordChange] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  // 비밀번호 변경 함수
  const handlePasswordChange = async () => {
    if (!currentPassword || !newPassword || !confirmPassword) {
      Toast.show({
        type: "error",
        text1: "비밀번호 변경 실패",
        text2: "모든 항목을 입력해주세요",
        position: "top",
      });
    }

    if (newPassword !== confirmPassword) {
      Toast.show({
        type: "error",
        text1: "비밀번호 변경 실패",
        text2: "새 비밀번호가 일치하지 않습니다.",
        position: "top",
      });
      return;
    }

    if (newPassword.length < 4) {
      Toast.show({
        type: "error",
        text1: "비밀번호 변경 실패",
        text2: "비밀번호는 최소 4자리 이상이어야 합니다",
        position: "top",
      });
      return;
    }

    Toast.show({
      type: "success",
      text1: "비밀번호 변경 성공",
      text2: "비밀번호가 변경되었습니다.",
      position: "top",
    });

    try {
      const message = await changePasswordApi({
        newPassword,
        currentPassword,
      });

      setShowPasswordChange(false);
      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (error) {
      Toast.show({
        type: "error",
        text1: "비밀번호 변경 실패",
        text2: error?.response?.data?.message || "오류가 발생했습니다.",
        position: "top",
      });
    }
  };
  return (
    <View style={{ flex: 1 }}>
      <Header title="마이페이지" />

      <ScrollView
        style={[styles.container, { flex: 1 }]}
        contentContainerStyle={{ paddingBottom: 40 }}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.container}>
          <View style={styles.card}>
            <Text style={styles.userLabel}>My 정보</Text>
            <View style={styles.row}>
              <Building2 style={styles.icon} />
              <Text style={styles.userTitle}>
                {userInfo?.houseDong}동 {userInfo?.houseHo}호
              </Text>
            </View>

            <View style={styles.infoBox}>
              <Text style={styles.infoLabel}>대표 세대주명</Text>
              <Text style={styles.infoValue}>{userInfo?.householderName}</Text>
            </View>
            <View style={styles.infoBox}>
              <Text style={styles.infoLabel}>대표 이메일</Text>
              <Text style={styles.infoValue}>{userInfo?.householderEmail}</Text>
            </View>
            <View style={styles.infoBox}>
              <Text style={styles.infoLabel}>대표 전화번호</Text>
              <Text style={styles.infoValue}>{phone}</Text>
            </View>
          </View>

          <View style={styles.card}>
            <View style={styles.rowBetween}>
              <View style={styles.rowBetween}>
                <Lock style={styles.icon} />
                <Text style={styles.setctionTitle}>비밀번호 변경</Text>
              </View>

              <TouchableOpacity onPress={() => setShowPasswordChange(!showPasswordChange)}>
                <Text style={styles.changeButtonText}>{showPasswordChange ? "취소" : "변경"}</Text>
              </TouchableOpacity>
            </View>

            {showPasswordChange ? (
              <View style={styles.passwordSection}>
                <Text style={styles.label}>현재 비밀번호</Text>
                <TextInput
                  secureTextEntry
                  value={currentPassword}
                  onChangeText={setCurrentPassword}
                  style={styles.input}
                  placeholder="현재 비밀번호 입력"
                  placeholderTextColor="#9CA3AF"
                />

                <Text style={styles.label}>새 비밀번호</Text>
                <TextInput
                  secureTextEntry
                  value={newPassword}
                  onChangeText={setNewPassword}
                  style={styles.input}
                  placeholder="새 비밀번호 입력"
                  placeholderTextColor="#9CA3AF"
                />

                <Text style={styles.label}>새 비밀번호 확인</Text>
                <TextInput
                  secureTextEntry
                  value={confirmPassword}
                  onChangeText={setConfirmPassword}
                  style={styles.input}
                  placeholder="새 비밀번호 재입력"
                  placeholderTextColor="#9CA3AF"
                />

                <TouchableOpacity style={styles.submitButton} onPress={handlePasswordChange}>
                  <Text style={styles.submitButtonText}>비밀번호 변경</Text>
                </TouchableOpacity>
              </View>
            ) : (
              <Text style={styles.description}>보안을 위해 주기적으로 비밀번호를 변경해주세요</Text>
            )}
          </View>
          <View style={styles.infoNotice}>
            <Text style={styles.infoNoticeText}>회원 정보는 관리사무소를 통해 관리됩니다.</Text>
          </View>
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 10,
  },
  card: {
    backgroundColor: "#ffffff",
    padding: 20,
    borderRadius: 12,
    marginBottom: 16,
    elevation: 2,
    shadowColor: "#000",
    shadowOpacity: 0.05,
    shadowRadius: 5,
    shadowOffset: { width: 0, height: 2 },
  },
  userLabel: {
    fontSize: 14,
    color: "#6b7280",
  },
  userTitle: {
    fontSize: 20,
    fontWeight: "500",
    marginBottom: 16,
  },
  infoBox: {
    backgroundColor: "#f9fafb",
    padding: 12,
    borderRadius: 8,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: "#d1d5db",
  },
  infoLabel: {
    fontSize: 12,
    color: "#6b7280",
  },
  infoValue: {
    fontSize: 16,
    fontWeight: "400",
  },
  icon: {
    marginRight: 10,
    color: "#6b6b6b",
  },
  row: {
    marginTop: 10,
    flexDirection: "row",
  },
  rowBetween: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  setctionTitle: {
    fontSize: 16,
    fontWeight: "500",
  },
  changeButtonText: {
    fontSize: 14,
    color: "#2563eb",
  },
  passwordSection: {
    marginTop: 16,
  },
  label: {
    fontSize: 14,
    marginBottom: 4,
  },
  input: {
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 8,
    padding: 10,
    marginBottom: 12,
  },
  submitButton: {
    backgroundColor: "#2563eb",
    padding: 14,
    borderRadius: 8,
    alignItems: "center",
  },
  submitButtonText: {
    color: "#ffffff",
    fontWeight: "500",
    fontSize: 16,
  },
  description: {
    marginTop: 10,
    fontSize: 12,
    color: "#6b7280",
  },
  infoNotice: {
    backgroundColor: "#dbeafe",
    padding: 14,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#74acf5",
  },
  infoNoticeText: {
    color: "#2f57c5",
    fontSize: 14,
  },
});
