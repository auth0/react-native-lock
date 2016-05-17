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

  delegation(options) {
    let clientId = this.lockOptions.clientId;
    let domain = this.lockOptions.domain;
    if (!domain.startsWith("http")) {
      domain = `https://${domain}`;
    }

    let payload = {
      "client_id": clientId,
      "grant_type": "urn:ietf:params:oauth:grant-type:jwt-bearer",
    };

    let token = options.refreshToken || options.idToken;
    if (token === undefined) {
        return Promise.reject("must supply either a refreshToken or idToken");
    }

    let attrName = "refresh_token";
    if (options.refreshToken === undefined) {
      attrName = "id_token";
    } else {
      payload["api_type"] = "app";
    }

    payload[attrName] = token;

    if (options.apiType !== undefined) {
      payload["api_type"] = options.apiType;
    }

    if (options.target !== undefined) {
      payload["target"] = options.target;
    }

    if (options.scope !== undefined) {
      payload["scope"] = options.scope;
    }

    return fetch(`${domain}/delegation`, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload)
    })
    .then((response) => response.json());
  }
}


module.exports = Auth0Lock;
