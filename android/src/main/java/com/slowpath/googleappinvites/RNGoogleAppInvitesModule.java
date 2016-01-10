package com.slowpath.hockeyapp;

import android.app.Activity;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.RuntimeException;
import java.lang.Runnable;
import java.lang.Thread;

public class RNHockeyAppModule extends ReactContextBaseJavaModule {
  public static final int RC_HOCKEYAPP_IN = 9200;

  private Activity _activity;
  private static ReactApplicationContext _context;
  public static boolean _initialized = false;
  public static String _token = '';

  public RNGoogleAppInvitesModule(ReactApplicationContext _reactContext, Activity activity) {
    super(_reactContext);
    _context = _reactContext;
    _activity = activity;
  }

  @Override
  public String getName() {
    return "RNHockeyApp";
  }

  @ReactMethod
  public void configure(String token, Boolean autoSend) {
    if (!_initialized) {
      _token = token;
      FeedbackManager.register(_activity, token, null);
      if (autoSend) {
        CrashManager.register(_activity, token, new CrashManagerListener() {
          public boolean shouldAutoUploadCrashes() {
            return true;
          }
        });
      } else {
        CrashManager.register(_activity, token);
      }
    }
  }

  @ReactMethod
  public void configure(String token) {
    configure(token, true);
  }

  @ReactMethod
  public void checkForUpdate() {
    if (_initialized) {
      UpdateManager.register(_activity, _token);
    }
  }

  @ReactMethod
  public void feedback() {
    if (_initialized) {
      _activity.runOnUiThread(new Runnable() {
        private Activity _activity;

        public Runnable init(Activity activity) {
          _activity = activity;
          return (this);
        }

        @Override
        public void run() {
          FeedbackManager.showFeedbackActivity(_activity);
        }
      }.init(_activity));
    }
  }

  @ReactMethod
  public void generateCrashReport() {
    if (_initialized) {
      new Thread(new Runnable() {
        public void run() {
          Calendar c = Calendar.getInstance();
          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          throw new RuntimeException("Test crash at " + df.format(c.getTime()));
        }
      }).start();
    }
  }

}
