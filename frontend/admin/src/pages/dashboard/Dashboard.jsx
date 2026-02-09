import { useState } from "react";

export default function Dashboard() {
  const [showFirstLoginModal, setShowFirstLoginModal] = useState(() => {
    try {
      const auth = JSON.parse(localStorage.getItem("auth"));
      return Boolean(auth?.isFirstLogin);
    } catch {
      return false;
    }
  });

  const handleSaveFirstLogin = () => {
    const auth = JSON.parse(localStorage.getItem("auth")) || {};
    localStorage.setItem("auth", JSON.stringify({ ...auth, isFirstLogin: false }));
    setShowFirstLoginModal(false);
  };

  return (
    <>
      <h1 className="text-3xl font-bold mb-4">대시보드</h1>
      <p className="text-gray-500">관리자 시스템 메인 화면입니다.</p>

      {showFirstLoginModal && (
        <div className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center">
          <div className="bg-white rounded-2xl p-6 w-full max-w-lg">
            <h2 className="text-xl font-bold mb-2">최초 로그인 정보 입력</h2>
            <p className="text-sm text-gray-500 mb-4">계속 진행하려면 추가 정보를 입력해주세요.</p>

            <div className="space-y-3">
              <input placeholder="이름" className="w-full h-11 px-3 border rounded-lg" />
              <input placeholder="전화번호" className="w-full h-11 px-3 border rounded-lg" />
              <input placeholder="이메일" className="w-full h-11 px-3 border rounded-lg" />
              <input
                placeholder="새 비밀번호 (8자 이상)"
                type="password"
                className="w-full h-11 px-3 border rounded-lg"
              />
              <input
                placeholder="비밀번호 확인"
                type="password"
                className="w-full h-11 px-3 border rounded-lg"
              />
            </div>

            <button
              onClick={handleSaveFirstLogin}
              className="mt-5 w-full h-11 bg-blue-600 text-white rounded-lg font-medium"
            >
              저장하고 계속
            </button>
          </div>
        </div>
      )}
    </>
  );
}
