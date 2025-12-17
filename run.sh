#!/bin/bash

echo "🚖 Autonomous Taxi System"

OUT_DIR="out"
rm -rf $OUT_DIR
mkdir -p $OUT_DIR

echo "Compiling..."
find src -name "*.java" > sources.txt
javac -d $OUT_DIR @sources.txt

if [ $? -eq 0 ]; then
    echo "Starting..."
    echo ""
    java -cp $OUT_DIR com.taxi.Main
else
    echo "Compilation failed"
    exit 1
fi

rm -f sources.txt