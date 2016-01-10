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
pod "HockeySDK"
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
dependencies {
    classpath 'com.android.tools.build:gradle:1.3.1'
    classpath 'net.hockeyapp.android:HockeySDK:3.0.2' // <--- add this
}
```

* In `android/app/build.gradle`

```gradle
apply plugin: "com.android.application"
apply plugin: 'net.hockeyapp.android:HockeySDK' // <--- add this at the TOP
...
dependencies {
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:23.0.1"
    compile "com.facebook.react:react-native:0.14.+"
    compile project(":react-native-hockeyapp") // <--- add this
}
```

* Manifest file
```xml
<activity android:name="net.hockeyapp.android.UpdateActivity" />
```

* Register Module (in MainActivity.java)

```java
import com.slowpath.hockeyapp.RNHockeyAppModule; // <--- import
import com.slowpath.hockeyapp.RNHockeyAppPackage;  // <--- import

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  ......

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReactRootView = new ReactRootView(this);

    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage())
      .addPackage(new RNHockeyAppPackage(this)) // <------ add this line to yout MainActivity class
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mReactRootView.startReactApplication(mReactInstanceManager, "AndroidRNSample", null);

    setContentView(mReactRootView);
  }
  ......

}
```


# Usage

From your JS files for both iOS and Android:

```js
var HockeyApp = require('react-native-hockeyapp');

HockeyApp.configure(HOCKEY_KEY);
// or turn off auto send crash reports
HockeyApp.configure(HOCKEY_KEY, false);
```

