#!/usr/bin/env bash

# 1. Compile all .java files from src/java/main/ into the bin/ directory
javac -cp .:lib/json-20210307.jar -d bin src/java/main/*.java

# 2. Run the application
java -cp .:lib/json-20210307.jar:bin JournalApp