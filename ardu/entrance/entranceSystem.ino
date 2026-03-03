#include <LiquidCrystal_I2C.h>
#include <WiFiS3.h>
#include <PubSubClient.h>
#include <MFRC522.h>

// ---------------- 키패드 설정 ----------------
byte rowPins[4] = {9, 8, 7, 6};
byte colPins[4] = {5, 4, 3, 2};
char keymap[4][4] = {
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};
bool lastState[4][4];  
String inputText = "";

// ---------------- 핀 설정 ----------------
int motorIN1 = A2;
int motorIN2 = A3;
int motorPWM = A4;
int buzzerPin = 10;
int ledPin = A5;

#define SS_PIN   A0
#define RST_PIN  A1
MFRC522 mfrc522(SS_PIN, RST_PIN);

bool doorIsOpen = false;

// ---------------- WiFi & MQTT 설정 ----------------
const char* ssid = "iGrowth";
const char* password = "new1234~!";
const char* mqtt_server = "192.168.14.99"; // 라즈베리파이 IP
const int mqtt_port = 1883;

WiFiClient wifiClient;
PubSubClient client(wifiClient);

// 토픽 설정
const char* sub_topic = "jjld/entrance/+/door/control";
const char* card_result_topic = "jjld/entrance/door/101/+/CARD_RESULT";
const char* pass_result_topic = "jjld/entrance/door/101/+/PASS_RESULT";

// ---------------- 하드웨어 제어 함수 ----------------
void motorControl(bool open){
  if(open){
    digitalWrite(motorIN1, LOW);
    digitalWrite(motorIN2, HIGH);
    analogWrite(motorPWM, 180); 
    Serial.println("ACTION:OPEN"); // 라즈베리파이 모니터링용
  } else {
    digitalWrite(motorIN1, HIGH);
    digitalWrite(motorIN2, LOW);
    analogWrite(motorPWM, 170);
    Serial.println("ACTION:CLOSE");
  }
}

void motorStop(){
  digitalWrite(motorPWM, LOW);
  digitalWrite(motorIN1, HIGH);
  digitalWrite(motorIN2, HIGH);
}

void buzz(int freq, int duration){
  tone(buzzerPin, freq, duration);
}

void ledOn(){ digitalWrite(ledPin, HIGH); }
void ledOff(){ digitalWrite(ledPin, LOW); }

// ---------------- 문 상태 서버 알림 ----------------
void notifyDoorOpened(int houseDong, int houseHo){
  String topic = "jjld/entrance/door/" + String(houseDong) + "/" + String(houseHo) + "/status";
  client.publish(topic.c_str(), "OPENED");
  doorIsOpen = true;
}

void notifyDoorClosed(int houseDong, int houseHo){
  String topic = "jjld/entrance/door/" + String(houseDong) + "/" + String(houseHo) + "/status";
  client.publish(topic.c_str(), "CLOSED");
  doorIsOpen = false;
}

// ---------------- 문 열기/닫기 시퀀스 ----------------
void openDoorSequence() {
  ledOn();
  motorControl(true);
  delay(2200);
  motorStop();
  notifyDoorOpened(101, 0);
}

void closeDoorSequence() {
  ledOff();
  motorControl(false);
  delay(1850);
  motorStop();
  notifyDoorClosed(101, 0);
}

// ---------------- MQTT 콜백 ----------------
void callback(char* topic, byte* payload, unsigned int length){
  char msg[50];
  if(length >= sizeof(msg)) length = sizeof(msg)-1;
  memcpy(msg, payload, length);
  msg[length] = '\0';
  String message(msg);
  String topicStr(topic);

  // 1. 원격 및 서버 결과 공통 처리
  if(message == "OK" || message == "OPEN") {
    if(!doorIsOpen) openDoorSequence();
  } 
  else if(message == "CLOSED") {
    if(doorIsOpen) closeDoorSequence();
  }
  else if(message == "FAIL") {
    buzz(500, 200);
  }
}

void reconnect(){
  while(!client.connected()){
    if(client.connect("UNO-R4-CLIENT")){
      client.subscribe(sub_topic);
      client.subscribe(card_result_topic);
      client.subscribe(pass_result_topic);
    } else {
      delay(2000);
    }
  }
}

