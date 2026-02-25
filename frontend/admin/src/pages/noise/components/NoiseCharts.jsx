import { Card, CardContent, CardHeader, CardTitle } from "../../../components/ui/card";
import { Button } from "../../../components/ui/button";
import { Activity, Moon, Sun as SunIcon, PieChart as PieIcon, BarChart3 } from "lucide-react";

import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  PieChart,
  Pie,
  Cell,
  Legend,
} from "recharts";

export default function NoiseCharts({
  statisticsLoading,
  charts,
  viewMode,
  onChangeViewMode,
  colors,
  compact = false,
  mode = "all", // ✅ "all" | "hourly" (기본 all)
}) {
  const barH = compact ? 220 : 300;
  const pieH = compact ? 180 : 240;

  const HourlyCard = (
    <Card className="noise2-card">
      <CardHeader className="noise2-cardheader">
        <div className="noise2-cardheader-row">
          <div>
            <CardTitle className="noise2-cardtitle">
              {viewMode === "day" ? <SunIcon className="size-5 text-orange-500" /> : null}
              {viewMode === "night" ? <Moon className="size-5 text-indigo-500" /> : null}
              {viewMode === "all" ? <Activity className="size-5" /> : null}
              시간대별 소음 발생 현황
            </CardTitle>
            <div className="noise2-carddesc">0~23시 기준</div>
          </div>

          <div className="noise2-seg">
            <Button
              size="sm"
              variant={viewMode === "all" ? "default" : "outline"}
              onClick={() => onChangeViewMode("all")}
            >
              전체
            </Button>
            <Button
              size="sm"
              variant={viewMode === "day" ? "default" : "outline"}
              className={viewMode === "day" ? "noise2-btn-day" : ""}
              onClick={() => onChangeViewMode("day")}
            >
              <SunIcon className="size-3 mr-1" />
              주간
            </Button>
            <Button
              size="sm"
              variant={viewMode === "night" ? "default" : "outline"}
              className={viewMode === "night" ? "noise2-btn-night" : ""}
              onClick={() => onChangeViewMode("night")}
            >
              <Moon className="size-3 mr-1" />
              야간
            </Button>
          </div>
        </div>
      </CardHeader>

      <CardContent>
        {statisticsLoading ? (
          <div className="noise2-empty">로딩 중...</div>
        ) : !charts?.noiseByHour?.length ? (
          <div className="noise2-empty">데이터가 없습니다.</div>
        ) : (
          <ResponsiveContainer width="100%" height={barH}>
            <BarChart
              data={charts.noiseByHour}
              margin={
                compact
                  ? { top: 6, right: 8, left: 0, bottom: 0 }
                  : { top: 10, right: 16, left: 0, bottom: 8 }
              }
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis
                dataKey="hour"
                style={{ fontSize: compact ? "10px" : "12px" }}
                interval={compact ? 3 : 2}
                tickMargin={compact ? 4 : 8}
              />
              <YAxis style={{ fontSize: compact ? "10px" : "12px" }} width={compact ? 28 : 36} />
              <Tooltip />
              {!compact && <Legend />}
              <Bar dataKey="count" name="발생" />
            </BarChart>
          </ResponsiveContainer>
        )}
      </CardContent>
    </Card>
  );

  const DistributionCard = (
    <Card className="noise2-card">
      <CardHeader className="noise2-cardheader">
        <CardTitle className="noise2-cardtitle">
          <PieIcon className="size-5 text-blue-500" />
          센서/패턴 분포
        </CardTitle>
        <div className="noise2-carddesc">센서유형 / 패턴(NoisePattern1)</div>
      </CardHeader>

      <CardContent>
        {statisticsLoading ? (
          <div className="noise2-empty">로딩 중...</div>
        ) : (
          <div className="noise2-piegrid">
            <div>
              <div className="noise2-smalllabel">센서 유형</div>
              {!charts?.sensorPie?.length ? (
                <div className="noise2-empty">데이터 없음</div>
              ) : (
                <ResponsiveContainer width="100%" height={pieH}>
                  <PieChart>
                    <Pie
                      data={charts.sensorPie}
                      dataKey="value"
                      nameKey="name"
                      cx="50%"
                      cy="50%"
                      outerRadius={70}
                      label={({ percent }) => `${(percent * 100).toFixed(0)}%`}
                    >
                      {charts.sensorPie.map((_, idx) => (
                        <Cell key={idx} fill={colors[idx % colors.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              )}
            </div>

            <div>
              <div className="noise2-smalllabel">패턴(1차)</div>
              {!charts?.patternPie?.length ? (
                <div className="noise2-empty">데이터 없음</div>
              ) : (
                <ResponsiveContainer width="100%" height={pieH}>
                  <PieChart>
                    <Pie
                      data={charts.patternPie}
                      dataKey="value"
                      nameKey="name"
                      cx="50%"
                      cy="50%"
                      outerRadius={70}
                      label={({ percent }) => `${(percent * 100).toFixed(0)}%`}
                    >
                      {charts.patternPie.map((_, idx) => (
                        <Cell key={idx} fill={colors[idx % colors.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              )}
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );

  // ✅ mode 분기
  if (mode === "hourly") return HourlyCard;
  if (mode === "distribution") return DistributionCard; // 혹시 나중에 쓸까봐
  return (
    <div className="noise2-charts-grid">
      {HourlyCard}
      {DistributionCard}
    </div>
  );
}
