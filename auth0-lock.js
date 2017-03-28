import { NativeModules, Platform } from 'react-native';
import Auth0 from 'react-native-auth0';

import VERSION from './version';

const LockModule = NativeModules.Auth0LockModule;

class Auth0Lock {
  constructor(options) {
    let { clientId, domain, style } = options;
    if (options != null && clientId != null && domain != null) {
      this.lockOptions = {
        clientId: clientId,
        domain: domain,
        style: style,
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

export default Auth0Lock;
