package com.slowpath.hockeyapp;

import android.app.Activity;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.slowpath.hockeyapp.RNHockeyAppModule;

public class RNHockeyAppPackage implements ReactPackage {
  private Activity _activity;

  public RNHockeyAppPackage(Activity activity) {
    super();
    _activity = activity;
  }

  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();

    modules.add(new RNHockeyAppModule(reactContext, _activity));

    return modules;
  }

  @Override
  public List<Class<? extends JavaScriptModule>> createJSModules() {
    return Collections.emptyList();
  }

  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
     return Collections.emptyList();
  }
}
