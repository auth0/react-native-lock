# react-native-lock-ios

[![NPM version][npm-image]][npm-url]
[![CI Status][travis-image]][travis-url]
[![CP Version][cocoapods-version-image]][cocoapods-url]
[![CP License][cocoapods-license-image]][cocoapods-url]
[![CP Platform][cocoapods-platform-image]][cocoapods-url]

[Auth0](https://auth0.com) is an authentication broker that supports social identity providers as well as enterprise identity providers such as Active Directory, LDAP, Google Apps and Salesforce.

**react-native-lock-ios** is a wrapper around [Lock](https://github.com/auth0/Lock.iOS-OSX) so it can be used from an iOS React Native application

## Requirements

* iOS 7+ 
* React Native
* CocoaPods

## Installation

Run `npm install --save react-native-lock-ios` to add the package to your app's dependencies.

To tell CocoaPods what native libraries you need, create a file named `Podfile` with the following content inside the folder `<project name>/ios`

```ruby
source 'https://github.com/CocoaPods/Specs.git'
pod 'React', :subspecs => [
  'Core', 
  'RCTImage', 
  'RCTNetwork', 
  'RCTText', 
  'RCTWebSocket'
  ], :path => '../node_modules/react-native'
pod 'LockReactNative', :path => '../node_modules/react-native-lock-ios'
```

Now run from the same folder the command `pod install`. It will automatically download **Lock for iOS** with all it's dependencies, and create an Xcode workspace containing all of them. 
From now on open `<YourAppName>.xcworkspace` instead of `<YourAppName>.xcodeproject`. This is because now React Native's iOS code (and Lock's) is now pulled in via CocoaPods.
Another necessary step you need to do is remove the React, RCTImage, etc. subprojects from your app's Xcode project.

## Usage

Let's require `react-native-lock-ios` module:

```js
var Auth0Lock = require('react-native-lock-ios');
```

And initialize it with your Auth0 credentials that you can get from [our dashboard](https://app.auth0.com/#/applications)

```js
var lock = new Auth0Lock({clientId: "YOUR_CLIENT_ID", domain: "YOUR_DOMAIN"});
```

### Email/Password, Enterprise & Social authentication

```js
lock.show({}, (err, profile, token) => {
  console.log('Logged in!');
});
```

And you'll see our native login screen

[![Lock.png](https://cdn.auth0.com/mobile-sdk-lock/lock-ios-default.png)](https://auth0.com)

### TouchID

```js
lock.show({
  connections: ["touchid"]
}, (err, profile, token) => {
  console.log('Logged in!');
});
```

And you'll see TouchID login screen

[![Lock.png](https://cdn.auth0.com/mobile-sdk-lock/lock-ios-pwdless-touchid.png)](https://auth0.com)

> Because it uses a Database connection, the user can change it's password and authenticate using email/password whenever needed. For example when you change your device.

### SMS Passwordless

```js
lock.show({
  connections: ["sms"]
}, (err, profile, token) => {
  console.log('Logged in!');
});
```
And you'll see SMS Passwordless login screen

[![Lock.png](https://cdn.auth0.com/mobile-sdk-lock/lock-ios-pwdless-sms.png)](https://auth0.com)

### Email Passwordless

```js
lock.show({
  connections: ["email"]
}, (err, profile, token) => {
  console.log('Logged in!');
});
```
And you'll see Email Passwordless login screen

[![Lock.png](https://cdn.auth0.com/mobile-sdk-lock/lock-ios-pwdless-email.png)](https://auth0.com)

## API

### Lock

####.show(options, callback)
Show Lock's authentication screen as a modal screen using the connections configured for your applications or the ones specified in the `options` parameter. This is the list of valid options:

* **closable** (`boolean`): If Lock screen can be dismissed
* **connections** (`[string]`): List of enabled connections to use for authentication. Must be enabled in your app's dashboard first.
* **authParams** (`object`): Object with the parameters to be sent to the Authentication API, e.g. `scope`.

The callback will have the error if anything went wrong or after a successful authentication, it will yield the user's profile info and tokens.

## Issue Reporting

If you have found a bug or if you have a feature request, please report them at this repository issues section. Please do not report security vulnerabilities on the public GitHub issue tracker. The [Responsible Disclosure Program](https://auth0.com/whitehat) details the procedure for disclosing security issues.

## What is Auth0?

Auth0 helps you to:

* Add authentication with [multiple authentication sources](https://docs.auth0.com/identityproviders), either social like **Google, Facebook, Microsoft Account, LinkedIn, GitHub, Twitter, Box, Salesforce, amont others**, or enterprise identity systems like **Windows Azure AD, Google Apps, Active Directory, ADFS or any SAML Identity Provider**.
* Add authentication through more traditional **[username/password databases](https://docs.auth0.com/mysql-connection-tutorial)**.
* Add support for **[linking different user accounts](https://docs.auth0.com/link-accounts)** with the same user.
* Support for generating signed [Json Web Tokens](https://docs.auth0.com/jwt) to call your APIs and **flow the user identity** securely.
* Analytics of how, when and where users are logging in.
* Pull data from other sources and add it to the user profile, through [JavaScript rules](https://docs.auth0.com/rules).

## Create a free account in Auth0

1. Go to [Auth0](https://auth0.com) and click Sign Up.
2. Use Google, GitHub or Microsoft Account to login.

## Author

Auth0

## License

react-native-lock-ios is available under the MIT license. See the [LICENSE file](LICENSE) for more info.

<!-- Variables -->
[npm-image]: https://img.shields.io/npm/v/react-native-lock-ios.svg?style=flat
[npm-url]: https://npmjs.org/package/react-native-lock-ios
[travis-image]: http://img.shields.io/travis/auth0/react-native-lock-ios.svg?style=flat
[travis-url]: https://travis-ci.org/auth0/react-native-lock-ios
[cocoapods-version-image]: https://img.shields.io/cocoapods/v/LockReactNative.svg?style=flat
[cocoapods-license-image]: https://img.shields.io/cocoapods/l/LockReactNative.svg?style=flat
[cocoapods-platform-image]: https://img.shields.io/cocoapods/p/LockReactNative.svg?style=flat
[cocoapods-url]: http://cocoapods.org/pods/LockReactNative

