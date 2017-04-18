#!/bin/bash
java -jar DataManipulatorGenerator-1.0.1.jar BridgeData.conf
mv Bridge*.java ..\java\eu\letsmine\sponge\bridgesignswitch\