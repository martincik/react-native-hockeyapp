var {
  NativeModules: {
    RNHockeyApp
  }
} = require('react-native');
var invariant = require('invariant');

function checkInstalled(){
    invariant(RNHockeyApp, 'react-native-hockeyapp platform setup not complete');
}

module.exports = {
    AuthenticationType: {
        Anonymous: 0,
        EmailSecret: 1,
        EmailPassword: 2,
        DeviceUUID: 3,
        Web: 4
    },
    configure(apiToken, autoSendCrashes, authenticationType, apiSecret, ignoreDefaultHandler) {
        checkInstalled();
        RNHockeyApp.configure(apiToken, autoSendCrashes || true, authenticationType || 0, apiSecret || '', ignoreDefaultHandler || false);
    },
    start(){
        checkInstalled();
        RNHockeyApp.start();
    },
    checkForUpdate(){
        checkInstalled();
        RNHockeyApp.checkForUpdate();
    },
    feedback(){
        checkInstalled();
        RNHockeyApp.feedback();
    },
    addMetadata(metadata){
        checkInstalled();
        var json = JSON.stringify(metadata);
        RNHockeyApp.addMetadata(json);
    },
    generateTestCrash(){
        checkInstalled();
        RNHockeyApp.generateTestCrash();
    }
}

