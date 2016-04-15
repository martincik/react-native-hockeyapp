package com.slowpath.hockeyapp;

import android.app.Activity;
import android.content.Context;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.LoginManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

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
  public static boolean _ignoreDefaultHandler = false;
  public static int _authType = 0;
  public static String _appSecret = null;
  public static RNHockeyCrashManagerListener _crashManagerListener = null;

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
    if (_initialized) {
      FeedbackManager.register(_activity, _token, null);

      CrashManager.register(_activity, _token, _crashManagerListener);

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

      _crashManagerListener.deleteMetadataFileIfExists();
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

        FileOutputStream stream = this.context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);

        stream.write(allMetadata.toString().getBytes("UTF8"));
        stream.close();
      } catch (IOException e) {
      } catch (JSONException e) {
      }
    }

    public void deleteMetadataFileIfExists() {
      _context.deleteFile(FILE_NAME);
    }

    private JSONObject getExistingMetadata() {
      try {
        BufferedInputStream stream = new BufferedInputStream(this.context.openFileInput(FILE_NAME));

        int bytesRequired = stream.available();
        byte[] buffer = new byte[bytesRequired];

        stream.read(buffer);
        stream.close();
        String json = new String(buffer, "UTF8");

        return new JSONObject(json);
      } catch (IOException e) {
      } catch (JSONException e) {
      }

      return null;
    }
  }
}
