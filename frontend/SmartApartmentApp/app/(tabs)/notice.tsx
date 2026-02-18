import { StyleSheet, Text, View } from "react-native";
import React from "react";
import Header from "@/components/Header";

export default function NoticeScreen() {
  return (
    <View>
      <Header title="아파트 공지조회" />
      <Text>공지사항 화면</Text>
    </View>
  );
}

const styles = StyleSheet.create({});
