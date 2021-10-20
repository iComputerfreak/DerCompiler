#!/bin/bash

# Renaming this file from build.sh to build causes a conflict with the build folder from gradle

# Change into the directory of this file
cd $(dirname $0)

# Run the gradle build command using the gradle wrapper
./gradlew build