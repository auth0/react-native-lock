var { NativeModules } = require('react-native');
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
    if (!LockModule.hide) {
      callback();
      return;
    }
    LockModule.hide(callback);
  }

  show(options, callback) {
    LockModule.init(this.lockOptions);
    if (this.nativeIntegrations && LockModule.nativeIntegrations) {
      LockModule.nativeIntegrations(this.nativeIntegrations);
    }
    LockModule.show(options, callback);
  }

  authenticate(connectionName, options, callback) {
    LockModule.init(this.lockOptions);
    if (this.nativeIntegrations && LockModule.nativeIntegrations) {
      LockModule.nativeIntegrations(this.nativeIntegrations);
    }
    LockModule.authenticate(connectionName, options, callback);
  }

  delegation(options, callback) {
    LockModule.init(this.lockOptions);
    LockModule.delegation(options, callback);
  }
}

module.exports = Auth0Lock;
