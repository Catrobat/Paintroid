#!/usr/bin/env bash
set -euo pipefail

android_version=$1
stageName=$2
echo "Starting emulator"
adb start-server
echo no | avdmanager create avd --force --name "android${android_version}" --package "system-images;android-${android_version};default;x86_64"
emulator -no-window -no-boot-anim -noaudio -avd "android${android_version}" > "${stageName}_emulator.log" 2>&1 &

adb devices
timeout 5m adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1;done'
echo "Emulator started"

echo "Disable Animations"
adb shell settings put global window_animation_scale 0 &
adb shell settings put global transition_animation_scale 0 &
adb shell settings put global animator_duration_scale 0 &

adb shell input keyevent KEYCODE_WAKEUP
