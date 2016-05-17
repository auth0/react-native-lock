#!/bin/bash

case "${TRAVIS_OS_NAME}" in
  osx)
    gem install cocoapods # Since Travis is not always on latest version
    gem install xcpretty --no-rdoc --no-ri --no-document --quiet
  ;;
esac