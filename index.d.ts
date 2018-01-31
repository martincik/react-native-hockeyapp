type AuthenticationType = 0 | 1 | 2 | 3 | 4

interface HockeyAppStatic {
  AuthenticationType: {
    Anonymous: 0
    EmailSecret: 1
    EmailPassword: 2
    DeviceUUID: 3
    Web: 4
  }
  configure(
    HockeyAppId: string,
    autoSendCrashReports?: boolean,
    authenticationType?: AuthenticationType,
    appSecret?: string,
    ignoreDefaultHandler?: boolean
  ): void // Configure the settings
  start(): void // Start the HockeyApp integration
  checkForUpdate(): void // Check if there's new version and if so trigger update
  feedback(): void // Ask user for feedback.
  addMetadata(metadata: object): void // Add metadata to crash report.  The argument must be an object with key-value pairs.
  generateTestCrash(): void // Generate test crash. Only works in no-debug mode.
}

declare const HockeyApp: HockeyAppStatic

export = HockeyApp
