#!/bin/bash

# First we need to set up some functions to display the test results in a pretty way.
# Source: https://stackoverflow.com/a/54190627/3426509 (modified by disabling color output, but keeping the indent)
BOOTUP=nocolor
RES_COL=60
MOVE_TO_COL="echo -en \\033[${RES_COL}G"
SETCOLOR_SUCCESS="echo -en \\033[1;32m"
SETCOLOR_FAILURE="echo -en \\033[1;31m"
SETCOLOR_WARNING="echo -en \\033[1;33m"
SETCOLOR_NORMAL="echo -en \\033[0;39m"

echo_success() {
    $MOVE_TO_COL
    echo -n "["
    [ "$BOOTUP" = "color" ] && $SETCOLOR_SUCCESS
    echo -n $"  OK  "
    [ "$BOOTUP" = "color" ] && $SETCOLOR_NORMAL
    echo -n "]"
    echo -ne "\r"
    return 0
}

echo_failure() {
    $MOVE_TO_COL
    echo -n "["
    [ "$BOOTUP" = "color" ] && $SETCOLOR_FAILURE
    echo -n $"FAILED"
    [ "$BOOTUP" = "color" ] && $SETCOLOR_NORMAL
    echo -n "]"
    echo -ne "\r"
    return 1
}

echo_passed() {
    $MOVE_TO_COL
    echo -n "["
    [ "$BOOTUP" = "color" ] && $SETCOLOR_WARNING
    echo -n $"PASSED"
    [ "$BOOTUP" = "color" ] && $SETCOLOR_NORMAL
    echo -n "]"
    echo -ne "\r"
    return 1
}

echo_warning() {
    $MOVE_TO_COL
    echo -n "["
    [ "$BOOTUP" = "color" ] && $SETCOLOR_WARNING
    echo -n $"WARNING"
    [ "$BOOTUP" = "color" ] && $SETCOLOR_NORMAL
    echo -n "]"
    echo -ne "\r"
    return 1
}

# Next we set up the functions to control the test stages
# Source: https://stackoverflow.com/a/5196220/3426509 (removed error output to file, instead keep it on stderr)

# Use step(), try(), and next() to perform a series of commands and print
# [  OK  ] or [FAILED] at the end. The step as a whole fails if any individual
# command fails.
#
# Example:
#     step "Remounting / and /boot as read-write:"
#     try mount -o remount,rw /
#     try mount -o remount,rw /boot
#     next
step() {
    echo -n "$@"

    STEP_OK=0
    [[ -w /tmp ]] && echo $STEP_OK > /tmp/step.$$
}

try() {
    # Check for `-b' argument to run command in the background.
    local BG=

    [[ $1 == -b ]] && { BG=1; shift; }
    [[ $1 == -- ]] && {       shift; }

    # Run the command.
    if [[ -z $BG ]]; then
        "$@" 2>> "$LOG_STEPS"
    else
        "$@" &
    fi

    # Check if command failed and update $STEP_OK if so.
    local EXIT_CODE=$?

    if [[ $EXIT_CODE -ne 0 ]]; then
        STEP_OK=$EXIT_CODE
        [[ -w /tmp ]] && echo $STEP_OK > /tmp/step.$$

        if [[ -n $LOG_STEPS ]]; then
            local FILE=$(readlink "${BASH_SOURCE[1]}")
            local LINE=${BASH_LINENO[0]}

            echo "$FILE: line $LINE: Command \`$*' failed with exit code $EXIT_CODE." >> "$LOG_STEPS"
        fi
    fi

    return $EXIT_CODE
}

next() {
    [[ -f /tmp/step.$$ ]] && { STEP_OK=$(< /tmp/step.$$); rm -f /tmp/step.$$; }
    [[ $STEP_OK -eq 0 ]]  && echo_success || echo_failure
    echo

    return $STEP_OK
}

# We need to keep track if all tests succeeded
FAILED_TESTS=0
SUCCEEDED_TESTS=0
inc_failures() {
	FAILED_TESTS=$((FAILED_TESTS+1))
}
inc_successes() {
	SUCCEEDED_TESTS=$((SUCCEEDED_TESTS+1))
}

# Change into the directory of this file, so we can set everything up relative to the script
cd $(dirname $0)
LOG_STEPS="`pwd`/tests.log"
# Clear the log file if it exists already
if [ -f "$LOG_STEPS" ]; then
	rm -f "$LOG_STEPS"
fi


###################################
### Setting up test environment ###
###################################

# We need to create some directories and files. Best do this in a separate directory
step "Setting up test environment"
try mkdir .tests
try cd .tests
try mkdir "Der Compiler"
try cd "Der Compiler"
try mkdir folder1
try mkdir folder1/folder2
try touch file1
try touch folder1/file2
try touch folder1/folder2/file3
try touch "file 4"
try mkdir "folder 3"
try touch "folder 3/file 5"
cd .. # Move back into .tests
next && inc_successes || inc_failures

# Resulting file structure:
# .tests/Der Compiler
# ├── file\ 4
# ├── file1
# ├── folder\ 3
# │   └── file\ 5
# └── folder1
#     ├── file2
#     └── folder2
#         └── file3


############################
### Testing build script ###
############################

# We are currently in .tests, so the build script is located in the folder above us
step "Testing build script"
try ../gradlew build -x test
next && inc_successes || inc_failures


#############################
### Testing echo argument ###
#############################

# --echo argument
test_echo() {
	PATH_PREFIX="$1"
	try "$RUN_PATH" --echo "${PATH_PREFIX}file 4"
	try "$RUN_PATH" --echo "${PATH_PREFIX}file1"
	try "$RUN_PATH" --echo "${PATH_PREFIX}folder 3/file 5"
	try "$RUN_PATH" --echo "${PATH_PREFIX}folder1/file2"
	try "$RUN_PATH" --echo "${PATH_PREFIX}folder1/folder2/file3"
	
}

RUN_PATH="../run"
step "Testing run --echo from different working directory"
test_echo "Der Compiler/" # Our files are located in the "Der Compiler" directory
next && inc_successes || inc_failures

# Now we change into the run script directory and do it all again from there
RUN_PATH="./run"
step "Testing run --echo from run script directory"
try cd ..
try test_echo ".tests/Der Compiler/" # Our files are located in the ".tests/Der Compiler" directory
try # Change back into .tests
try cd .tests
next && inc_successes || inc_failures

# Reset the run path
RUN_PATH="../run"
step "Testing calling --echo after the argument"
try "$RUN_PATH" "Der Compiler/file 4" --echo
try "$RUN_PATH" "Der Compiler/file1" --echo
try "$RUN_PATH" "Der Compiler/folder 3/file 5" --echo
try "$RUN_PATH" "Der Compiler/folder1/file2" --echo
try "$RUN_PATH" "Der Compiler/folder1/folder2/file3" --echo


#################
### Next Test ###
#################



###############
### Cleanup ###
###############

step "Cleanup"
try cd ..
try rm -rf .tests
next && inc_successes || inc_failures


###############
### Results ###
###############

ALL_TESTS=$((FAILED_TESTS+SUCCEEDED_TESTS))
if [ $FAILED_TESTS -gt 0 ]; then
	echo "$FAILED_TESTS of $ALL_TESTS tests failed."
	echo
	echo "#################"
	echo "### Error Log ###"
	echo "#################"
	cat "$LOG_STEPS"
	exit 1
else
	echo "All tests succeeded."
	exit 0
fi