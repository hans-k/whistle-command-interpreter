#include "arduinoFFT.h" // From https://github.com/kosme/arduinoFFT
arduinoFFT FFT = arduinoFFT();

#define SAMPLES 1024 // Must be a power of 2
#define SAMPLING_FREQUENCY 40000

unsigned int sampling_period_us;
unsigned long currentMicros, previousMicros;

double vReal[SAMPLES];
double vImag[SAMPLES];

void setup() {
  sampling_period_us = round(1000000 * (1.0 / SAMPLING_FREQUENCY));
  Serial.begin(115200);
}

void loop() {
  for (int i = 0; i < SAMPLES; i++) {
    currentMicros = micros() - previousMicros; // To prevent the program from crashing when micros() overflows after ~70 min
    previousMicros = currentMicros;
    vReal[i] = analogRead(39);
    vImag[i] = 0;
    while (micros() < (currentMicros + sampling_period_us)) {
      // do nothing
    }
  }
  FFT.Windowing(vReal, SAMPLES, FFT_WIN_TYP_HAMMING, FFT_FORWARD);
  FFT.Compute(vReal, vImag, SAMPLES, FFT_FORWARD);
  FFT.ComplexToMagnitude(vReal, vImag, SAMPLES);
  for (int i = 2; i < (SAMPLES/2); i++){
    if (vReal[i] > 1500) {
      Serial.print(':');Serial.print(i);Serial.print(';');
    }
  }
}

