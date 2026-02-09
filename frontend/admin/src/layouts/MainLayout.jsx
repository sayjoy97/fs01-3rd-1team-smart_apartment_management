import { Outlet } from "react-router-dom";
import Header from "../components/Header";
import "./MainLayout.css";

export default function MainLayout() {
  return (
    <div className="main-layout">
      <Header className="header" />

      {/* 헤더(상단바+메뉴바) 높이 보정 */}
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
