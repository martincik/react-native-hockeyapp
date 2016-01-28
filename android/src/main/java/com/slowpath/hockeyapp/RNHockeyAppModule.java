package com.slowpath.hockeyapp;

import android.app.Activity;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.LoginManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.RuntimeException;
import java.lang.Runnable;
import java.lang.Thread;

public class RNHockeyAppModule extends ReactContextBaseJavaModule {
  public static final int RC_HOCKEYAPP_IN = 9200;

  // This wants to be an enum, but cannot translate that to match the JS API properly
  public static final int AUTHENTICATION_TYPE_ANONYMOUS = 0;
  public static final int AUTHENTICATION_TYPE_EMAIL_SECRET = 1;
  public static final int AUTHENTICATION_TYPE_EMAIL_PASSWORD = 2;
  public static final int AUTHENTICATION_TYPE_DEVICE_UUID = 3;
  public static final int AUTHENTICATION_TYPE_WEB = 4; // Included for consistency, but not supported on Android currently

  private Activity _activity;
  private static ReactApplicationContext _context;
  public static boolean _initialized = false;
  public static String _token = null;
  public static boolean _autoSend = true;
  public static int _authType = 0;
  public static String _appSecret = null;

  public RNHockeyAppModule(ReactApplicationContext _reactContext, Activity activity) {
    super(_reactContext);
    _context = _reactContext;
    _activity = activity;
  }

  @Override
  public String getName() {
    return "RNHockeyApp";
  }

  @ReactMethod
  public void configure(String token, Boolean autoSend, int apiAuthType, String secret) {
    if (!_initialized) {
      _token = token;
      _autoSend = autoSend;
      _authType = apiAuthType;
      _appSecret = secret;
      _initialized = true;
    }
  }

  @ReactMethod
  public void start() {
    if (_initialized) {
      FeedbackManager.register(_activity, _token, null);

      if (_autoSend) {
        CrashManager.register(_activity, _token, new CrashManagerListener() {
          public boolean shouldAutoUploadCrashes() {
            return true;
          }
        });
      } else {
        CrashManager.register(_activity, _token);
      }

      int authenticationMode;
      switch (_authType) {
        case AUTHENTICATION_TYPE_EMAIL_SECRET: {
          authenticationMode = LoginManager.LOGIN_MODE_EMAIL_ONLY;
          break;
        }
        case AUTHENTICATION_TYPE_EMAIL_PASSWORD: {
          authenticationMode = LoginManager.LOGIN_MODE_EMAIL_PASSWORD;
          break;
        }
        case AUTHENTICATION_TYPE_DEVICE_UUID: {
          authenticationMode = LoginManager.LOGIN_MODE_VALIDATE;
          break;
        }
        case AUTHENTICATION_TYPE_WEB: {
          throw new IllegalArgumentException("Web authentication is not supported!");
        }
        case AUTHENTICATION_TYPE_ANONYMOUS:
        default: {
          authenticationMode = LoginManager.LOGIN_MODE_ANONYMOUS;
          break;
        }
      }

      LoginManager.register(_context, _token, _appSecret, authenticationMode, (Class<?>) null);
      LoginManager.verifyLogin(_activity, _activity.getIntent());
    }
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
