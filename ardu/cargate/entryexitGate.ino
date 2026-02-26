#include <WiFiS3.h>
#include <Servo.h>
#include <PubSubClient.h>
#include <Wire.h> 
#include <LiquidCrystal_I2C.h>

/* =================== 설정 =================== */
const char* ssid = "WIFI_ID(2.4GHz)";
const char* password = "WIFI_PASSWORD";
const char* mqtt_server = "BROKER_URL";
const int mqtt_port = 1884;

// 구독 토픽
const char* SUB_TOPIC_ENTRY = "jjld/cargate/entry/gate_command";
const char* SUB_TOPIC_EXIT  = "jjld/cargate/exit/gate_command";

// 발행 토픽
const char* PUB_TOPIC_ENTRY = "jjld/cargate/entry/gate_sub";
const char* PUB_TOPIC_EXIT  = "jjld/cargate/exit/gate_sub";

/* =================== 객체 및 핀 할당 =================== */
WiFiClient wifiClient;
PubSubClient client(wifiClient);
LiquidCrystal_I2C lcd(0x27, 16, 2); 

// [입차용 하드웨어 핀]
Servo entryServo;
#define ENTRY_TRIG  6
#define ENTRY_ECHO  5
#define ENTRY_SERVO 4
#define ENTRY_IR    3

// [출차용 하드웨어 핀]
Servo exitServo;
#define EXIT_TRIG   8
#define EXIT_ECHO   7
#define EXIT_SERVO  9
#define EXIT_IR     2

/* =================== 상태변수 =================== */
// 입차 상태 변수
bool entryGateDetected = false;
bool entryGateOpened   = false;
bool entryCarDetected  = false;
bool entryWaitingCommand = false;

// 출차 상태 변수
bool exitGateDetected  = false;
bool exitGateOpened   = false;
bool exitCarDetected   = false;
bool exitWaitingCommand = false;

/* ===================== LCD 유틸 ===================== */
void updateLCD(String line1, String line2) {
  lcd.clear();
  lcd.setCursor(0, 0); lcd.print(line1);
  lcd.setCursor(0, 1); lcd.print(line2);
}

void resetLCD() {
  updateLCD("  PARKING SYSTEM ", "  READY TO WORK  ");
}

/* ===================== 거리 측정 함수 ===================== */
double getDistance(int trig, int echo) {
  digitalWrite(trig, LOW);
  delayMicroseconds(2);
  digitalWrite(trig, HIGH);
  delayMicroseconds(10);
  digitalWrite(trig, LOW);

  long duration = pulseIn(echo, HIGH, 30000);
  if (duration == 0) return 999.0;

  return duration * 0.034 / 2;
}

/* ===================== 센서 체크 로직 ===================== */
void checkSensors() {
  // --- 입차 초음파 센서 체크 ---
  if (!entryWaitingCommand) {
    double distEntry = getDistance(ENTRY_TRIG, ENTRY_ECHO);
    if (distEntry < 15.0 && !entryGateDetected) {
      entryGateDetected = true;
      entryWaitingCommand = true;
      
      Serial.println("[입차] 물체 탐지 -> 서버에 확인 요청");
      updateLCD("  IDENTIFYING.. ", "  PLEASE WAIT   ");
      
      client.publish(PUB_TOPIC_ENTRY, "detect");
    }
    if (distEntry >= 20.0) {
      entryGateDetected = false;
    }
  }

  // --- 출차 초음파 센서 체크 ---
  if (!exitWaitingCommand) {
    double distExit = getDistance(EXIT_TRIG, EXIT_ECHO);
    if (distExit < 15.0 && !exitGateDetected) {
      exitGateDetected = true;
      exitWaitingCommand = true;
      
      Serial.println("[출차] 물체 탐지 -> 서버에 확인 요청");
      updateLCD("  IDENTIFYING.. ", "  PLEASE WAIT   ");
      
      client.publish(PUB_TOPIC_EXIT, "detect");
    }
    if (distExit >= 20.0) {
      exitGateDetected = false;
    }
  }

  // --- 입차 IR 센서 체크 (통과 감지) ---
  if (entryGateOpened) {
    int irValEntry = digitalRead(ENTRY_IR);
    if (irValEntry == LOW && !entryCarDetected) {
      entryCarDetected = true;
      Serial.println("[입차] 차량 통과 시작...");
    }
    if (irValEntry == HIGH && entryCarDetected) {
      Serial.println("[입차] 통과 완료 -> 게이트 닫힘");
      delay(1000);
      entryServo.write(90);
      
      entryGateOpened = false;
      entryCarDetected = false;
      entryWaitingCommand = false;
      resetLCD();
    }
  }

  // --- 출차 IR 센서 체크 (통과 감지) ---
  if (exitGateOpened) {
    int irValExit = digitalRead(EXIT_IR);
    if (irValExit == LOW && !exitCarDetected) {
      exitCarDetected = true;
      Serial.println("[출차] 차량 통과 시작...");
    }
    if (irValExit == HIGH && exitCarDetected) {
      Serial.println("[출차] 통과 완료 -> 게이트 닫힘");
      delay(1000);
      exitServo.write(90);
      
      exitGateOpened = false;
      exitCarDetected = false;
      exitWaitingCommand = false;
      resetLCD();
    }
  }
}

