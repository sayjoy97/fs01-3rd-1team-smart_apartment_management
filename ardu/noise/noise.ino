// ardu/noise/noise.ino

const int PIN_MAX9814 = A0; // sensor_id=1
const int PIN_PIEZO   = A1; // sensor_id=2
const int PIN_SW420   = 2;  // sensor_id=3 (D2)

const long SENSOR_MAX9814_ID = 1;
const long SENSOR_PIEZO_ID   = 2;
const long SENSOR_SW420_ID   = 3;

// ===== 튜닝 파라미터 (시연용) =====
int TH_MAX9814 = 15;     // 음성/박수 감지 임계(peak2peak)
int TH_PIEZO   = 130;    // 충격 감지 임계(peak)
unsigned long COOLDOWN_MAX = 1200;  // ms
unsigned long COOLDOWN_PZ  = 1500;  // ms
unsigned long COOLDOWN_SW  = 2000;  // ms

unsigned long lastMaxMs = 0;
unsigned long lastPzMs  = 0;
unsigned long lastSwMs  = 0;

// 0~100으로 스케일
int clamp100(int v){
  if (v < 0) return 0;
  if (v > 100) return 100;
  return v;
}

// 이벤트 JSON 한 줄 출력
void emitEvent(long sensorId, int soundLevel, const char* src, int raw){
  Serial.print("{\"type\":\"noise\",");
  Serial.print("\"sensorId\":"); Serial.print(sensorId); Serial.print(",");
  Serial.print("\"soundLevel\":"); Serial.print(soundLevel); Serial.print(",");
  Serial.print("\"src\":\""); Serial.print(src); Serial.print("\",");
  Serial.print("\"raw\":"); Serial.print(raw);
  Serial.println("}");
}

void setup() {
  Serial.begin(115200);
  pinMode(PIN_SW420, INPUT);

  // 아날로그 기준 잡기 안정화 (초반 튐 방지)
  delay(300);
  for (int i=0;i<50;i++){ analogRead(PIN_MAX9814); analogRead(PIN_PIEZO); delay(5); }
}

void loop() {
  unsigned long now = millis();

  // ===== 1) MAX9814 (음성/박수) =====
  // 50ms 동안 min/max를 잡아서 peak-to-peak 계산
  int mn = 1023, mx = 0;
  unsigned long t0 = millis();
  while (millis() - t0 < 50) {
    int v = analogRead(PIN_MAX9814);
    if (v < mn) mn = v;
    if (v > mx) mx = v;
  }
  int p2p = mx - mn;               // 파형 진폭(대략적인 크기)
  int levelMax = map(p2p, 3, 70, 10, 90); 
  levelMax = clamp100(levelMax);

  if (p2p >= TH_MAX9814 && (now - lastMaxMs) > COOLDOWN_MAX) {
    lastMaxMs = now;
    emitEvent(SENSOR_MAX9814_ID, levelMax, "MAX9814", p2p);
  }

  // ===== 2) PIEZO (충격- 발쿵) =====
  int pz = analogRead(PIN_PIEZO);
  // piezo는 순간 피크가 중요해서 한 번 더 샘플링해서 max 잡음
  int pz2 = analogRead(PIN_PIEZO);
  int pzPeak = (pz > pz2) ? pz : pz2;
  int levelPz = map(pzPeak, 130, 430, 20, 100);
  levelPz = clamp100(levelPz);

  if (pzPeak >= TH_PIEZO && (now - lastPzMs) > COOLDOWN_PZ) {
    lastPzMs = now;
    emitEvent(SENSOR_PIEZO_ID, levelPz, "PIEZO", pzPeak);
  }

  // ===== 3) SW-420 (진동 스위치) =====
  int sw = digitalRead(PIN_SW420);
  if (sw == HIGH && (now - lastSwMs) > COOLDOWN_SW) {
    lastSwMs = now;
    // SW-420은 강도를 못 재니까 “강한 진동 이벤트”로 90 고정
    emitEvent(SENSOR_SW420_ID, 85, "SW420", 1);
  }

  delay(20);
}