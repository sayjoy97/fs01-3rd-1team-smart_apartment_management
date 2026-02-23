// src/mocks/noiseMocks.js
export function generateMockNoiseEvents() {
  const primaryTypes = ["충격성 소음", "지속적/반복적 진동", "음향 강도 변화", "불명확"];
  const secondaryTypes = [
    "발걸음 추정",
    "가구 이동 추정",
    "단발성 충격음 추정",
    "뛰는 소리 추정",
    "미분류",
  ];
  const sensors = ["충격·진동 감지", "반복적 진동 발생", "음향 강도 변화"];
  const buildings = ["101동", "102동", "103동", "104동"];
  const statuses = ["대기", "승인", "보류", "관찰 중"];

  const events = [];

  for (let i = 1; i <= 47; i++) {
    const hour = Math.floor(Math.random() * 24);
    const isNight = hour >= 22 || hour < 6;
    const intensity = 40 + Math.random() * 50;
    const hasViolation = intensity > 70 && (isNight ? Math.random() > 0.3 : Math.random() > 0.6);
    const status = hasViolation ? statuses[Math.floor(Math.random() * statuses.length)] : "정상";

    const floor = Math.floor(Math.random() * 15) + 1;
    const unit = `${Math.floor(Math.random() * 4) + 1}0${Math.floor(Math.random() * 4) + 1}`;

    events.push({
      id: i,
      time: `2026-01-23 ${String(hour).padStart(2, "0")}:${String(Math.floor(Math.random() * 60)).padStart(2, "0")}`,
      building: buildings[Math.floor(Math.random() * buildings.length)],
      floor,
      unit,
      sensor: sensors[Math.floor(Math.random() * sensors.length)],
      primaryType: primaryTypes[Math.floor(Math.random() * primaryTypes.length)],
      secondaryType: secondaryTypes[Math.floor(Math.random() * secondaryTypes.length)],
      intensity: Math.round(intensity),
      isDayTime: !isNight,
      hasViolation,
      status,
      repeatCount: hasViolation ? Math.floor(Math.random() * 8) + 3 : Math.floor(Math.random() * 3),
      isRecurring: status === "관찰 중" && Math.random() > 0.7,
      isHabitual: hasViolation && Math.random() > 0.85,
    });
  }

  return events.sort((a, b) => new Date(b.time) - new Date(a.time));
}
