/*
 * Type definitions for HockeyApp RN plugin
 */

export enum AuthenticationType {
    Anonymous = 0,
    EmailSecret = 1,
    EmailPassword = 2,
    DeviceUUID = 3,
    Web = 4
}

export default HockeyApp;

export class HockeyApp {
    /**
     * Configures it with the approrpiate app ID and user settings (e.g. should crash reports be automatically submitted).
    *
    * @param {string} appId
    * @param {boolean} autoSendCrashes
    * @param {AuthenticationType} authenticationType
    * @param {string} apiSecret
    * @param {Function} ignoreDefaultHandler
    * @memberof HockeyApp
    */
    static configure(appId: string, autoSendCrashes?: boolean, authenticationType?: AuthenticationType, apiSecret?: string, ignoreDefaultHandler?: Function): void;
    /**
     * Initializes the HockeyApp plugin.
    *
    * @memberof HockeyApp
    */
    static start(): void;
    /**
     * Checks for a new update from the HockeyApp service
    *
    * @memberof HockeyApp
    */
    static checkForUpdate(): void;
    /**
     * Displays the feedback UI so that testers can send and receive feedback about the app.
    *
    * @memberof HockeyApp
    */
    static feedback(): void;
    /**
     * Attaches arbitrary metadata to the next crash report in order to provide more context about the user's state.
    *
    * @param {Object} metadata
    * @memberof HockeyApp
    */
    static addMetadata(metadata: Object): void;
    /**
     * Immediately crashes the app. This is used strictly for testing the HockeyApp crash reporting capabilities.
    *
    * @memberof HockeyApp
    */
    static generateTestCrash(): void;
    /**
     * Logs an app-specific event for analytic purposes.
    *
    * @param {string} eventName
    * @memberof HockeyApp
    */
    static trackEvent(eventName: string): void;

    static setUserName(userName: string): void;
    static setUserEmail(userEmail: string): void;
    static setUserId(userId: string): void;
}
