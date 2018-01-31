:exclamation: *While I do not have the time to actively maintain RN-hockeyapp anymore, I am open to new maintainers taking the lead. If you would be interested, contact me at ladislav (at) benloop (dot) com.* :exclamation:

# react-native-hockeyapp
[HockeyApp](http://hockeyapp.com) integration for React Native.

## Requirements

- iOS 7+
- Android
- React Native >0.17
- CocoaPods

## Installation

```bash
npm install react-native-hockeyapp --save
```

## iOS

You will need:

CocoaPods ([Setup](https://guides.cocoapods.org/using/getting-started.html#installation))

### Podfile

Add to your `ios/Podfile`:
```ruby
pod "HockeySDK"
```

Run `pod install`

### Add Pods.xcodeproj to your project
Drag-and-drop ./ios/Pods/Pods.xcodeproj into your Project > Libraries.

### Add the RNHockeyApp/ folder to your project
Drag-and-drop files from ./node_modules/react-native-hockeyapp/RNHockeyApp into your Project > Libraries.

### Changes to AppDelegate.m
If you wish to use Device UUID authentication or Web authentication, the following must be added to `ios/AppDelegate.m`
```objective-c
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
  if( [[BITHockeyManager sharedHockeyManager].authenticator handleOpenURL:url
                                                        sourceApplication:sourceApplication
                                                               annotation:annotation]) {
    return YES;
  }

  /* Your own custom URL handlers */

  return NO;
}
```

## Android (React Native >= 0.29)

### Google project configuration

* In `android/setting.gradle`

```gradle
...
include ':react-native-hockeyapp', ':app'
project(':react-native-hockeyapp').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-hockeyapp/android')
```

* In `android/build.gradle`

```gradle
...
repositories {
    jcenter()
    mavenCentral()
}
dependencies {
    classpath 'com.android.tools.build:gradle:1.3.1'
    classpath 'net.hockeyapp.android:HockeySDK:4.1.0' // <--- add this
}
```

* In `android/app/build.gradle`

```gradle
apply plugin: "com.android.application"
...
dependencies {
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:23.0.1"
    compile "com.facebook.react:react-native:0.29.+"
    compile project(":react-native-hockeyapp") // <--- add this
}
```

* Manifest file
```xml
<application ..>
    <activity android:name="net.hockeyapp.android.UpdateActivity" />
    <activity android:name="net.hockeyapp.android.FeedbackActivity" />
</application>
```

* Register Module (in MainApplication.java)

```java
import com.slowpath.hockeyapp.RNHockeyAppModule; // <--- import
import com.slowpath.hockeyapp.RNHockeyAppPackage;  // <--- import

public class MainApplication extends Application implements ReactApplication {
  ......

  @Override
  protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
      new RNHockeyAppPackage(MainApplication.this), // <------ add this line to yout MainApplication class
      new MainReactPackage());
  }

  ......

}
```

## Android (React Native 0.17 - 0.28) - Only react-native-hockeyapp:0.4.2 or less

### Google project configuration

* In `android/setting.gradle`

```gradle
...
include ':react-native-hockeyapp', ':app'
project(':react-native-hockeyapp').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-hockeyapp/android')
```

* In `android/build.gradle`

```gradle
...
repositories {
    jcenter()
    mavenCentral()
}
dependencies {
    classpath 'com.android.tools.build:gradle:1.3.1'
    classpath 'net.hockeyapp.android:HockeySDK:4.1.2' // <--- add this
}
```

* In `android/app/build.gradle`

```gradle
apply plugin: "com.android.application"
...
dependencies {
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:23.0.1"
    compile "com.facebook.react:react-native:0.17.+"
    compile project(":react-native-hockeyapp") // <--- add this
}
```

* Manifest file
```xml
<application ..>
    <activity android:name="net.hockeyapp.android.UpdateActivity" />
    <activity android:name="net.hockeyapp.android.FeedbackActivity" />
</application>
```

* Register Module (in MainActivity.java)

```java
import com.slowpath.hockeyapp.RNHockeyAppModule; // <--- import
import com.slowpath.hockeyapp.RNHockeyAppPackage;  // <--- import

public class MainActivity extends ReactActivity {
  ......

  @Override
  protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
      new RNHockeyAppPackage(this), // <------ add this line to yout MainActivity class
      new MainReactPackage());
  }

  ......

}
```

# Usage

From your JS files for both iOS and Android:

```js
var HockeyApp = require('react-native-hockeyapp');

componentWillMount() {
    HockeyApp.configure(HOCKEY_APP_ID, true);
}

componentDidMount() {
    HockeyApp.start();
    HockeyApp.checkForUpdate(); // optional
}
```

You have available these methods:
```js
HockeyApp.configure(HockeyAppId: string, autoSendCrashReports: boolean = true, authenticationType: AuthenticationType = AuthenticationType.Anonymous, appSecret: string = '', ignoreDefaultHandler: string = false); // Configure the settings
HockeyApp.start(); // Start the HockeyApp integration
HockeyApp.checkForUpdate(); // Check if there's new version and if so trigger update
HockeyApp.feedback(); // Ask user for feedback.
HockeyApp.addMetadata(metadata: object); // Add metadata to crash report.  The argument must be an object with key-value pairs.
HockeyApp.generateTestCrash(); // Generate test crash. Only works in no-debug mode.
```
The following authentication methods are available:

1. AuthenticationType.Anonymous - Anonymous Authentication
1. AuthenticationType.EmailSecret - HockeyApp email & App Secret
1. AuthenticationType.EmailPassword - HockeyApp email & password
1. AuthenticationType.DeviceUUID - HockeyApp registered device UUID
1. AuthenticationType.Web - HockeyApp Web Auth (iOS only)

# Contributions
See https://github.com/slowpath/react-native-hockeyapp/graphs/contributors
