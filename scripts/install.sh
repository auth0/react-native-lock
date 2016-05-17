#!/bin/bash

case "${TRAVIS_OS_NAME}" in
  osx)
    bundle install
    bundle exec pod repo update
    bundle exec pod install --project-directory=ios
  ;;
esac