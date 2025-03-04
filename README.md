# EnigmaMachineJavaProject
A small project I made for a class. I wanted to work with Java a bit more and develop my own copy of the Enigma machine.

It uses a JavaSwing GUI to provide a frontend for the user and allows them to enter text to be sent through the virtual enigma machine. They can set the positions of the rotors and the wire connections used for letter substitution, and then encipher and decipher the text strings. 

The Rotors each translate an input letter to an output letter, based on their rotation, which is incrimented every time a letter is entered into the machine. Internally, the code simulates the spinning of the rotors that the real enigma machine utilized along with the reflection mechanism where the letter is sent through the rotors from first to last, then sent back through from last to first.
