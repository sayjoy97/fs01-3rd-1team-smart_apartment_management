import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Building2, LogIn } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";

export default function LoginPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: "",
    password: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    console.log("LOGIN SUBMIT"); // 추가

    // ✅ 아무 값으로 로그인 허용
    localStorage.setItem(
      "auth",
      JSON.stringify({
        username: form.username || "admin",
        isFirstLogin: false,
      }),
    );

    navigate("/", { replace: true });
  };

  return (
    <div className="min-h-screen w-full bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 flex items-center justify-center">
      <Card className="w-full max-w-md shadow-lg">
        <CardHeader className="text-center space-y-2">
          <div className="flex justify-center">
            <div className="bg-primary p-3 rounded-full">
              <Building2 className="size-7 text-primary-foreground" />
            </div>
          </div>
          <CardTitle className="text-2xl">아파트 관리 시스템</CardTitle>
          <CardDescription>단지 관리자 로그인</CardDescription>
        </CardHeader>

        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-1">
              <Label htmlFor="username">아이디</Label>
              <Input
                id="username"
                name="username"
                placeholder="아이디를 입력하세요"
                value={form.username}
                onChange={handleChange}
              />
            </div>

            <div className="space-y-1">
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                type="password"
                name="password"
                placeholder="비밀번호를 입력하세요"
                value={form.password}
                onChange={handleChange}
              />
            </div>

            <Button type="submit" className="w-full gap-2">
              <LogIn className="size-4" />
              로그인
            </Button>
          </form>

          <div className="mt-6 text-center text-xs text-muted-foreground">
            <p>데모용: 아무 값이나 입력하여 로그인 가능</p>
            <p className="mt-1">
              💡 최초 로그인 테스트: <span className="font-mono text-primary">new_admin</span>
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
