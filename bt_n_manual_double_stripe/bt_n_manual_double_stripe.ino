#include <FastLED.h>
#include <arduino-timer.h>
#include <BluetoothSerial.h>

// Non robust/standard variables.
int flag_pin = 22; // Switch (manual - BT) pin.
int pb_pin = 16; // Push button pin.
#define LED_PIN 5 // Leds stripe pin.
#define NUM_LEDS 150 // Leds stripe length.
#define LED_PIN_2 18 // Leds stripe pin.
#define NUM_LEDS_2 5 // Leds stripe length.

// Variables related to the Serial/BT communication.
#define COMMAND_LENGTH 100
char command[COMMAND_LENGTH];
const char delimiter[2] = ";";
char* token;
int sIt;
char c;
int numel;
int ref;
int tokens_counter = 0;
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif
BluetoothSerial SerialBT;
bool new_serial = false;

// Variables related to the timer interruptions.
volatile unsigned int t2_counter = 0;
bool state = false;
float desired_time_s = 1;
float overflow_time = 255/7812.5; // Resolution (8 bit => 255) / Frequence (set by TCCR2B register)
float threshold = desired_time_s / overflow_time;

// Variables to control the duration of the user's clicks.
uint32_t tLocalTimeMs = 0;  // Timestamp for each iteration [ms].
uint32_t tLastPush = 0; // Timestamp when the user starts clicking the pushbutton [ms].
uint32_t tFinalPressed = 0; // Timestamp when the user stops clicking the pushbutton [ms].
uint32_t tPressed = 0; // Time the pushbutton has been pressed [ms].
uint8_t clicks_num = 0; // Clicks' counter.
uint8_t pb_Pushed = false; // Flag that indicates the pushbutton has been pressed.
int mode = 0;
int num_mode = 0;
int brght_mode = 0;
int freq_mode = 0;

// Required variables to initialize the led strip.
#define LED_TYPE WS2811
#define COLOR_ORDER GRB
CRGB leds[NUM_LEDS];
CRGB leds_2[NUM_LEDS_2];

// Initiliaze main software variables.
bool flag = true; // Boolean to substitute the switch reading and select from manual or BT mode.
int freq = 20; // It should be greater than 10, because there is a delay in the main loop that interferes with the evaluation of the pushbutton clicks duration.
int num = NUM_LEDS;
int last_num = num;
int brght = 50; // If it is lower than 10, the colors resolution is reduced.
int palette = 0;
int R1_1 = 200;
int G1_1 = 0;
int B1_1 = 0;

int R2_1 = 100;
int G2_1 = 250;
int B2_1 = 0;

int R3_1 = 0;
int G3_1 = 200;
int B3_1 = 0;

int num_2 = NUM_LEDS_2;
int last_num_2 = num_2;
int brght_2 = 50; // If it is lower than 10, the colors resolution is reduced.
int palette_2 = 0;
int R1_2 = 200;
int G1_2 = 0;
int B1_2 = 0;

int R2_2 = 100;
int G2_2 = 250;
int B2_2 = 0;

int R3_2 = 0;
int G3_2 = 200;
int B3_2 = 0;

// Initiliaze variables to manage the palettes.
CRGBPalette16 currentPalette;
TBlendType    currentBlending;
CRGBPalette16 currentPalette_2;
TBlendType    currentBlending_2;

// This function fills the palette with totally random colors.
CRGBPalette16 SetupTotallyRandomPalette() {
  for( int i = 0; i < 16; ++i) {
      currentPalette[i] = CHSV( random8(), 255, random8());
  }
  return currentPalette;
}

// This function sets up a palette of purple and green stripes.
CRGBPalette16 SetupPurpleAndGreenPalette() {
  CRGB purple = CHSV( HUE_PURPLE, 255, 255);
  CRGB green  = CHSV( HUE_GREEN, 255, 255);
  CRGB black  = CRGB::Black;
  
  currentPalette = CRGBPalette16(
                                 green,  green,  black,  black,
                                 purple, purple, black,  black,
                                 green,  green,  black,  black,
                                 purple, purple, black,  black );

  return currentPalette;
}

