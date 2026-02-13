import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import styles from "./LoginPage.module.css";
import {Building2, LogIn} from "lucide-react";

import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {login} from "../../api/admin/adminAPI";

export default function LoginPage() {
  const navigate = useNavigate();

  const [adminform, setAdminForm] = useState({
    adminLoginId: "",
    adminPass: "",
  });

  const handleChange = (e) => {
    const {name, value} = e.target;
    setAdminForm((prev) => ({...prev, [name]: value}));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    login(adminform)
      .then((data) => {
        localStorage.setItem("accessToken", data.loginRes.accessToken);
        localStorage.setItem("roles", JSON.stringify(data.loginRes.roles));
        localStorage.setItem("adminId", data.loginRes.loginAdminRes.adminId);

        if (data.loginAdminRes.isFristLogin) {
          navigate("/initial-setup");
        } else {
          navigate("/");
        }
      })
      .catch((err) => {
        // console.log("로그인 실패: ", err);
        // console.log(err.response?.error);
        // // const status = err.response.status;
        // const {code, message} = err.response.data || {};

        // switch (code) {
        //   case "INVALID_CREDENTIALS":
        //     alert(message);
        //     break;
        //   default:
        //     alert("알 수 없는 오류가 발생했습니다.");
        console.error("로그인 실패:", err);

        if (err.response && err.response.data) {
          // const {code, message} = err.response.error || {};
          switch (err.response.error.errorcode) {
            case "INVALID_CREDENTIALS":
              alert(err.response.error.message);
              break;
            default:
              alert("서버 오류 발생");
          }
        } else {
          alert("서버 연결 실패 또는 CORS 오류");
        }
        return null;
      });
  };

  return (
    <div className={styles.container}>
      <div className={styles.loginBox}>
        <div className={styles.header}>
          <div className={styles.logoWrapper}>
            <div className={styles.logoCircle}>
              <Building2 className={styles.logoIcon} />
            </div>
          </div>
          <h2 className={styles.title}>아파트 관리 시스템</h2>
          <p className={styles.subtitle}>단지 관리자 로그인</p>
        </div>
        <div className={styles.content}>
          <div className="{fieldGroup}">
            <Label htmlFor="adminLoginId">아이디</Label>
            <Input
              id="adminLoginId"
              type="text"
              name="adminLoginId"
              placeholder="아이디를 입력하세요"
              value={adminform.adminLoginId}
              onChange={handleChange}
            />
          </div>
          <div className="{inputText}">
            <Label htmlFor="adminPass">비밀번호</Label>
            <Input
              id="adminPass"
              type="password"
              name="adminPass"
              placeholder="비밀번호를 입력하세요"
              value={adminform.adminPass}
              onChange={handleChange}
            />
          </div>
          <Button className={styles.loginButton} onClick={handleSubmit}>
            <LogIn className={styles.loginIcon} />
            로그인
          </Button>
          {/* 비밀번호 찾기 버튼 생성 예정 }*/}
          <div className={styles.testInfo}>
            <p>아이디: admin01</p>
            <p>패스워드: 11111111 (1 여덟 개)</p>
          </div>
        </div>
      </div>
    </div>

    // <div className="min-h-screen w-full bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 flex items-center justify-center">
    //   <Card className="w-full max-w-md shadow-lg">
    //     <CardHeader className="text-center space-y-2">
    //       <div className="flex justify-center">
    //         <div className="bg-primary p-3 rounded-full">
    //           <Building2 className="size-7 text-primary-foreground" />
    //         </div>
    //       </div>
    //       <CardTitle className="text-2xl">아파트 관리 시스템</CardTitle>
    //       <CardDescription>단지 관리자 로그인</CardDescription>
    //     </CardHeader>

    //     <CardContent>
    //       <form onSubmit={handleSubmit} className="space-y-4">
    //         <div className="space-y-1">
    //           <Label htmlFor="username">아이디</Label>
    //           <Input
    //             id="username"
    //             name="username"
    //             placeholder="아이디를 입력하세요"
    //             value={adminform.adminLoginId}
    //             onChange={handleChange}
    //           />
    //         </div>

    //         <div className="space-y-1">
    //           <Label htmlFor="password">비밀번호</Label>
    //           <Input
    //             id="password"
    //             type="password"
    //             name="password"
    //             placeholder="비밀번호를 입력하세요"
    //             value={adminform.adminPass}
    //             onChange={handleChange}
    //           />
    //         </div>

    //         <Button type="submit" className="w-full gap-2">
    //           <LogIn className="size-4" />
    //           로그인
    //         </Button>
    //       </form>

    //       <div className="mt-6 text-center text-xs text-muted-foreground">
    //         <p>아이디: admin01</p>
    //         <p>패스워드: 11111111 (1 여덟 개)</p>
    //         {/* <p className="mt-1">
    //           💡 최초 로그인 테스트: <span className="font-mono text-primary">new_admin</span>
    //         </p> */}
    //       </div>
    //     </CardContent>
    //   </Card>
    // </div>
  );
}
