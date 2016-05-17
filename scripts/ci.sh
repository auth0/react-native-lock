#!/bin/bash

case "${TRAVIS_OS_NAME}" in
  osx)
    set -o pipefail && xcodebuild build -workspace ios/A0RNLockCore.xcworkspace -scheme A0RNLockCore -sdk iphonesimulator ONLY_ACTIVE_ARCH=NO | xcpretty -c
  ;;
  linux)
    cd android
    ./gradlew clean test --continue
  ;;
esac