//CRGBPalette16 currentPalettes[9] = {RainbowColors_p, RainbowStripeColors_p, CloudColors_p, PartyColors_p, OceanColors_p, LavaColors_p, ForestColors_p, SetupTotallyRandomPalette(), SetupPurpleAndGreenPalette()};

void setup() {
  // Initiliaze the serial communication.
  Serial.begin(115200);
  while(!Serial){}
  
  // Initiliaze the bluetooth communication.
  SerialBT.register_callback(btEvent);
  if(!SerialBT.begin("BT420")) { // Begin the bluetooth serial and set the visible module name.
    Serial.println("ERROR: initializing Bluetooth.");
  }
  else {
    Serial.println("Hey there! I am using bluetooth!");
  }

//  // Initialize led to control by the timer.
//  pinMode(8, OUTPUT); 
//  digitalWrite(8, HIGH);
//  
//  // Variables related to the Timer 2 (by registers -> setup).
//  SREG = (SREG & 0b01111111); // Disable all the interruptions.
//  TCNT2 = 0; // Reset the timer register value (it clould be an offset).
//  TIMSK2 = TIMSK2|0b00000001; // Enable flag 0 (bit 0) to set an overflow interruption.
//  TCCR2B = 0b00000111; // Set the frequence by the fisrt three bits (0, 1 and 2) [111 => 7812.5 Hz]
//  SREG = (SREG & 0b01111111) | 0b10000000; // Enable all the interruptions.
  
  // Initialize hardware pins.
  pinMode(pb_pin, INPUT_PULLUP);
  pinMode(flag_pin, INPUT_PULLUP);
  
  // Define the leds stripe.
  FastLED.addLeds<LED_TYPE, LED_PIN, COLOR_ORDER>(leds, NUM_LEDS).setCorrection( TypicalLEDStrip );
  FastLED.setBrightness(brght);
  currentPalette = RainbowColors_p;
  currentBlending = LINEARBLEND;

  FastLED.addLeds<LED_TYPE, LED_PIN_2, COLOR_ORDER>(leds_2, NUM_LEDS_2).setCorrection( TypicalLEDStrip );
  FastLED.setBrightness(brght_2);
  currentPalette_2 = RainbowColors_p;
  currentBlending_2 = LINEARBLEND;
  
  // Finish setup feedback.
  for (int i=0; i<num; i++) {
      leds[i] = CRGB(random8(), random8(), random8());
  }
  for (int i=0; i<num_2; i++) {
      leds_2[i] = CRGB(random8(), random8(), random8());
  }
  FastLED.show();
  FastLED.delay(500);
  
  fill_solid(leds, NUM_LEDS, CRGB::Black);
  fill_solid(leds_2, NUM_LEDS_2, CRGB::Black);
  FastLED.show();

  Serial.println(" * ~ · ~ Setup finished ~ · ~ * ");
}

