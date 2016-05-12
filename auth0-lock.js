var { NativeModules, Platform } = require('react-native');
var LockModule = NativeModules.Auth0LockModule;

const VERSION = require('./version');

class Auth0Lock {
  constructor(options) {
    if (options) {
      this.lockOptions = {
        clientId: options.clientId,
        domain: options.domain,
        configurationDomain: options.configurationDomain,
        libraryVersion: VERSION
      };
      this.nativeIntegrations = options.integrations;
    }
  }

  hide(callback) {
    if (Platform.OS === "android") {
      callback();
      return;
    }
    LockModule.hide(callback);
  }

  show(options, callback) {
    LockModule.init(this.lockOptions);
    if (Platform.OS === "ios" && this.nativeIntegrations) {
      LockModule.nativeIntegrations(this.nativeIntegrations);
    }
    LockModule.show(options, callback);
  }

  authenticate(connectionName, options, callback) {
    if (Platform.OS === "android") {
      callback("Not available in Android", null, null);
      return;
    }
    LockModule.init(this.lockOptions);
    if (this.nativeIntegrations) {
      LockModule.nativeIntegrations(this.nativeIntegrations);
    }
    LockModule.authenticate(connectionName, options, callback);
  }

  delegation(options, callback) {
    if (Platform.OS === "android") {
      callback("Not available in Android", null, null);
      return;
    }
    LockModule.init(this.lockOptions);
    LockModule.delegation(options, callback);
  }

  /**
   * customizeTheme - will apply the theme changes over the shared instance.
   * @param options {obj} - theme customizations, see following link
   *  for all possible values: https://auth0.com/docs/libraries/lock-ios/customization
   *  - A0Theme....Color {str} - '#......' or '' for tranparent.
   *  - A0Theme....ImageName {str}
   *  - A0Theme....Font {obj}
   *    - fontName {str} - ie: 'Lato-Regular'
   *    - fontSize {number} - ie: 12.0
   */
  customizeTheme(options) {
    Object.keys(options).forEach(function (key) {
      if (key.endsWith("Color")) {
        LockModule.registerColorForKey(options[key], key);
      } else if (key.endsWith("Font")) {
        LockModule.registerFontForKey(options[key].fontName, options[key].fontSize, key);
      } else if (key.endsWith("ImageName")) {
        LockModule.registerImageNameForKey(options[key], key);
      } else {
        throw 'Auth0Theme: Key ' + key + ' not supported.';
      }
    });
  }
}

module.exports = Auth0Lock;
