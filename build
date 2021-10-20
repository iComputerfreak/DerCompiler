#!/bin/bash

# Renaming this file from build.sh to build causes a conflict with the build folder from gradle

# Get the full path to the directory of this file
# Source: https://stackoverflow.com/questions/4774054/reliable-way-for-a-bash-script-to-get-the-full-path-to-itself/12197518#12197518
pushd . > /dev/null
SCRIPT_PATH="${BASH_SOURCE[0]}";
while([ -h "${SCRIPT_PATH}" ]); do
    cd "`dirname "${SCRIPT_PATH}"`"
    SCRIPT_PATH="$(readlink "`basename "${SCRIPT_PATH}"`")";
done
cd "`dirname "${SCRIPT_PATH}"`" > /dev/null
SCRIPT_PATH="`pwd`";
popd  > /dev/null

# Change into the directory of this file
cd "${SCRIPT_PATH}"

# Run the gradle build command using the gradle wrapper
./gradlew clean
./gradlew build