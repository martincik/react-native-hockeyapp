package com.slowpath.hockeyapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import net.hockeyapp.android.Tracking;

final class RNHockeyAppUsageTracker {
  private static ActivityLifecycleCallbacks callbacks = new ActivityLifecycleCallbacks();
  private static boolean isInitialized = false;

  private RNHockeyAppUsageTracker() {
  }

  public static void initialize(Application app) {
    if (!isInitialized) {
      app.registerActivityLifecycleCallbacks(callbacks);

      isInitialized = true;
    }
  }

  private static final class ActivityLifecycleCallbacks implements android.app.Application.ActivityLifecycleCallbacks {
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
      if (!isHockeyAppPackageActivity(activity)){
        Tracking.startUsage(activity);
      }
    }

    public void onActivityPaused(Activity activity) {
      if (!isHockeyAppPackageActivity(activity)) {
        Tracking.stopUsage(activity);
      }
    }

    public void onActivityStopped(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    private static boolean isHockeyAppPackageActivity(Activity activity) {
      return activity.getClass().getPackage().getName().startsWith("net.hockeyapp.android");
    }
  }
}
