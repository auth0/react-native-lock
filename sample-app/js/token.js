var React = require('react-native');

var {
  StyleSheet,
  Text,
  View,
  Image,
} = React;

var TokenView = React.createClass({

  render: function() {
    return (
      <View style={styles.container}>
        <Text style={styles.label}>Username:</Text>
        <Text style={styles.value}>{this.props.username}</Text>
        <Text style={styles.label}>Email:</Text>
        <Text style={styles.value}>{this.props.email}</Text>
        <Text style={styles.label}>JWT:</Text>
        <Text style={styles.value}>{this.props.jwt}</Text>
        <Text style={styles.label}>Refresh Token:</Text>
        <Text style={styles.value}>{this.props.refreshToken}</Text>
      </View>
    );
  }

});

var styles = StyleSheet.create({
  container: {
    flex: 0,
    justifyContent: 'flex-start',
    alignItems: 'flex-start',
    backgroundColor: '#D0D2D3',
    margin: 8,
    padding: 10,
  },
  label: {
    fontFamily: 'HelveticaNeue-Medium',
    marginTop: 10,
  },
  value: {
    fontFamily: 'HelveticaNeue-Light',
    alignSelf: 'center',
  },
});
module.exports = TokenView;