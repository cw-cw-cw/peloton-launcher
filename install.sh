#!/bin/bash
# Usage: ./install.sh <ip>:<connectPort>
# (Pair first: adb pair <ip>:<pairPort> <code>)
set -e
ADDR="${1:?pass the device address, e.g. 192.168.4.34:34627}"
DIR="$(cd "$(dirname "$0")" && pwd)"
adb connect "$ADDR"
adb -s "$ADDR" install -r "$DIR/peloton-launcher.apk"
adb -s "$ADDR" shell cmd package set-home-activity com.colin.treadhome/.MainActivity
adb -s "$ADDR" shell appops set com.colin.treadhome SYSTEM_ALERT_WINDOW allow
adb -s "$ADDR" shell am start -n com.colin.treadhome/.MainActivity
echo "Installed. Press the Peloton home button to open the launcher."
