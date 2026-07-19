#!/bin/bash
# Compiles and runs the Hotel Room Booking Console App (Linux/Mac).
set -e
cd "$(dirname "$0")"
echo "Compiling..."
mkdir -p bin
javac -d bin -encoding UTF-8 src/model/*.java src/utility/*.java src/repository/*.java src/service/*.java src/main/*.java
echo "Launching app..."
java -cp bin main.Main
