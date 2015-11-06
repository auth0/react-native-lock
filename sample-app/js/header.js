var React = require('react-native');

var {
  StyleSheet,
  Text,
  View,
  Image,
} = React;

var HeaderView = React.createClass({
  render: function() {
    return (
      <View style={styles.header}>
        <Image style={styles.logo} source={require('image!logo-horizontal-blue')}/>
        <Text style={styles.title}>Welcome to Lock + React Native</Text>
      </View>
    );
  }
});

var styles = StyleSheet.create({
  header: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
    paddingTop: 30,
  },
  title: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
    paddingTop: 10,
    fontFamily: 'HelveticaNeue-Light',
    fontSize: 20,
  },
  logo: {
    height: 70,
    width: 191
  },
});

module.exports = HeaderView;