/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet
} from 'react-native';

import Lock from 'react-native-lock';

const styles = StyleSheet.create({
  lock: {
    flex: 1,
    flexDirection: 'column'
  }
});

class App extends Component {
  render() {
    return (
      <Lock
      domain='overmind.auth0.com' clientId='U5MhUrbyQHSVWjlEqZSTCBUFABLbJAS3'
      onCredentials={credentials  => alert('Logged In and got token ' + credentials.id_token)}
      />
    );
  }
}

AppRegistry.registerComponent('Lock', () => App);
