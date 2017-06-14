#include <Bridge.h>
#include <Console.h>
#include <HttpClient.h>
#include <Mailbox.h>
#include <Process.h>
#include <YunClient.h>
#include <YunServer.h>
#include <SPI.h>
#include <SoftwareSerial.h>

byte mac[] = {0x90,0xA2,0xDA,0xF9,0x07,0xC8};
YunClient client;
SoftwareSerial mySerial(10, 11);
char frame[200];
int8_t counter, answer;
long previous;
String parametri;
String imei;
String gps_str;
String UTCdate;
String latitude;
String logitude;
int com_1;
int com_2;
int com_3;
int com_4;
int com_5;
bool IS_GET_IMEI=false;
int good_led_pin = 9;
int test_led_pin = 8;
int error_led_pin = 7;

void setup() {
  pinMode(good_led_pin, OUTPUT);
  pinMode(test_led_pin, OUTPUT);
  pinMode(error_led_pin, OUTPUT);
  
  while (!Serial) {;}
  Bridge.begin();
  Serial.begin(9600);
  mySerial.begin(9600);
  mySerial.println("AT");
  mySerial.println("AT+CGNSPWR=1");
  Serial.println("setup");
}

void loop() {
  if(getimei()){   
    if(getGpsInfo()){    
    Serial.println("imei : "+imei);
    Serial.println("UTCdate : " + UTCdate);
    Serial.println("latitude : " + latitude);
    Serial.println("logitude : " + logitude);
    IS_GET_IMEI = true;
    parametri ="";
    delay(2500);
    Serial.println("connecting...");
    if (client.connect("le1779.bixone.com", 80)){
      Serial.println("connected");
      delay(2500);
      parametri="arduino_imei="+ String(imei)
      + "&arduino_latitude=" + String(latitude)
      + "&arduino_longitude=" + String(logitude)
      + "&arduino_date=" + String(UTCdate);
    
      client.println("POST /arduino_connect.php HTTP/1.1");
      client.print("Content-length:");
      client.println(parametri.length());
      Serial.println(parametri.length());
      Serial.println(parametri);
      client.println("Connection: Close");
      client.println("Host:le1779.bixone.com");
      client.println("Content-Type: application/x-www-form-urlencoded");
      client.println();
      client.println(parametri);
      
      digitalWrite(test_led_pin, LOW);
      digitalWrite(error_led_pin, LOW);
      digitalWrite(good_led_pin, HIGH);
      delay(100);
      digitalWrite(good_led_pin, LOW);
      delay(100);
      digitalWrite(good_led_pin, HIGH);
    } else{
      digitalWrite(good_led_pin, LOW);
      digitalWrite(test_led_pin, LOW);
      digitalWrite(error_led_pin, HIGH);
      delay(100);
      digitalWrite(error_led_pin, LOW);
      delay(100);
      digitalWrite(error_led_pin, HIGH);
      Serial.println("connection failed");
    }
    if (client.available()) {
      char c = client.read();
      Serial.print(c);  
    }
    if (!client.connected()) {
      Serial.println();
      Serial.println("disconnecting.");
      client.stop();      
    }
    delay(60000); 
    }else{
      Serial.println("gps info no get");
      Serial.println("imei : "+imei);
      Serial.println("UTCdate : " + UTCdate);
      Serial.println("latitude : " + latitude);
      Serial.println("logitude : " + logitude);
      digitalWrite(good_led_pin, LOW);    
      digitalWrite(error_led_pin, LOW);
      digitalWrite(test_led_pin, HIGH);
      delay(100);
      digitalWrite(test_led_pin, LOW);
      delay(100);
      digitalWrite(test_led_pin, HIGH);
      delay(10000);
    }
  } else{
    Serial.println("imei no get");
    Serial.println("imei : "+imei);
    Serial.println("UTCdate : " + UTCdate);
    Serial.println("latitude : " + latitude);
    Serial.println("logitude : " + logitude);
    IS_GET_IMEI = false;
    digitalWrite(good_led_pin, LOW);    
    digitalWrite(error_led_pin, LOW);
    digitalWrite(test_led_pin, HIGH);
    delay(100);
    digitalWrite(test_led_pin, LOW);
    delay(100);
    digitalWrite(test_led_pin, HIGH);
    delay(10000);
  }  
}
bool getGpsInfo(){
  while ( mySerial.available() > 0) mySerial.read();
  mySerial.println("AT+CGNSINF");
  counter = 0;
  answer = 0;
  memset(frame, '\0', sizeof(frame));
  previous = millis();
  do {
    if (mySerial.available() != 0) {
      frame[counter] = mySerial.read();
      counter++;
      if (strstr(frame, "OK") != NULL){
        answer = 1;
      }
    }
  }
  while ((answer == 0) && ((millis() - previous) < 2000));
  frame[counter - 3] = '\0';
  gps_str = frame;
  com_1 = gps_str.indexOf(',');
  com_2 = gps_str.indexOf(',', com_1+1);
  com_3 = gps_str.indexOf(',', com_2+1);
  com_4 = gps_str.indexOf(',', com_3+1);
  com_5 = gps_str.indexOf(',', com_4+1);
  UTCdate = gps_str.substring(com_2+1, com_3);
  latitude = gps_str.substring(com_3+1, com_4);
  logitude = gps_str.substring(com_4+1, com_5);
  if(UTCdate=="" || latitude=="" || logitude==""){
    return false;
  }else {
    return true;  
  }
}
bool getimei(){
  while ( mySerial.available() > 0) mySerial.read();
  mySerial.println("AT+GSN");
  counter = 0;
  answer = 0;
  memset(frame, '\0', sizeof(frame));
  previous = millis();
  do {
    if (mySerial.available() != 0) {
      frame[counter] = mySerial.read();
      counter++;
      if (strstr(frame, "OK") != NULL){
        answer = 1;
      }
    }
  }while ((answer == 0) && ((millis() - previous) < 2000));
  frame[counter-3] = '\0';//去除OK
  int c_to_i;
  char i_to_c;
  imei="";
  for(int i=0; i<counter-3; i++){
    c_to_i=frame[i];
    if(c_to_i>=48 && c_to_i<=57){
      i_to_c=c_to_i;
      imei+=i_to_c;      
    }
   }
   int imei_len = imei.length();
   if(imei_len==15)
      return true;
   else
      return false;
}
//TX 10 RX 11 VBA 5V VIO 3V
