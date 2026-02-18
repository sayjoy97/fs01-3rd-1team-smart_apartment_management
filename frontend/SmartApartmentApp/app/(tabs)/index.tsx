import Header from "@/components/Header";
import { Platform, StyleSheet, Text, View } from "react-native";

export default function ComplaintScreen() {
  return (
    <View style={styles.container}>
      <Header title="민원" />
      <View style={styles.listView}>
        <Text style={styles.listView}>민원</Text>
      </View>
      <View style={styles.listView}>
        <Text style={styles.listView}>엘리베이터</Text>
      </View>
      <View style={styles.listView}>
        <Text style={styles.listView}>공지조회</Text>
      </View>
      <View style={styles.listView}>
        <Text style={styles.listView}>My정보</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: Platform.OS === "android" ? 20 : 0,
    backgroundColor: "#f0f0f8",
  },
  pageTitle: {
    marginBottom: 35,
    paddingHorizontal: 15,
    fontSize: 55,
    fontWeight: "600",
  },
  listView: {
    flex: 1,
  },
  listTitle: {
    marginBottom: 25,
    fontSize: 25,
    paddingHorizontal: 15,
    fontWeight: "500",
  },
  separator: {
    marginHorizontal: 10,
    marginTop: 25,
    marginBottom: 10,
    borderBottomWidth: 1,
    borderBottomColor: "rgba(0, 0, 0, 0.5)",
  },
});
