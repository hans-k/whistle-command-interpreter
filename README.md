# Whistle command interpreter
For a more complete presentation: https://hans-k.github.io/blog


# Technical details
The software part of this project has to two parts; one Arduino, one Java.
#### Arduino code
Only one file, main.ino. Should be compatible with most Arduino’s. Reads the microphone as often as possible and uses a Fast Fourier Transformation (FFT) to deduce frequency out of decibel readings. This data is then sent away on the serial port. The Arduino part purposely does little else than reading the microphone, because the FFT decibel-to-frequency trick only works with large amounts of measurements.
#### Java code
A Java ‘back end’. Handles incoming communication on a serial port. Displays the last twenty received frequency readings in a nice graph. These same twenty readings are compared to defined triggers, and if one matches, the associated next trigger is available for 1.5 seconds. If all triggers of one combination are completed within their time, an action executes. This last bit is where you can connect any of your own actuators.

More on specifics is available as Javadoc.


# How to use
## Adding your own whistle combinations
Currently, two TriggerCombinations are defined in line 27 and 28 of Main.java. If you don't really know how the Triggers relate to actual sound, experiment a while with the UI enabled.

## Adding your own actions
Custom actions can be added in the placeholder 'doAction' method in the TriggerCombination class.
