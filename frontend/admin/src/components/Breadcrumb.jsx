import { Link, useLocation, useParams } from "react-router-dom";

const routeNameMap = {
  "": "행복아파트", // index route
  dash: "",
  mypage: "마이페이지",
  house: "세대 관리",
  complaint: "민원 관리",
  entrance: "출입 관리",
  elevator: "엘리베이터 관리",
  admins: "관리자 관리",
  noise: "층간소음 관리",
  habitual: "상습 구간 관리",
  energy: "에너지 관리",
  notices: "공지사항",
  notice: "공지사항",
  write: "공지사항 등록",
  cargate: "방문차량 관리",
  feeDetail: "요금 현황",
};

export default function Breadcrumb() {
  const location = useLocation();
  const params = useParams();

  const pathnames = location.pathname.split("/").filter(Boolean);

  // / (index)일 때
  if (pathnames.length === 0) {
    return (
      <div className="breadcrumb">
        <span className="breadcrumb-current">{routeNameMap[""]}</span>
        <span className="breadcrumb-sep">›</span>
      </div>
    );
  }

  return (
    <div className="breadcrumb">
      <Link to="/" className="breadcrumb-link">
        행복아파트
      </Link>

      {pathnames.map((seg, idx) => {
        const to = "/" + pathnames.slice(0, idx + 1).join("/");
        const isLast = idx === pathnames.length - 1;

        // notices/:noticeId 같은 경우 id는 제목으로 바꾸고 싶으면 나중에 API로 치환 가능
        const isNoticeDetail = seg === params.noticeId;
        const label = isNoticeDetail
          ? "공지 상세"
          : seg.includes("habitual")
            ? "상습 구간 관리"
            : (routeNameMap[seg] ?? seg);

        return (
          <span key={to} className="breadcrumb-item">
            <span className="breadcrumb-sep">›</span>
            {isLast ? (
              <span className="breadcrumb-current">{label}</span>
            ) : (
              <Link to={to} className="breadcrumb-link">
                {label}
              </Link>
            )}
          </span>
        );
      })}
    </div>
  );
}
