import React, { createContext, useContext, useState } from "react";

export interface MyHouseInfo {
  houseDong: number;
  houseHo: number;
  householderName: string;
  householderPhone: string;
  householderEmail: string;
  moveInAt: string;
}

interface UserContextType {
  userInfo: MyHouseInfo | null;
  setUserInfo: React.Dispatch<React.SetStateAction<MyHouseInfo | null>>;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider = ({ children }: { children: React.ReactNode }) => {
  const [userInfo, setUserInfo] = useState<MyHouseInfo | null>(null);

  return <UserContext.Provider value={{ userInfo, setUserInfo }}>{children}</UserContext.Provider>;
};

export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("useUser must be used within UserProvider");
  }
  return context;
};