void loop() {
  //if (digitalRead(flag_pin)) {
  if (flag) {
    // Framework to evaluate the bluetooth communication.
    if ((SerialBT.available())) {
      clearCommand();
      new_serial = true;
    }
    int i = 0;
    while (SerialBT.available() && new_serial) {
      command[i] = SerialBT.read();
      i++;
    }
    if (new_serial) {
      analizeString();
      new_serial = false;
    }
  }
  else {
    // Push-button checking.
    tLocalTimeMs = millis();
    if (!digitalRead(pb_pin)) { // Pushed.
      // Save the first instant pushing.
      if (!pb_Pushed && tLocalTimeMs > tLastPush + 70) { // Let 70 ms from the last push to avoid the uncontrolled pulses of the pushbutton.
        tLastPush = tLocalTimeMs;
        pb_Pushed = true;
        Serial.println("PUSH");
      }
    }
    else { // Not pushed.
      // Check how many time have elapsed from the first instant pushing.
      if (pb_Pushed && tLocalTimeMs > tLastPush + 70) { // Leaving this time allowed (70 ms) seems that the program avoid more noise errors.
        pb_Pushed = false;
        tPressed = tLocalTimeMs - tLastPush;
        tFinalPressed = tLastPush + tPressed;
        clicks_num += 1;
      }
    
      if (tLocalTimeMs > tFinalPressed + 500 && clicks_num == 1) { // Check if it is a single click waiting for 500 ms.
        clicks_num = 0;
    
        if (tPressed > 70 && tPressed < 800) { // Check if it is a short click.
          mode += 1;
          
          if (mode > 3) {
            mode = 0;
          }
          
          switch (mode) {
            case 0:
              currentPalette = RainbowColors_p;
              currentBlending = NOBLEND;
              break;
            case 1:
              currentPalette = CloudColors_p; 
              currentBlending = NOBLEND;
              break;
            case 2:
              SetupPurpleAndGreenPalette();
              currentBlending = LINEARBLEND;
              break;
            case 3:
              currentPalette = PartyColors_p;
              currentBlending = NOBLEND;
              break;
            default:
              // statements
              break;
          }
          Serial.println("Short click");
        }
        else if (tPressed >= 800 && tPressed <= 3000) {  // Check if it is a long click.
          num_mode += 1;
          
          if (num_mode > 3) {
            num_mode = 0;
          }
          
          switch (num_mode) {
            case 0:
              num = NUM_LEDS/(NUM_LEDS/2);
              break;
            case 1:
              num = NUM_LEDS/10;
              break;
            case 2:
              num = NUM_LEDS/2;
              break;
            case 3:
              num = NUM_LEDS;
              break;
            default:
              // statements
              break;
          }

          Serial.println("Long click");
        }
      }
      else if (tPressed > 70 && tPressed < 800 && clicks_num == 2){ // Check if it is a double click of short clicks.
        clicks_num = 0;
        freq_mode += 1;
        
        if (freq_mode > 3) {
          freq_mode = 0;
        }
        
        switch (freq_mode) {
          case 0:
            freq = 10;
            break;
          case 1:
            freq = 100;
            break;
          case 2:
            freq = 500;
            break;
          case 3:
            freq = 2000;
            break;
          default:
            // statements
            break;
        }
        Serial.println("Double short click");
      }
      else if (tPressed > 70 && tPressed >= 800 && clicks_num == 2){ // Check if it is a double click of short click + long click.
        num = 10;
        clicks_num = 0;
        brght_mode += 1;
        
        if (brght_mode > 3) {
          brght_mode = 0;
        }
        
        switch (brght_mode) {
          case 0:
            brght = 2;
            break;
          case 1:
            brght = 10;
            break;
          case 2:
            brght = 50;
            break;
          case 3:
            brght = 100;
            break;
          default:
            // statements
            break;
        }
        
        Serial.println("Double long click");
      }
      else if (clicks_num > 2) { // Restart the clicks' counter if no of the last cases has happened.
        clicks_num = 0;
      }
    }
  }

  // Turn off the left over leds when the number is set to a lower one.
  if (num != last_num) {
    fill_solid(leds, NUM_LEDS, CRGB::Black);
    last_num = num;
  }
  if (num_2 != last_num_2) {
    fill_solid(leds_2, NUM_LEDS_2, CRGB::Black);
    last_num_2 = num_2;
  }
  
  // Framework to set the current palette.
  // static uint8_t startIndex = 0;
  // startIndex = startIndex + 1; /* motion speed */
  // FillLEDsFromPaletteColors(startIndex);
  FillLEDsFromPaletteColors(1);
  // FastLED.setBrightness(brght);
  FastLED.show();
  FastLED.delay(1000 / freq);
}

//ISR(TIMER2_OVF_vect) { // Timer 2 overflow interruption function.
//  t2_counter++;
//  if (t2_counter > threshold) {
//    digitalWrite(8, state);
//    state = !state;
//    t2_counter = 0;
//  }
//}

void FillLEDsFromPaletteColors( uint8_t colorIndex) { // Function to set the colors palette to the leds stripe.
  // uint8_t brightness = 255;
  for( int i = 0; i < num; ++i) {
      leds[i] = ColorFromPalette( currentPalette, colorIndex, brght, currentBlending);
      colorIndex += 3;
  }
  for( int i = 0; i < num_2; ++i) {
      leds_2[i] = ColorFromPalette( currentPalette_2, colorIndex, brght_2, currentBlending_2);
      colorIndex += 3;
  }
}
