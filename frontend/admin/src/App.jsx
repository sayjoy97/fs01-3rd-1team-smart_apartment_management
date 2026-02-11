import { Routes, Route, Navigate } from "react-router-dom";

import MainLayout from "./layouts/MainLayout";
import AuthLayout from "./layouts/AuthLayout";

import LoginPage from "./pages/auth/LoginPage";
import Dashboard from "./pages/dashboard/Dashboard";
import HouseholdManagement from "./pages/house/HouseholdManagement";
import ComplaintsPage from "./pages/complaint/ComplaintsPage";
import EntranceDoor from "./pages/entrance/EntranceDoor";

// 임시 인증 상태 (나중에 AuthContext로 교체)

// PrivateRoute 컴포넌트
function PrivateRoute({ children }) {
  const auth = localStorage.getItem("auth");
  return auth ? children : <Navigate to="/login" replace />;
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
        <Route path="/house" element={<HouseholdManagement />} />
        <Route path="/complaint" element={<ComplaintsPage />} />
        <Route path="/entrance" element={<EntranceDoor />} />
      </Route>

      {/* 그 외 전부 로그인으로 */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
