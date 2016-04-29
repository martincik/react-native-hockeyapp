# react-native-hockeyapp
[HockeyApp](http://hockeyapp.com) integration for React Native.

## Requirements

- iOS 7+
- Android
- React Native >0.14
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
pod 'React', :path => '../node_modules/react-native', :subspecs => [
  'Core',
  'RCTImage',
  'RCTNetwork',
  'RCTText',
  'RCTWebSocket'
]
pod 'RNHockeyApp', :path => '../node_modules/react-native-hockeyapp'
```

Run `pod install`

### Add Pods.xcodeproj to your project
Drag-and-drop ./ios/Pods/Pods.xcodeproj into your Project > Libraries.

### Add $(inherited) flag to your Build Settings
Under your app target -> Build Settings, look for Other Linker Flags and add `$(inherited)`.
![Build Settings](https://cloud.githubusercontent.com/assets/8598682/14924166/f595db8a-0df5-11e6-84b2-1d51aebdd678.png)

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

## Android

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
allprojects {
    repositories {
        ...
        // add this line below
        flatDir { dirs "$projectDir/../../node_modules/react-native-hockeyapp/android/HockeySDK-Android/libs" }
    }
}
```

* In `android/app/build.gradle`

```gradle
apply plugin: "com.android.application"
...
dependencies {
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:23.0.1"
    compile "com.facebook.react:react-native:0.19.+"
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
    HockeyApp.checkForUpdate();
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
@martincik, @berickson1, @rspeyer, @dtivel
