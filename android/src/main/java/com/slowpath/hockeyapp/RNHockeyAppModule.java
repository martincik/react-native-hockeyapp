package com.slowpath.hockeyapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.LoginManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.metrics.MetricsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

public class RNHockeyAppModule extends ReactContextBaseJavaModule {
  public static final int RC_HOCKEYAPP_IN = 9200;
  private static final int DEFAULT_BUFFER_SIZE = 8192;

  // This wants to be an enum, but cannot translate that to match the JS API properly
  public static final int AUTHENTICATION_TYPE_ANONYMOUS = 0;
  public static final int AUTHENTICATION_TYPE_EMAIL_SECRET = 1;
  public static final int AUTHENTICATION_TYPE_EMAIL_PASSWORD = 2;
  public static final int AUTHENTICATION_TYPE_DEVICE_UUID = 3;
  public static final int AUTHENTICATION_TYPE_WEB = 4; // Included for consistency, but not supported on Android currently

  private static ReactApplicationContext _context;
  public static boolean _initialized = false;
  public static String _token = null;
  public static boolean _autoSend = true;
  public static boolean _ignoreDefaultHandler = false;
  public static int _authType = 0;
  public static String _appSecret = null;
  public static RNHockeyCrashManagerListener _crashManagerListener = null;

  public RNHockeyAppModule(ReactApplicationContext _reactContext) {
    super(_reactContext);
    _context = _reactContext;
  }

  @Override
  public String getName() {
    return "RNHockeyApp";
  }

  @ReactMethod
  public void configure(String token, Boolean autoSend, int apiAuthType, String secret, Boolean ignoreDefaultHandler) {
    if (!_initialized) {
      _token = token;
      _autoSend = autoSend;
      _authType = apiAuthType;
      _appSecret = secret;
      _ignoreDefaultHandler = ignoreDefaultHandler;
      _crashManagerListener = new RNHockeyCrashManagerListener(_context, _autoSend, _ignoreDefaultHandler);
      _initialized = true;
    }
  }

  @ReactMethod
  public void start() {

    Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
        // The currentActivity can be null if it is backgrounded / destroyed, so we simply
        // no-op to prevent any null pointer exceptions.
        return;
    }
    if (_initialized) {
      FeedbackManager.register(currentActivity, _token, null);

      CrashManager.register(currentActivity, _token, _crashManagerListener);
      MetricsManager.register(currentActivity.getApplication(), _token);

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

      LoginManager.register(_context, _token, _appSecret, authenticationMode, currentActivity.getClass());
      LoginManager.verifyLogin(currentActivity, currentActivity.getIntent());

      _crashManagerListener.deleteMetadataFileIfExists();
    }
  }

  @ReactMethod
  public void checkForUpdate() {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
        // The currentActivity can be null if it is backgrounded / destroyed, so we simply
        // no-op to prevent any null pointer exceptions.
        return;
    }
    if (_initialized) {
      UpdateManager.register(currentActivity, _token);
    }
  }

  @ReactMethod
  public void setUserName(String name) {
    if (!_initialized) {
      return;
    }
    FeedbackManager.setUserName(name);
  }

  @ReactMethod
  public void setUserEmail(String userEmail) {
    if (!_initialized) {
      return;
    }
    FeedbackManager.setUserEmail(userEmail);
  }

  @ReactMethod
  public void setUserId(String id) {
    if (!_initialized) {
      return;
    }
    FeedbackManager.setUserId(id);
  }

  @ReactMethod
  public void feedback() {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
        // The currentActivity can be null if it is backgrounded / destroyed, so we simply
        // no-op to prevent any null pointer exceptions.
        return;
    }
    if (_initialized) {
      currentActivity.runOnUiThread(new Runnable() {
        private Activity currentActivity;

        public Runnable init(Activity activity) {
          currentActivity = activity;
          return (this);
        }

        @Override
        public void run() {
          FeedbackManager.showFeedbackActivity(currentActivity);
        }
      }.init(currentActivity));
    }
  }

  @ReactMethod
  public void addMetadata(String metadata) {
    if (_initialized) {
      _crashManagerListener.addMetadata(metadata);
    }
  }

  @ReactMethod
  public void generateTestCrash() {
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

   @ReactMethod
  public void trackEvent(String eventName) {
    if(_initialized)
    {
      log(eventName);
       MetricsManager.trackEvent(eventName);
    }
  }

   @ReactMethod
  public void trackEventWithOptionsAndMeasurements(String eventName, Map<String,String> properties, Map<String,Double> measurements) {
    if(_initialized)
    {
      log(eventName);
       MetricsManager.trackEvent(eventName, properties, measurements);
    }
  }

  private void log(String message) {
     Log.d("ReactNativeJS", "react-native-hockeyapp: " + message);
   }

  private static class RNHockeyCrashManagerListener extends CrashManagerListener {
    private boolean autoSend = false;
    private boolean ignoreDefaultHandler = false;
    private Context context = null;

    private static final String FILE_NAME = "HockeyAppCrashMetadata.json";

    public RNHockeyCrashManagerListener(Context context, boolean autoSend, boolean ignoreDefaultHandler) {
      this.context = context;
      this.autoSend = autoSend;
      this.ignoreDefaultHandler = ignoreDefaultHandler;
    }

    @Override
    public boolean ignoreDefaultHandler() {
      return this.ignoreDefaultHandler;
    }

    @Override
    public boolean shouldAutoUploadCrashes() {
      return this.autoSend;
    }

    @Override
    public String getDescription() {
      JSONObject metadata = this.getExistingMetadata();

      if (metadata == null) {
        return null;
      }

      return metadata.toString();
    }

    public void addMetadata(String metadata) {
      OutputStream stream = null;
      try {
        JSONObject newMetadata = new JSONObject(metadata);
        JSONObject allMetadata = this.getExistingMetadata();

        if (allMetadata == null) {
          allMetadata = new JSONObject();
        }

        // Merge new metadata into existing metadata, overwriting existing keys if necessary.
        for (Iterator<String> keys = newMetadata.keys(); keys.hasNext(); ) {
          String key = keys.next();

          allMetadata.put(key, newMetadata.get(key));
        }

        stream = this.context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        stream = new BufferedOutputStream(stream, DEFAULT_BUFFER_SIZE);

        stream.write(allMetadata.toString().getBytes("UTF8"));

      } catch (IOException|JSONException e) {
      } finally {
        try {
          stream.close();
        } catch (IOException e) {
          // NO OP
        }
      }
    }

    public void deleteMetadataFileIfExists() {
      _context.deleteFile(FILE_NAME);
    }

    private JSONObject getExistingMetadata() {
      InputStream stream = null;
      try {
        stream = new BufferedInputStream(this.context.openFileInput(FILE_NAME), DEFAULT_BUFFER_SIZE);
        StringBuilder builder = new StringBuilder(DEFAULT_BUFFER_SIZE);

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int bytesRead;
        do {
          bytesRead = stream.read(buffer);
          if (bytesRead > 0) {
            builder.append(new String(buffer, 0, bytesRead, "UTF8"));
          }
        } while(bytesRead > 0);

        return new JSONObject(builder.toString());
      } catch (IOException|JSONException e) {
        if (stream != null) {
          try {
            stream.close();
          } catch (IOException er) {
            // NO OP
          }
        }
      }

      return null;
    }
  }
}
