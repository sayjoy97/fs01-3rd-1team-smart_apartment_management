import { useEffect, useMemo, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import {
  Building2,
  Moon,
  Sun,
  Bell,
  UserCircle,
  Settings,
  LogOut,
  ChevronDown,
} from "lucide-react";

import { menuStructure } from "../constants/menu";
import { logout } from "../api/admin/adminAPI";

export default function Header() {
  const navigate = useNavigate();
  const location = useLocation();

  const [isDarkMode, setIsDarkMode] = useState(false);
  const [openDropdown, setOpenDropdown] = useState(null);
  const [showNotifications, setShowNotifications] = useState(false);

  const [notifications, setNotifications] = useState([
    {
      id: 1,
      category: "민원",
      title: "새로운 민원이 접수되었습니다",
      time: "5분 전",
      unread: true,
    },
    {
      id: 2,
      category: "시설물",
      title: "시설물 점검일이 다가왔습니다",
      time: "1시간 전",
      unread: true,
    },
    {
      id: 3,
      category: "관리비",
      title: "관리비 미납 세대가 있습니다",
      time: "2시간 전",
      unread: false,
    },
  ]);

  const unreadCount = useMemo(() => notifications.filter((n) => n.unread).length, [notifications]);

  useEffect(() => {
    document.documentElement.classList.toggle("dark", isDarkMode);
  }, [isDarkMode]);

  const handleLogout = () => {
    logout(localStorage.getItem("adminId"))
      .then((res) => {
        console.log("로그아웃 성공");
      })
      .catch((err) => {
        const status = err.response.status;
        const { code, message } = err.response.data.error;

        switch (code) {
          case "ADMIN_NOT_FOUND":
            alert(message);
            break;
          default:
            alert("알 수 없는 오류가 발생했습니다.");
        }
      });

    localStorage.removeItem("auth");
    navigate("/login", { replace: true });
  };

  const toggleDropdown = (groupId) => {
    setOpenDropdown((prev) => (prev === groupId ? null : groupId));
  };

  const go = (path) => {
    setOpenDropdown(null);
    setShowNotifications(false);
    navigate(path);
  };

  const markAsRead = (id, e) => {
    e.stopPropagation();
    setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, unread: false } : n)));
  };

  const isActivePath = (menuId) => {
    if (menuId === "dashboard") return location.pathname === "/";
    return location.pathname.startsWith(`/${menuId}`);
  };

  return (
    <header className="app-header fixed top-0 left-0 right-0 z-50">
      {/* 상단 바 */}
      <div className="bg-card border-b border-border">
        <div className="flex items-center justify-between px-6 py-4">
          {/* 로고 */}
          <div className="flex items-center gap-3">
            <div className="bg-primary p-2 rounded-lg text-primary-foreground">
              <Building2 className="size-6" />
            </div>
            <div className="leading-tight">
              <div className="text-lg font-semibold text-foreground">행복 아파트</div>
              <div className="text-sm text-muted-foreground">관리자 시스템</div>
            </div>
          </div>

          {/* 우측 액션 */}
          <div className="flex items-center gap-2">
            {/* 다크모드 */}
            <button
              onClick={() => setIsDarkMode((v) => !v)}
              className="h-10 w-10 rounded-lg hover:bg-accent flex items-center justify-center text-foreground"
            >
              {isDarkMode ? <Sun className="size-5" /> : <Moon className="size-5" />}
            </button>

            {/* 알림 */}
            <div className="relative">
              <button
                onClick={() => setShowNotifications((v) => !v)}
                className="h-10 w-10 rounded-lg hover:bg-accent flex items-center justify-center relative text-foreground"
              >
                <Bell className="size-5" />
                {unreadCount > 0 && (
                  <span className="absolute -top-1 -right-1 h-5 min-w-5 px-1 rounded-full bg-destructive text-destructive-foreground text-xs flex items-center justify-center">
                    {unreadCount}
                  </span>
                )}
              </button>

              {showNotifications && (
                <div className="absolute right-0 mt-2 w-96 bg-card border border-border rounded-lg shadow-lg overflow-hidden">
                  <div className="p-4 border-b border-border">
                    <div className="font-medium text-foreground">알림</div>
                    <div className="text-xs text-muted-foreground mt-1">
                      읽지 않은 알림이 강조 표시됩니다
                    </div>
                  </div>

                  <div className="max-h-80 overflow-y-auto">
                    {notifications.length === 0 ? (
                      <div className="p-8 text-center text-sm text-muted-foreground">
                        알림이 없습니다
                      </div>
                    ) : (
                      notifications.map((n) => (
                        <div
                          key={n.id}
                          onClick={() => go("/notifications")}
                          className={`p-4 border-b border-border cursor-pointer hover:bg-accent/60 ${
                            n.unread ? "bg-accent/40" : ""
                          }`}
                        >
                          <div className="flex items-start justify-between gap-3">
                            <div>
                              <div className="text-xs text-muted-foreground">
                                {n.category} · {n.time}
                              </div>
                              <div className="text-sm font-medium text-foreground mt-1">
                                {n.title}
                              </div>
                            </div>
                            {n.unread && (
                              <button
                                onClick={(e) => markAsRead(n.id, e)}
                                className="text-xs px-2 py-1 rounded-md border border-border hover:bg-accent text-foreground"
                              >
                                읽음
                              </button>
                            )}
                          </div>
                        </div>
                      ))
                    )}
                  </div>

                  <div className="p-3 text-center">
                    <button
                      onClick={() => go("/notifications")}
                      className="text-sm text-primary hover:underline font-medium"
                    >
                      모두 보기
                    </button>
                  </div>
                </div>
              )}
            </div>

            {/* 마이페이지 */}
            <button
              onClick={() => navigate("/mypage")}
              className="h-10 px-3 rounded-lg hover:bg-accent flex items-center gap-2 text-foreground"
            >
              <UserCircle className="size-4" />
              <span className="text-sm">마이페이지</span>
            </button>

            {/* 설정 */}
            <button
              onClick={() => go("/settings")}
              className="h-10 px-3 rounded-lg hover:bg-accent flex items-center gap-2 text-foreground"
            >
              <Settings className="size-4" />
              <span className="text-sm">설정</span>
            </button>

            {/* 로그아웃 */}
            <button
              onClick={handleLogout}
              className="h-10 px-3 rounded-lg hover:bg-accent flex items-center gap-2 text-foreground"
            >
              <LogOut className="size-4" />
              <span className="text-sm">로그아웃</span>
            </button>
          </div>
        </div>
      </div>

      {/* 메뉴 바 */}
      <nav className="bg-card border-b border-border">
        <div className="flex items-center px-6">
          {menuStructure.map((menu) => {
            const Icon = menu.icon;

            if (menu.type === "single") {
              return (
                <button
                  key={menu.id}
                  onClick={() => go(menu.id === "dashboard" ? "/" : `/${menu.id}`)}
                  className={`flex items-center gap-2 px-4 py-3 border-b-2 transition-colors whitespace-nowrap ${
                    isActivePath(menu.id)
                      ? "border-primary text-primary"
                      : "border-transparent text-muted-foreground hover:text-foreground"
                  }`}
                >
                  <Icon className="size-4" />
                  <span>{menu.label}</span>
                </button>
              );
            }

            const isActive = menu.items?.some((it) => isActivePath(it.id));
            const isOpen = openDropdown === menu.id;

            return (
              <div key={menu.id} className="relative">
                <button
                  onClick={() => toggleDropdown(menu.id)}
                  className={`flex items-center gap-2 px-4 py-3 border-b-2 transition-colors whitespace-nowrap ${
                    isActive || isOpen
                      ? "border-primary text-primary"
                      : "border-transparent text-muted-foreground hover:text-foreground"
                  }`}
                >
                  <Icon className="size-4" />
                  <span>{menu.label}</span>
                  <ChevronDown
                    className={`size-4 transition-transform ${isOpen ? "rotate-180" : ""}`}
                  />
                </button>

                {isOpen && (
                  <div className="absolute top-full left-0 w-56 bg-card border border-border rounded-b-lg shadow-lg z-50">
                    {menu.items.map((item) => {
                      const ItemIcon = item.icon;
                      return (
                        <button
                          key={item.id}
                          onClick={() => go(item.path || `/${item.id}`)}
                          className={`w-full flex items-center gap-2 px-4 py-3 text-left transition-colors ${
                            isActivePath(item.id)
                              ? "bg-accent text-primary"
                              : "text-foreground hover:bg-accent/60"
                          }`}
                        >
                          <ItemIcon className="size-4" />
                          <span className="text-sm">{item.label}</span>
                        </button>
                      );
                    })}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </nav>
    </header>
  );
}