/* ===================== MQTT 콜백 ===================== */
void callback(char* topic, byte* payload, unsigned int length) {
  char messageArr[length + 1];
  memcpy(messageArr, payload, length);
  messageArr[length] = '\0';
  String msg = String(messageArr);
  String currentTopic = String(topic);

  Serial.print("MQTT 수신 ["); 
  Serial.print(currentTopic); 
  Serial.print("]: ");
  Serial.println(msg);

  // 1. 인식 실패 및 중복 처리
  if (msg == "reload") {
    Serial.println("인식 실패 - 재시도 대기");
    updateLCD("ACCESS DENIED", "TRY AGAIN"); 
    if (currentTopic.indexOf("entry") != -1) {
      entryWaitingCommand = false;
      entryGateDetected = false;
    } else {
      exitWaitingCommand = false;
      exitGateDetected = false;
    }
    return;
  } 
  
  if (msg == "duplicate") {
    Serial.println("중복 입차 오류 발생");
    updateLCD("CAR DUPLICATE", "ALREADY IN"); 
    entryWaitingCommand = false;
    entryGateDetected = false;
    return;
  }

  // 2. 출차 정산 프로세스 (request_payment)
  if (msg.startsWith("request_payment")) {
    int idx1 = msg.indexOf('_');
    int idx2 = msg.indexOf('_', idx1 + 1);
    int idx3 = msg.indexOf('_', idx2 + 1);
    int idx4 = msg.indexOf('_', idx3 + 1);
    int idx5 = msg.indexOf('_', idx4 + 1);

    String plateNum = msg.substring(idx2 + 1, idx3);
    String stayTime = msg.substring(idx4 + 1, idx5);
    String feeAndDate = msg.substring(idx5 + 1); 
    
    int lastIdx = feeAndDate.indexOf('_');
    String feeAmount = feeAndDate.substring(0, lastIdx);
    String serverTime = feeAndDate.substring(lastIdx + 1);

    Serial.println("[정산 대기] 차량: " + plateNum + ", 시간: " + stayTime + "분, 요금: " + feeAmount);
    updateLCD("STAY: " + stayTime + " MIN", "FEE: " + feeAmount + " WON");
    
    delay(5000); // 5초 자동 정산 시뮬레이션

    Serial.println("[정산 완료] 결제 데이터 서버 전송");
    updateLCD("PAYMENT SUCCESS", "SAFE DRIVE!");
    
    String pubMsg = plateNum + "_" + serverTime + "_" + feeAmount;
    client.publish("jjld/cargate/payment", pubMsg.c_str());

    exitServo.write(0);
    exitGateOpened = true;
  }

  // 3. 일반적인 오픈 명령 처리 (open_)
  else if (msg.startsWith("open_")) {
    if (currentTopic.indexOf("entry") != -1) {
      Serial.println("입차 게이트 오픈 실행");
      entryServo.write(0);
      entryGateOpened = true;
      entryWaitingCommand = false;

      int first = msg.indexOf('_');
      int second = msg.indexOf('_', first + 1);
      if (second != -1) {
        String vehicleType = msg.substring(second + 1);
        updateLCD("    SUCCESS!    ", vehicleType);
      } else {
        updateLCD("    SUCCESS!    ", "   GATE OPEN    ");
      }
    } else {
      Serial.println("출차 게이트 오픈 실행");
      exitServo.write(0);
      exitGateOpened = true;
      exitWaitingCommand = false;
      updateLCD("    SUCCESS!    ", "   GATE OPEN    ");
    }
  }
}

/* ===================== 네트워크 설정 ===================== */
void setup_wifi() {
  Serial.print("WiFi 연결 중: ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi 연결 성공");
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("MQTT 연결 시도...");
    if (client.connect("GATE-TOTAL-NODE")) {
      Serial.println("연결 성공");
      client.subscribe(SUB_TOPIC_ENTRY);
      client.subscribe(SUB_TOPIC_EXIT);
    } else {
      Serial.print("실패, rc=");
      Serial.print(client.state());
      Serial.println(" 2초 후 재시도");
      delay(2000);
    }
  }
}

/* ===================== 초기 설정 및 루프 ===================== */
void setup() {
  Serial.begin(9600);
  
  lcd.init();
  lcd.backlight();
  resetLCD();

  // 입차 핀 모드 설정
  pinMode(ENTRY_TRIG, OUTPUT);
  pinMode(ENTRY_ECHO, INPUT);
  pinMode(ENTRY_IR, INPUT_PULLUP);
  entryServo.attach(ENTRY_SERVO);
  entryServo.write(90);

  // 출차 핀 모드 설정
  pinMode(EXIT_TRIG, OUTPUT);
  pinMode(EXIT_ECHO, INPUT);
  pinMode(EXIT_IR, INPUT_PULLUP);
  exitServo.attach(EXIT_SERVO);
  exitServo.write(90);

  setup_wifi();
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) reconnect();
  client.loop();

  checkSensors();
}