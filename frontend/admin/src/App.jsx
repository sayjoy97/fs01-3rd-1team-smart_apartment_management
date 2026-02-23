import { Routes, Route, Navigate } from "react-router-dom";

import MainLayout from "./layouts/MainLayout";
import AuthLayout from "./layouts/AuthLayout";

import LoginPage from "./pages/auth/LoginPage";
import Dashboard from "./pages/dashboard/Dashboard";
import HouseholdMangement from "./pages/house/HouseholdMangement";
import ComplaintsPage from "./pages/complaint/ComplaintsPage";
import NoisePage from "./pages/noise/NoisePage";
import HabitualPage from "./pages/noise/HabitualPage";
import EnergyPage from "./pages/energy/EnergyPage";
import { NoticesPage } from "./pages/notice/NoticesPage";
import { NoticeDetailPage } from "./pages/notice/NoticeDetailPage";
import { NoticeCreatePage } from "./pages/notice/NoticeCreatePage";
import { CargatePage } from "./pages/cargate/CargatePage";
import { FeeDetailPage } from "./pages/cargate/FeeDetailPage";

// 임시 인증 상태 (나중에 AuthContext로 교체)

// PrivateRoute 컴포넌트
function PrivateRoute({ children }) {
  const token = localStorage.getItem("accessToken"); // 🔥 이걸로 체크해야 함

  return token ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      {/* 로그인 */}
      <Route
        path="/login"
        element={
          <AuthLayout>
            <LoginPage />
          </AuthLayout>
        }
      />

      {/* 로그인 이후 영역 */}
      <Route
        path="/"
        element={
          <PrivateRoute>
            <MainLayout />
          </PrivateRoute>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="/house" element={<HouseholdMangement />} />
        <Route path="/complaint" element={<ComplaintsPage />} />
        <Route path="/noise" element={<NoisePage />} />
        <Route path="/noise/habitual" element={<HabitualPage />} />
        <Route path="/energy" element={<EnergyPage />} />
        <Route path="/notices" element={<NoticesPage />} />
        <Route path="/notices/:noticeId" element={<NoticeDetailPage />} />
        <Route path="/notice/write" element={<NoticeCreatePage />} />
        <Route path="/cargate" element={<CargatePage />} />
        <Route path="/cargate/feeDetail" element={<FeeDetailPage />} />
      </Route>

      {/* 그 외 전부 로그인으로 */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
