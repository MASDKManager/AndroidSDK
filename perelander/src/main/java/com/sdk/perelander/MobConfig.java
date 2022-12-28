package com.sdk.perelander;

import static android.content.Context.MODE_PRIVATE;

import static com.sdk.perelander.Utils.getValue;
import static com.sdk.perelander.Utils.isValidGUID;
import static com.sdk.perelander.Utils.isValidNaming;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;

import java.util.Date;
import java.util.UUID;

public class MobConfig {

    public String aasdKoMLUKKoueHb = "" ;

    public Context context;
    public String userUUID;
    public String appToken;
    public String osToken;
    public String params;
    public String appUrl;
    public String googleAdId;
    public String paramsMacrosValues;

    public String deeplink = "";
    public String campaign = "";
    public String attribution = "";
    public String action = "";

    public String sub_endu_title = "";
    public String params_macros_title = "";
    public String param_s_title = "";

    public String user_uuid = "";

    public String sub_endu = "";
    public String params_macros = "";
    public String param_s = "";
    public String os_token = "";
    public boolean isNotifiction = false;

    OnSplashListener onSplashListener;
    public Activity sActivity;
    public Activity rActivity;

    public MobConfig(Context context, String at ) {
        init(context, at );
    }

    private void init(Context context, String at  ) {

        aasdKoMLUKKoueHb = getFieldName( new MobConfigField());

        if (context != null) {
            context = context.getApplicationContext();
        }

        this.context = context;
        Log.v("AdjustSDK", "CryptUtil called" );
        new CryptUtil(this);

        this.appToken = at;
        this.userUUID = generateUserUUID(context);

    }

    private static String getFieldName(  Object parent) {

        java.lang.reflect.Field[] allFields = parent.getClass().getFields();
        for (java.lang.reflect.Field field : allFields) {
            try {
                String s = field.toString();
                String last = s.substring(s.lastIndexOf('.') + 1).trim();
                return last;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String generateUserUUID(Context context) {
        String App_PREF =  context.getPackageName() ;

        SharedPreferences preferences = context.getSharedPreferences(App_PREF,
                MODE_PRIVATE);
        String md5uuid = preferences.getString(this.user_uuid, "");

        if (md5uuid.isEmpty()) {
            String guid = "";
            final String uniqueID = UUID.randomUUID().toString();
            Date date = new Date();
            long timeMilli = date.getTime();
            guid = uniqueID + timeMilli;

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(this.user_uuid, guid);
            editor.apply();
            md5uuid = guid;
        }
        return md5uuid;
    }

    public void getRemoteConfig(Activity activity) {

        Log.v("AdjustSDK", "Firebase Remote Config init" );

        sActivity = activity;

        long cacheExpiration = 14400;

        if (BuildConfig.DEBUG) {
            cacheExpiration = 0;
        }

        FirebaseApp.initializeApp(context);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(cacheExpiration)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate().addOnFailureListener( activity, new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("AdjustSDK", "Firebase Remote Config Exception" );
            }
        });

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(activity, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            task.getResult();

                            appUrl = mFirebaseRemoteConfig.getString(sub_endu);
                            Log.v("AdjustSDK", "appUrl: " + appUrl);

                            params = mFirebaseRemoteConfig.getString(param_s);
                            paramsMacrosValues = mFirebaseRemoteConfig.getString(params_macros);

                            osToken = mFirebaseRemoteConfig.getString(os_token);
                            Log.v("AdjustSDK", "Firebase Remote Config Complete" );

                            checkAttrValues();
                            initOneSignal();
                        }
                    }
                });
    }

    private void checkAttrValues(){

        String deeplink = getValue(this.context, this.deeplink);
        String campaign = getValue(this.context, this.campaign);

        if (!deeplink.isEmpty()) {
            Log.v("AdjustSDK", "Deeplink already captured");
            Mob.getDefaultInstance().openWActivity(Utils.Action.Deeplink);

        } else if (isValidGUID(campaign) || isValidNaming(campaign)) {
            Log.v("AdjustSDK", "Campaign already captured");
            Utils.saveIntValue(this.context, this.action, Utils.Action.Campaign);
            Mob.getDefaultInstance().openWActivity(Utils.Action.Campaign);

        }

    }

    private void initOneSignal(){

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this.context);
        OneSignal.setAppId(this.osToken);

        OneSignal.setNotificationOpenedHandler(
                new OneSignal.OSNotificationOpenedHandler() {

                    @Override
                    public void notificationOpened(OSNotificationOpenedResult result) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("AdjustSDK", "postDelayed 5 " );
                                try {
                                    appUrl = (String) result.getNotification().getAdditionalData().get( sub_endu);
                                    Utils.saveIntValue( context, action, Utils.Action.Notification);
                                    isNotifiction = true;

                                    Log.v("AdjustSDK", "Notification received with url: " + appUrl);

                                    Intent intent = new Intent( context, AdsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);
                    }
                });
    }

    public void setOnResumeActivity(Activity activity) {
        this.rActivity = activity;
    }

    public void OnSplashListener(OnSplashListener OnSplashChangedListener) {
        this.onSplashListener = OnSplashChangedListener;
    }
}