package com.sdk.perelander;

import static com.sdk.perelander.Utils.*;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.OnDeviceIdsRead;
import java.net.URLEncoder;

public class MobInstance {

    private MobConfig mobConfig;

    public void onCreate(MobConfig config) {
        onPrivateCreate(config);
    }

    private void onPrivateCreate(MobConfig mConfig) {

        this.setMobConfig(mConfig);

        String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
        AdjustConfig config = new AdjustConfig(mobConfig.context, mobConfig.appToken, environment);
        config.setLogLevel(LogLevel.VERBOSE);
        config.setSendInBackground(true);

        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution) {
                saveValue(mobConfig.context, mobConfig.campaign, attribution.campaign);
                saveValue(mobConfig.context, mobConfig.attribution, attribution.toString());

                if (isValidGUID(attribution.campaign)) {
                    openWActivity(Utils.Action.Campaign);
                }

            }
        });

        config.setOnDeeplinkResponseListener(deeplink -> {

            if(!deeplink.toString().isEmpty()) {
                saveValue(mobConfig.context, mobConfig.deeplink, deeplink.toString());
                openWActivity(Utils.Action.Deeplink);
                return false;
            }
            return false;
        });


        Adjust.onCreate(config);

        Adjust.getGoogleAdId(mobConfig.context, new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                mobConfig.googleAdId= googleAdId;
            }
        });

        Adjust.addSessionCallbackParameter(mobConfig.user_uuid, mobConfig.userUUID);

    }

    public void onPause() {
        Adjust.onPause();
    }

    public void onResume() {
        Adjust.onResume();
    }

    private void setMobConfig(MobConfig mobConfig) {
        this.mobConfig = mobConfig;
    }

    public void openWActivity(Integer action) {
        openActivity(action);
    }

    private void openActivity(Integer action) {
        Integer currentAction = Utils.getIntValue(mobConfig.context, mobConfig.action);

        if (action == Utils.Action.Deeplink) {
            Log.v("AdjustSDK", "Deeplink received: " + getValue(mobConfig.context, mobConfig.deeplink));

            if (currentAction == Utils.Action.Deeplink) {
                Log.v("AdjustSDK", "during first 5 seoncds");
            }

            if (currentAction == Utils.Action.Campaign) {
                Log.v("AdjustSDK", "after 5 seoncds");

            }
        }

        if (action == Utils.Action.Campaign) {
            Log.v("AdjustSDK", "Campagin received: " + getValue(mobConfig.context, mobConfig.attribution));

            if (currentAction == Utils.Action.Deeplink) {
                Log.v("AdjustSDK", "during first 5 seoncds");
            }

            if (currentAction == Utils.Action.Campaign) {
                Log.v("AdjustSDK", "after 5 seoncds");

            }
        }

        if (action == Action.Notification) {
            Log.v("AdjustSDK", "Notifiction received");

        }

        if (!currentAction.equals(Utils.Action.Cancel)) {
            if ((action.equals(Utils.Action.Campaign) && currentAction.equals(Utils.Action.Campaign)) || action.equals(Utils.Action.Deeplink) || action.equals(Action.Notification) ) {

                Utils.saveIntValue(mobConfig.context, mobConfig.action, Utils.Action.Cancel);

                Handler handler = new Handler(mobConfig.context.getMainLooper());

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(mobConfig.context, AdsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        mobConfig.context.startActivity(intent);
                    }
                };

                handler.post(runnable);

                Log.v("AdjustSDK", "screen opened");

            }
        }
    }

    public void closeWActivity() {
        cActivity();
    }

    private void cActivity() {

        Log.v("AdjustSDK", "return to app content");

        Handler handler = new Handler(this.mobConfig.context.getMainLooper());

        if (mobConfig.onSplashListener == null) {
            return;
        }
        // add it to the handler queue
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (mobConfig == null) {
                    return;
                }

                if (mobConfig.onSplashListener == null) {
                    return;
                }

                mobConfig.onSplashListener.OnSplashChanged();

            }
        };

        handler.post(runnable);

    }

    public String getMainU() {
        return getMU();

    }

    private String getMU(){

        if(this.mobConfig.isNotifiction){
            return this.mobConfig.appUrl;
        }

        String endURL = this.mobConfig.appUrl;

        try {

            String attribution = getValue(this.mobConfig.context, mobConfig.attribution);
            String deeplink = getValue(this.mobConfig.context, mobConfig.deeplink);

            String str = this.mobConfig.params;
            String[] params_macros_values = this.mobConfig.paramsMacrosValues.split(",");

            str =  str.replace(params_macros_values[0], getValue(this.mobConfig.context, mobConfig.campaign));
            str =  str.replace(params_macros_values[1], this.mobConfig.googleAdId);
            str =  str.replace(params_macros_values[2], Adjust.getAdid());
            str =  str.replace(params_macros_values[3], this.mobConfig.context.getPackageName());
            str =  str.replace(params_macros_values[4],  URLEncoder.encode(deeplink, "UTF-8"));
            str =  str.replace(params_macros_values[5], this.mobConfig.userUUID);
            str =  str.replace(params_macros_values[6], URLEncoder.encode(attribution, "UTF-8"));
            str =  str.replace(params_macros_values[7], "");
            str =  str.replace(params_macros_values[8], "");

            if (endURL != null && !endURL.equals("") && !endURL.startsWith("http")) {
                endURL = "https://" + endURL;
            }

            endURL = endURL + "?" + str;

            Log.v("AdjustSDK", "screen URL: " + endURL);

        } catch (Exception ignored) {

        }

        return endURL;
    }


}