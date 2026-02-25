import { useMemo, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../../components/ui/card";
import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip } from "recharts";

export default function NoiseDistributions({ loading, sensorPie, patternPie, colors }) {
  const [tab, setTab] = useState("sensor"); // "sensor" | "pattern"

  const data = useMemo(() => {
    return tab === "sensor" ? sensorPie : patternPie;
  }, [tab, sensorPie, patternPie]);

  return (
    <Card className="noise2-card">
      <CardHeader className="noise2-cardhead">
        <div className="noise2-cardheadrow">
          <CardTitle className="noise2-cardtitle">분포</CardTitle>

          {/* ✅ pillbar */}
          <div className="noise2-pillbar">
            <button
              className={`noise2-pill ${tab === "sensor" ? "active" : ""}`}
              onClick={() => setTab("sensor")}
              type="button"
            >
              센서
            </button>
            <button
              className={`noise2-pill ${tab === "pattern" ? "active" : ""}`}
              onClick={() => setTab("pattern")}
              type="button"
            >
              패턴
            </button>
          </div>
        </div>
      </CardHeader>

      <CardContent>
        {loading ? (
          <div className="noise2-empty">로딩 중...</div>
        ) : !data?.length ? (
          <div className="noise2-empty">데이터가 없습니다.</div>
        ) : (
          <div style={{ width: "100%", height: 220 }}>
            <ResponsiveContainer>
              <PieChart>
                <Pie
                  data={data}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                >
                  {data.map((_, idx) => (
                    <Cell key={idx} fill={colors[idx % colors.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
