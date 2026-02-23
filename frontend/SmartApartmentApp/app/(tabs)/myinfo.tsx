import { StyleSheet, Text, View } from "react-native";
import React from "react";
import Header from "@/components/Header";

export default function MyInfoScreen() {
  return (
    <View>
      <Header title="마이페이지" />
      <Text>마이페이지 화면</Text>
    </View>
  );
}

const styles = StyleSheet.create({});
