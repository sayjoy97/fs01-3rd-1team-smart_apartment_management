import { Button } from "../../../components/ui/button";
import { Settings, ListChecks } from "lucide-react";

export default function NoiseHeader({ onOpenHabitual, onOpenPolicy }) {
  return (
    <div className="noise2-header">
      <div>
        <h1 className="noise2-title">층간소음 관리</h1>
        <p className="noise2-subtitle">센서 기반 층간소음 감지 및 관리자 처리 시스템</p>
      </div>

      <div className="noise2-header-actions">
        <Button variant="outline" className="noise2-btn-outline-red" onClick={onOpenHabitual}>
          <ListChecks className="size-4 mr-2" />
          상습 구간 관리
        </Button>

        <Button variant="outline" onClick={onOpenPolicy}>
          <Settings className="size-4 mr-2" />
          정책 설정
        </Button>
      </div>
    </div>
  );
}
