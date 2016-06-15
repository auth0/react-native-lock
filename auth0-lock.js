var { NativeModules, Platform } = require('react-native');
var LockModule = NativeModules.Auth0LockModule;

const VERSION = require('./version');
const Auth0 = require('react-native-auth0');

class Auth0Lock {
  constructor(options) {
    let { clientId, domain } = options;
    if (options != null && clientId != null && domain != null) {
      this.lockOptions = {
        clientId: clientId,
        domain: domain,
        configurationDomain: options.configurationDomain,
        libraryVersion: VERSION
      };
      this.nativeIntegrations = options.integrations;
      this.auth0 = new Auth0(domain);
    } else {
      throw "Must supply clientId & domain";
    }
  }

  hide(callback) {
    if (Platform.OS === "android") {
      setTimeout(() => callback(), 0);
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

  authenticationAPI() {
    return this.auth0.authentication(this.lockOptions.clientId);
  }

  usersAPI(token) {
    return this.auth0.users(token);
  }
}

module.exports = Auth0Lock;
