import { Outlet } from "react-router-dom";

export default function AuthLayout({ children }) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-background">
      {children ?? <Outlet />}
    </div>
  );
}
