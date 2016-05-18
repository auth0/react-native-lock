var { NativeModules, Platform } = require('react-native');
var LockModule = NativeModules.Auth0LockModule;

const VERSION = require('./version');

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

  delegation(options) {
    let { clientId, domain } = this.lockOptions;
    if (!domain.startsWith("http")) {
      domain = `https://${domain}`;
    }

    let payload = {
      "client_id": clientId,
      "grant_type": "urn:ietf:params:oauth:grant-type:jwt-bearer",
    };

    let token = options.refreshToken || options.idToken;
    if (token == null) {
        return Promise.reject("must supply either a refreshToken or idToken");
    }

    let attrName = "refresh_token";
    if (options.refreshToken == null) {
      attrName = "id_token";
    }

    payload[attrName] = token;

    if (options.apiType != null) {
      payload["api_type"] = options.apiType;
    }

    if (options.target != null) {
      payload["target"] = options.target;
    }

    if (options.scope != null) {
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
    .then(response => response.json());
  }

  refreshToken(refreshToken, options) {
    const delegationOptions = Object.assign({}, options);
    delegationOptions.refreshToken = refreshToken;
    delegationOptions.apiType = "app";
    return this.delegation(delegationOptions)
    .then(json => {
      return {
        idToken: json.id_token,
        expiresIn: json.expires_in,
        tokenType: json.token_type
      };
    });
  }
}

module.exports = Auth0Lock;
