import {
  LayoutDashboard,
  Home,
  Users,
  FileText,
  MessageSquare,
  Car,
  Settings,
  UserCircle,
  LogOut,
  Building2,
  Moon,
  Sun,
  Bell,
  DoorOpen,
  Leaf,
  X,
  Clock,
  Shield,
  ArrowUpDown,
  Volume2,
  Zap,
  ChevronDown,
} from "lucide-react";

// 새로운 메뉴 구조
export const menuStructure = [
  {
    id: "dashboard",
    label: "대시보드",
    icon: LayoutDashboard,
    type: "single",
  },
  {
    id: "residents-group",
    label: "세대·입주민",
    icon: Users,
    type: "dropdown",
    items: [
      { id: "residents", label: "세대 관리", icon: Users, path: "/house" },
      { id: "complaints", label: "민원 관리", icon: MessageSquare, path: "/complaint" },
      { id: "notices", label: "공지사항", icon: FileText, path: "/notices" },
    ],
  },
  {
    id: "security-group",
    label: "출입·보안",
    icon: DoorOpen,
    type: "dropdown",
    items: [
      { id: "accesscontrol", label: "출입 관리", icon: DoorOpen },
      { id: "vehicles", label: "방문차량 관리", icon: Car, path: "/cargate" },
    ],
  },
  {
    id: "facility-group",
    label: "시설",
    icon: ArrowUpDown,
    type: "dropdown",
    items: [
      { id: "elevator", label: "엘리베이터 관리", icon: ArrowUpDown },
      { id: "garden", label: "정원 관리", icon: Leaf },
    ],
  },
  {
    id: "environment-group",
    label: "환경·에너지",
    icon: Zap,
    type: "dropdown",
    items: [
      { id: "energy", label: "에너지 관리", icon: Zap },
      { id: "noise", label: "층간소음 관리", icon: Volume2 },
    ],
  },
  {
    id: "system-group",
    label: "시스템",
    icon: Shield,
    type: "dropdown",
    items: [{ id: "admins", label: "관리자 관리", icon: Shield }],
  },
];
