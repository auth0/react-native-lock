var LockModule = require('NativeModules').LockReact;

class Auth0Lock {
  constructor(options) {
    this.lockOptions = {
      clientId: options.clientId,
      domain: options.domain,
      configurationDomain: options.configurationDomain
    };
    this.nativeIntegrations = options.integrations;
  }

  show(options, callback) {
    Lock.init(this.lockOptions);
    if (this.nativeIntegrations) {
      Lock.registerNativeAuthentication(this.nativeIntegrations);
    }
    Lock.show(options, callback);
  }
}