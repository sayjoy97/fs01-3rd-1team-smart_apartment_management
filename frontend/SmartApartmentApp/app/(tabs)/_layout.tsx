import { StyleSheet, Text, View } from "react-native";
import React from "react";
import AntDesign from "@expo/vector-icons/AntDesign";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import ElevatorScreen from "./elevator";
import NoticeScreen from "./notice";
import MyInfoScreen from "./myinfo";
import ComplaintScreen from ".";

export default function TabLayout() {
  const Tabs = createBottomTabNavigator();

  return (
    <Tabs.Navigator
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          height: 70,
          paddingBottom: 10,
          paddingTop: 5,
          backgroundColor: "white",
          borderTopWidth: 1,
          borderTopColor: "#ddd",
        },
        tabBarLabelStyle: {
          fontSize: 14,
          fontWeight: "500",
        },
        tabBarActiveTintColor: "#2563eb",
        tabBarInactiveTintColor: "#6b7280",
      }}
    >
      <Tabs.Screen
        name="index"
        component={ComplaintScreen}
        options={{
          title: "민원",
          tabBarIcon: ({ color, size, focused }) => (
            <AntDesign name="comment" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="elevator"
        component={ElevatorScreen}
        options={{
          title: "엘리베이터",
          tabBarIcon: ({ color, size, focused }) => (
            <MaterialIcons name="elevator" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="notice"
        component={NoticeScreen}
        options={{
          title: "공지조회",
          tabBarIcon: ({ color, size, focused }) => (
            <MaterialIcons name="announcement" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="myinfo"
        component={MyInfoScreen}
        options={{
          title: "My정보",
          tabBarIcon: ({ color, size, focused }) => (
            <MaterialIcons name="tag-faces" size={size} color={color} />
          ),
        }}
      />
    </Tabs.Navigator>
  );
}

const styles = StyleSheet.create({});
