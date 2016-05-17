#!/bin/bash

case "${TRAVIS_OS_NAME}" in
  osx)
    pod install --project-directory=ios
    set -o pipefail && xcodebuild build -workspace ios/A0RNLockCore.xcworkspace -scheme A0RNLockCore -sdk iphonesimulator ONLY_ACTIVE_ARCH=NO | xcpretty -c
  ;;
  linux)
    echo "Not yet implemented"
  ;;
esac