#!/bin/sh

ios_dir=`pwd`/ios
if [ -d ios_dir ]
  then
  exit 0
fi

podfile="$ios_dir/Podfile"
template=`pwd`/node_modules/react-native-lock/Podfile.template

project_name=$(node -pe "require('./package.json').name")

echo "Checking Podfile in iOS project $project_name ($podfile)"

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
    echo "pod 'Lock', '~> 1.29'"
    echo "pod 'Lock/TouchID'"
    echo "pod 'Lock/SMS'"
    echo "pod 'Lock/Email'"
    echo "pod 'Lock/Safari'"
    echo ""
    echo ""
    echo "and run 'pod install' to install Lock for iOS"
    exit 0
  fi

  rm -f $podfile
  rm -f "$podfile.lock"
fi

echo "Adding Podfile to iOS project"

touch ios/Podfile
cat >ios/Podfile <<EOL
target '${project_name}' do
  # Uncomment the next line if you're using Swift or would like to use dynamic frameworks
  # use_frameworks!
end

# Auth0 Lock

pod 'Lock', '~> 1.29'
pod 'Lock/TouchID'
pod 'Lock/SMS'
pod 'Lock/Email'
pod 'Lock/Safari'
EOL

echo "Installing Pods"

pod install --project-directory=ios