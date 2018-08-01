var {
    NativeModules: {
        RNHockeyApp
    }
} = require('react-native');
var invariant = require('invariant');

function checkInstalled() {
    invariant(RNHockeyApp, 'react-native-hockeyapp platform setup not complete');
}

export const AuthenticationType = {
    Anonymous: 0,
    EmailSecret: 1,
    EmailPassword: 2,
    DeviceUUID: 3,
    Web: 4
};

export const HockeyApp = {
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
    start() {
        checkInstalled();
        RNHockeyApp.start();
    },
    checkForUpdate() {
        checkInstalled();
        RNHockeyApp.checkForUpdate();
    },
    feedback() {
        checkInstalled();
        RNHockeyApp.feedback();
    },
    setUserName(userName) {
        checkInstalled();
        RNHockeyApp.setUserName(userName);
    },
    setUserEmail(userEmail) {
        checkInstalled();
        RNHockeyApp.setUserEmail(userEmail);
    },
    setUserId(userId) {
        checkInstalled();
        RNHockeyApp.setUserId(userId);
    },
    addMetadata(metadata) {
        checkInstalled();
        var json = JSON.stringify(metadata);
        RNHockeyApp.addMetadata(json);
    },
    generateTestCrash() {
        checkInstalled();
        RNHockeyApp.generateTestCrash();
    },
    trackEvent(eventName) {
        checkInstalled();
        RNHockeyApp.trackEvent(eventName);
    },
    trackEventWithOptionsAndMeasurements(eventName, options, measurements) {
        checkInstalled();
        RNHockeyApp.trackEventWithOptionsAndMeasurements(eventName, options || {}, measurements || {});
    }
}

export default HockeyApp;