// ---------------- 초기 설정 ----------------
void setup(){
  Serial.begin(9600);
  SPI.begin();      
  mfrc522.PCD_Init(); 

  for(int c=0; c<4; c++) pinMode(colPins[c], INPUT_PULLUP);
  for(int r=0; r<4; r++) pinMode(rowPins[r], INPUT_PULLUP);

  pinMode(motorPWM, OUTPUT);
  pinMode(motorIN1, OUTPUT);
  pinMode(motorIN2, OUTPUT);
  pinMode(buzzerPin, OUTPUT);
  pinMode(ledPin, OUTPUT);

  motorStop();
  ledOff();

  WiFi.begin(ssid, password);
  while(WiFi.status() != WL_CONNECTED){ delay(500); }

  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
}

// ---------------- 메인 루프 ----------------
void loop(){
  // 1. MQTT 연결 및 상태 유지
  if(!client.connected()) reconnect();
  client.loop();

  // 2. 라즈베리파이로부터 오는 제어 명령 (OPEN/CLOSE) 최우선 처리
  if(Serial.available() > 0) {
    String cmd = Serial.readStringUntil('\n');
    cmd.trim();
    
    if(cmd == "OPEN") {
      openDoorSequence();
    } 
    else if(cmd == "CLOSE") {
      closeDoorSequence();
    }
  }

  // 3. 센서 및 키패드 감지
  handleRFID();
  handleKeypad();
}

// ---------------- RFID 처리 (기존 XOR 로직 유지) ----------------
void handleRFID(){
  if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
      String uid = "";
      byte bcc = 0;
      for (byte i = 0; i < mfrc522.uid.size; i++) {
          if (i != 0) uid += "-";
          uid += String(mfrc522.uid.uidByte[i]);
          bcc ^= mfrc522.uid.uidByte[i]; // XOR 계산
      }
      uid += "-";
      uid += String(bcc);

      client.publish("jjld/entrance/101/card/request", uid.c_str());
      buzz(1000, 50);
      mfrc522.PICC_HaltA();
  }
}

// ---------------- 키패드 처리 (마스킹 기능 추가) ----------------
void handleKeypad(){
  for(int r=0; r<4; r++){
    pinMode(rowPins[r], OUTPUT);
    digitalWrite(rowPins[r], LOW);
    for(int c=0; c<4; c++){
      if(digitalRead(colPins[c]) == LOW && !lastState[r][c]){
        char key = keymap[r][c];
        lastState[r][c] = true;

        // 라즈베리파이 LCD 마스킹을 위해 시리얼 전송
        Serial.print("KEY:");
        Serial.println(key);
        
        processKey(key);
      }
      else if(digitalRead(colPins[c]) == HIGH && lastState[r][c]){
        lastState[r][c] = false;
      }
    }
    pinMode(rowPins[r], INPUT_PULLUP);
  }
}

void processKey(char key){
  // '*' 누르면 즉시 리셋
  if(key == '*'){
    inputText = "";
    Serial.println("KEY:RESET"); 
    buzz(500, 100);
    return;
  }

  inputText += key;
  buzz(1000, 50);

  // 4번째 자리 검증 로직 ('KEY:RESET'을 보내 파이 LCD를 초기화)
  if(inputText.length() == 4){
    if(inputText.charAt(3) != '#'){
      Serial.println("KEY:RESET"); // 파이에게 알림
      inputText = "";
      buzz(200, 700);
      return;
    }
  }

  // 8자리 완성 시 서버 전송
  if(inputText.length() == 8){
    if(validateInput()){
      // 서버에 전송만 하고, 문을 여는 건 서버 결과(MQTT)를 받고 나서 처리
      client.publish("jjld/entrance/101/pass/request", inputText.c_str());
      buzz(2000, 200); 
    } else {
      Serial.println("KEY:RESET");
      buzz(200, 700);
    }
    inputText = ""; 
  }
}

bool validateInput(){
  for(int i=0; i<3; i++){ if(!isDigit(inputText.charAt(i))) return false; }
  if(inputText.charAt(3) != '#') return false;
  for(int i=4; i<8; i++){ if(!isDigit(inputText.charAt(i))) return false; }
  return true; 
}