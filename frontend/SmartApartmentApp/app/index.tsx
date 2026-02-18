import AsyncStorage from "@react-native-async-storage/async-storage";
import { Redirect, router, useRouter } from "expo-router";
import { useEffect, useState } from "react";

export default function Index() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkToekn = async () => {
      const token = await AsyncStorage.getItem("accessToken");
      if (token) {
        router.replace("/(tabs)");
      } else {
        router.replace("/login");
      }
      setLoading(false);
    };

    checkToekn();
  }, []);

  if (loading) return null;
}
