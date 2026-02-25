#include <Wire.h>

// ===== Relay Pins (Active LOW) =====
const int PIN_RELAY_FAN    = 8;  // 4채널 IN1
const int PIN_RELAY_RES    = 9;  // 4채널 IN2
const int PIN_RELAY_HEATER = 7;  // 1채널

void relayOn(int pin)  { digitalWrite(pin, LOW); }
void relayOff(int pin) { digitalWrite(pin, HIGH); }

// ===== LED Pins =====
const int PIN_LED_FAN    = 4;
const int PIN_LED_RES    = 5;
const int PIN_LED_HEATER = 6;

// ===== INA219 (address 0x40) =====
const uint8_t INA219_ADDR = 0x40;
const uint8_t REG_CONFIG  = 0x00;
const uint8_t REG_BUS_V   = 0x02;
const uint8_t REG_CURRENT = 0x04;
const uint8_t REG_POWER   = 0x03;
const uint8_t REG_CALIB   = 0x05;

void writeReg16(uint8_t reg, uint16_t value) {
  Wire.beginTransmission(INA219_ADDR);
  Wire.write(reg);
  Wire.write(value >> 8);
  Wire.write(value & 0xFF);
  Wire.endTransmission();
}

int16_t readReg16(uint8_t reg) {
  Wire.beginTransmission(INA219_ADDR);
  Wire.write(reg);
  Wire.endTransmission(false);
  Wire.requestFrom(INA219_ADDR, 2);
  return (Wire.read() << 8) | Wire.read();
}

void ina219Init() {
  writeReg16(REG_CONFIG, 0x019F);
  writeReg16(REG_CALIB, 10240); // R100 기준 대표값
}

void sendAck(long deviceId, bool operate) {
  Serial.print("{\"type\":\"ack\",");
  Serial.print("\"deviceId\":"); Serial.print(deviceId); Serial.print(",");
  Serial.print("\"operate\":"); Serial.print(operate ? "true" : "false");
  Serial.println("}");
}

// ===== States =====
bool fanState = false;
bool resState = false;
bool heaterState = false;

int activeMeasureDeviceId = 2; // 2=FAN, 4=RES
double energyKwh = 0.0;
unsigned long lastMs = 0;

void setup() {
  Serial.begin(115200);

  // relay
  pinMode(PIN_RELAY_FAN, OUTPUT);
  pinMode(PIN_RELAY_RES, OUTPUT);
  pinMode(PIN_RELAY_HEATER, OUTPUT);

  relayOff(PIN_RELAY_FAN);
  relayOff(PIN_RELAY_RES);
  relayOff(PIN_RELAY_HEATER);

  // leds
  pinMode(PIN_LED_FAN, OUTPUT);
  pinMode(PIN_LED_RES, OUTPUT);
  pinMode(PIN_LED_HEATER, OUTPUT);

  digitalWrite(PIN_LED_FAN, LOW);
  digitalWrite(PIN_LED_RES, LOW);
  digitalWrite(PIN_LED_HEATER, LOW);

  // i2c
  Wire.begin();
  ina219Init();

  lastMs = millis();
}

void loop() {
  // ===== command receive =====
  if (Serial.available()) {
    String cmd = Serial.readStringUntil('\n');
    cmd.trim();

    if (cmd == "FAN_ON") {
      fanState = true;
      resState = false;          // FAN/RES 동시ON 방지
      activeMeasureDeviceId = 2; // 측정 대상 FAN
      sendAck(2, true);
    } else if (cmd == "FAN_OFF") {
      fanState = false;
      sendAck(2, true);

    } else if (cmd == "RES_ON") {
      resState = true;
      fanState = false;          // FAN/RES 동시ON 방지
      activeMeasureDeviceId = 4; // 측정 대상 RES
      sendAck(2, true);
    } else if (cmd == "RES_OFF") {
      resState = false;
      sendAck(2, true);
    } else if (cmd == "HEATER_ON") {
      heaterState = true;
      sendAck(2, true);
    } else if (cmd == "HEATER_OFF") {
      heaterState = false;
      sendAck(2, true);
    }
  }

  // ===== relay apply =====
  if (fanState) relayOn(PIN_RELAY_FAN); else relayOff(PIN_RELAY_FAN);
  if (resState) relayOn(PIN_RELAY_RES); else relayOff(PIN_RELAY_RES);
  if (heaterState) relayOn(PIN_RELAY_HEATER); else relayOff(PIN_RELAY_HEATER);

  // ===== LED apply =====
  digitalWrite(PIN_LED_FAN, fanState ? HIGH : LOW);
  digitalWrite(PIN_LED_RES, resState ? HIGH : LOW);
  digitalWrite(PIN_LED_HEATER, heaterState ? HIGH : LOW);

  // ===== INA219 read =====
  float V=0, A=0, W=0;

  uint16_t rawBus = (uint16_t)readReg16(REG_BUS_V);
  V = (rawBus >> 3) * 0.004f;

  int16_t rawCur = readReg16(REG_CURRENT);
  A = rawCur * 0.0001f;

  int16_t rawPow = readReg16(REG_POWER);
  W = rawPow * 0.002f;

  // ===== kWh integrate =====
  unsigned long now = millis();
  double dt_h = (now - lastMs) / 3600000.0;
  lastMs = now;

  if ((fanState || resState) && W > 0 && dt_h > 0) {
    energyKwh += (W * dt_h) / 1000.0;
  }

  // ===== JSON output =====
  Serial.print("{");
  Serial.print("\"fan\":"); Serial.print(fanState ? "true" : "false"); Serial.print(",");
  Serial.print("\"res\":"); Serial.print(resState ? "true" : "false"); Serial.print(",");
  Serial.print("\"heater\":"); Serial.print(heaterState ? "true" : "false"); Serial.print(",");
  Serial.print("\"measureDeviceId\":"); Serial.print(activeMeasureDeviceId); Serial.print(",");
  Serial.print("\"voltage\":"); Serial.print(V,3); Serial.print(",");
  Serial.print("\"current\":"); Serial.print(A,4); Serial.print(",");
  Serial.print("\"power\":"); Serial.print(W,3); Serial.print(",");
  Serial.print("\"energyKwh\":"); Serial.print(energyKwh, 6);
  Serial.println("}");

  delay(1000);
}