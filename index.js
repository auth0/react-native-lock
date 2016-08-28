import React, { Component } from 'react';
import {
  AppRegistry,
  StatusBar,
  StyleSheet,
  Text,
  Image,
  ScrollView,
  Dimensions,
  View
} from 'react-native';

import { Header, Input, PrimaryButton, Tabs, TextButton } from 'react-native-auth0-ui';
import Auth0 from 'react-native-auth0';

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'space-between',
    backgroundColor: '#ffffff'
  }
});

class Lock extends Component {
  constructor(props) {
    super(props);

    this.state = {
      viewHeight: Dimensions.get('window').height,
      user: {}
    }
  }

  refreshLayout() {
    console.log('re-layout');
    return this.setState({
      viewHeight: Dimensions.get('window').height
    });
  }

  render() {
    return (
      <ScrollView>
      <View style={[styles.mainView, {height: this.state.viewHeight}]}>
        <View>
          <Header/>
          <Tabs />
        </View>
        <View style={{paddingTop: 15, paddingBottom: 30}}>
            <Input
              placeholder='Email'
              validator={(val) => {
                let user = this.state.user;
                user.email = val;
                this.setState({user: user});
                if(/^[\w._-]+[+]?[\w._-]+@[\w.-]+\.[a-zA-Z]{2,6}$/.test(val)) {
                   return true;
                }
  	            return false;
   	          }}
              errorMsg='Please enter a valid email'
            />
            <Input
              placeholder='Password'
              icon='lock'
              isPassword={true}
              validator={(val) => {
                let user = this.state.user;
                user.password = val;
                this.setState({user: user});
                return val != null && val.length > 0;
              }}
            />
        </View>
        <View>
          <TextButton />
          <PrimaryButton action={() => {
            const auth0 = new Auth0(this.props.domain);
            const auth = auth0.authentication(this.props.clientId);
            auth.login(this.state.user.email, this.state.user.password, 'Username-Password-Authentication')
              .then(credentials => this.props.onCredentials(credentials))
              .catch(e => alert(e.message));
          }}/>
        </View>
      </View>
      </ScrollView>
    );
  }
}

export default Lock;
