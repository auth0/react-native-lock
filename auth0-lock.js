var LockModule = require('NativeModules').Auth0LockModule;

class Auth0Lock {
  constructor(options) {
    if (options) {
      this.lockOptions = {
        clientId: options.clientId,
        domain: options.domain,
        configurationDomain: options.configurationDomain
      };
      this.nativeIntegrations = options.integrations;
    } else {
      this.lockOptions = {};
    }
  }

  show(options, callback) {
    LockModule.init(this.lockOptions);
    if (this.nativeIntegrations) {
      LockModule.registerNativeAuthentication(this.nativeIntegrations);
    }
    LockModule.show(options, callback);
  }
}

module.exports = Auth0Lock;