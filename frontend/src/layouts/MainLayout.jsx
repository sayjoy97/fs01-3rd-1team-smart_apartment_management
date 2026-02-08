import { Outlet } from "react-router-dom";
import Header from "../components/Header";

export default function MainLayout() {
  return (
    <div className="min-h-screen bg-background">
      <Header />

      {/* 헤더(상단바+메뉴바) 높이 보정 */}
      <main className="pt-28 px-6">
        <Outlet />
      </main>
    </div>
  );
}
