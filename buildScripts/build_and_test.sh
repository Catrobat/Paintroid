#!/usr/bin/env bash
set -euo pipefail

android_version=$1

cd ..
./gradlew assembleDebug
./gradlew pmd checkstyle lint detekt
./gradlew -PenableCoverage -Pjenkins jacocoTestDebugUnitTestReport

./buildScripts/startEmulator.sh "$android_version" manual_test

./gradlew -PenableCoverage -Pjenkins -Pemulator=android"${android_version}" -Pci createDebugCoverageReport -i
./gradlew stopEmulator