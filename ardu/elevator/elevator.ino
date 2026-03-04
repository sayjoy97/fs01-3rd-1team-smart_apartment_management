#include <IRremote.hpp>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

#define IR_RECEIVE_PIN 8
LiquidCrystal_I2C lcd(0x27, 16, 2);
String lastFloor = "", lastDir = "", lastDoor = "";
String systemStatus = "IDLE";

// 외부 호출 버튼 설정 (총 6개)
// 0:1U, 1:2U, 2:3U, 3:2D, 4:3D, 5:4D
int callBtnPins[6] = {2, 3, 4, 5, 6, 7};
bool btnActiveStatus[6] = {false, false, false, false, false, false}; 
bool lastBtnState[6] = {HIGH, HIGH, HIGH, HIGH, HIGH, HIGH};

// IR (내부 버튼용) 1~4층
unsigned long irFloorValues[4] = {4077715200, 3877175040, 2707357440, 4144561920};
bool floorActiveStatus[4] = {false, false, false, false};
#define IR_OPEN  3927310080
#define IR_CLOSE 4161273600

void selectTCA(uint8_t bus) {
  if (bus > 7) return;
  Wire.beginTransmission(0x70);
  Wire.write(1 << bus);
  Wire.endTransmission();
  delay(1);
}

void setup() {
  Serial.begin(9600); 
  Wire.begin();

  // LCD 0~4 (0:내부, 1~4:각 층 외부)
  for (int i = 0; i <= 4; i++) {
    selectTCA(i);
    delay(50);
    lcd.init();
    lcd.backlight();
    lcd.clear();
    if(i==0) lcd.print("INT_LCD READY");
    else { lcd.print("EXT_LCD "); lcd.print(i); lcd.print("F"); }
  }

  IrReceiver.begin(IR_RECEIVE_PIN, ENABLE_LED_FEEDBACK);
  for(int i=0; i<6; i++) pinMode(callBtnPins[i], INPUT_PULLUP);

  delay(1000);
  Serial.println("ARDUINO_READY");
}

void loop() {
  handleSerial();
  handleExternalButtons();
  handleIRRemote();
}

void handleSerial() {
  if (Serial.available() > 0) {
    String data = Serial.readStringUntil('\n');
    data.trim();

    // 1. RESET 신호 처리
    if (data.startsWith("RESET:")) {
      char type = data.charAt(6); // 'C' or 'F'
      int index = data.substring(8).toInt();
      if (type == 'A') {
        for (int i = 0; i < 6; i++) {
          btnActiveStatus[i] = false;
          lastBtnState[i] = digitalRead(callBtnPins[i]);
        }
        for (int i = 0; i < 4; i++) {
          floorActiveStatus[i] = false;
        }
      }
      if (type == 'C' && index < 6) btnActiveStatus[index] = false;
      if (type == 'F' && index >= 1 && index <= 4) floorActiveStatus[index-1] = false;
      return; 
    }

    // 2. 시스템 상태 제어 (S:ERROR:0 또는 S:REPAIR:0)
    if (data.startsWith("S:")) {
      int firstColon = data.indexOf(':');
      int secondColon = data.indexOf(':', firstColon + 1);
      String statusMsg = data.substring(firstColon + 1, secondColon); // "ERROR" 또는 "REPAIR"
      
      systemStatus = statusMsg;

      for (int i = 0; i <= 4; i++) {
        selectTCA(i);
        delay(10);
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("SYSTEM STATUS:");
        lcd.setCursor(0, 1);
        lcd.print(statusMsg);
      }
      return; // 상태 표시 후 종료
    }

    // 3. 일반 LCD 업데이트 (포맷: "1:STAY:CLOSE")
    int firstColon = data.indexOf(':');
    int secondColon = data.indexOf(':', firstColon + 1);

    if (systemStatus == "IDLE" && firstColon != -1 && secondColon != -1) {
      String floor = data.substring(0, firstColon);
      String dir = data.substring(firstColon + 1, secondColon);
      String door = data.substring(secondColon + 1);

      // 데이터가 실제 변했을 때만 LCD 갱신
      if (floor != lastFloor || dir != lastDir || door != lastDoor) {
        updateAllLCDs(floor, dir, door);
        lastFloor = floor; 
        lastDir = dir;
        lastDoor = door;
      }
    }
  }
}

void updateAllLCDs(String floor, String dir, String door) {
  for (int i = 0; i <= 4; i++) {
    selectTCA(i);
    delay(10);
    lcd.backlight();
    
    if (i == 0) { // 내부 LCD
      lcd.clear();
      lcd.setCursor(0, 0); lcd.print("FL:" + floor + " [" + dir + "]");
      lcd.setCursor(0, 1); lcd.print("DOOR: " + door);
    } 
    else { // 외부 LCD (1~4층)
      lcd.clear();
      lcd.setCursor(2, 0); lcd.print("FLOOR " + floor);
      lcd.setCursor(4, 1); lcd.print("[" + dir + "]");
    }
  }
}

void handleExternalButtons() {
  if (systemStatus != "IDLE") return;
  for (int i=0; i<6; i++) {
    bool currentState = digitalRead(callBtnPins[i]);
    if (lastBtnState[i] == HIGH && currentState == LOW) {
      btnActiveStatus[i] = !btnActiveStatus[i];
      
      // 라즈베리파이로 전송 (C:버튼인덱스:상태)
      // 파이썬 쪽에서 인덱스 0~2는 상행, 3~5는 하행으로 판단
      Serial.print("C:"); Serial.print(i); Serial.print(":");
      Serial.println(btnActiveStatus[i] ? "1" : "0");
      
      delay(200); // 디바운스
    }
    lastBtnState[i] = currentState;
  }
}

void handleIRRemote() {
  if (systemStatus != "IDLE") return;
  if (IrReceiver.decode()) {
    unsigned long value = IrReceiver.decodedIRData.decodedRawData;
    if (value != 0) {
      for (int i=0; i<4; i++) {
        if (value == irFloorValues[i]) {
          floorActiveStatus[i] = !floorActiveStatus[i];
          // 내부 버튼은 F:층수:상태 로 전송
          Serial.print("F:"); 
          Serial.print(i + 1); 
          Serial.print(":");
          Serial.println(floorActiveStatus[i] ? "1" : "0");
          break;
        }
      }
      if (value == IR_OPEN) Serial.println("D:OPEN");
      if (value == IR_CLOSE) Serial.println("D:CLOSE");
    }
    IrReceiver.resume();
  }
}