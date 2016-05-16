#!/bin/sh

ios_dir=`pwd`/ios
if [ -d ios_dir ]
  then
  exit 0
fi

podfile="$ios_dir/Podfile"
template=`pwd`/node_modules/react-native-lock/Podfile.template

echo "Checking Podfile in iOS project ($podfile)"

if [ -f $podfile ]
  then
  echo ""
  echo "Found an existing Podfile, Do you want to override it? [N/y]"
  read generate_env_file

  if [ "$generate_env_file" != "y" ]
    then
    echo "Add the following pods":
    echo ""
    echo ""
    cat $template
    echo ""
    echo ""
    echo "and run 'pod install' to install Lock for iOS"
    exit 0
  fi

  rm -f $podfile
  rm -f "$podfile.lock"
fi

echo "Adding Podfile to iOS project"

cd ios
pod init >/dev/null 2>&1
cat $template >> $podfile
cd ..

echo "Installing Pods"

pod install --project-directory=ios