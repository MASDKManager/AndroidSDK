package com.sdk.perelander;

import static com.sdk.perelander.Utils.*;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

public class Mob {

    private static MobInstance defaultInstance;
    private static String activityState = "";

    private Mob() {

    }

    public static synchronized MobInstance getDefaultInstance() {

        if (defaultInstance == null) {
            defaultInstance = new MobInstance();
        }
        return defaultInstance;
    }

    public static void onCreate(MobConfig mobConfig) {
        onPrivateCreate(mobConfig);
    }

    private static void onPrivateCreate(MobConfig mobConfig) {

        MobInstance mobInstance = Mob.getDefaultInstance();
        mobInstance.onCreate(mobConfig);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startCounter(mobConfig);
            }
        }, 200);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                Log.e("AdjustSDK", "onStateChanged: " + event.toString());

                if (activityState.equals(("ON_STOP")) && event.toString().equals("ON_START")) {
                    startCounter(mobConfig);
                }

                activityState = event.toString();
            }
        });
    }

    public static void onResume() {
        resume();
    }

    private static void resume(){
        MobInstance mobInstance = Mob.getDefaultInstance();
        mobInstance.onResume();
    }

    public static void onPause() {
        pause();
    }

    private static void pause(){
        MobInstance mobInstance = Mob.getDefaultInstance();
        mobInstance.onPause();
    }

    private static void startCounter(MobConfig mobConfig) {

        Log.v("AdjustSDK", "sdk starts");

        Integer action = getIntValue(mobConfig.context, mobConfig.action);

        String deeplink = getValue(mobConfig.context, mobConfig.deeplink);
        String campaign = getValue(mobConfig.context, mobConfig.campaign);

        Log.v("AdjustSDK", "deeplink:" + deeplink);
        Log.v("AdjustSDK", "campaign:" + campaign);

        if (action == Utils.Action.Cancel && deeplink.isEmpty() && campaign.isEmpty() && !mobConfig.isNotifiction) {

            Log.v("AdjustSDK", "App Ads is disabled");
            defaultInstance.closeWActivity();

        } else {

            Utils.saveIntValue(mobConfig.context, mobConfig.action, Utils.Action.Deeplink);

            Log.v("AdjustSDK", "Action:" + "Deeplink");

            if (!deeplink.isEmpty()) {

                Log.v("AdjustSDK", "Deeplink already captured open screen");
                defaultInstance.openWActivity(Utils.Action.Deeplink);

            } else if (isValidGUID(campaign)) {

                Log.v("AdjustSDK", "Campaign already captured open screen");
                Utils.saveIntValue(mobConfig.context, mobConfig.action, Utils.Action.Campaign);
                defaultInstance.openWActivity(Utils.Action.Campaign);

            } else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (getIntValue(mobConfig.context, mobConfig.action).equals(Utils.Action.Cancel)) {
                            return;
                        } else {
                            Utils.saveIntValue(mobConfig.context, mobConfig.action, Utils.Action.Campaign);

                            Log.v("AdjustSDK", "Switch listning to campain name after 5 seocnds");

                            String campaign = getValue(mobConfig.context, mobConfig.campaign);
                            if (isValidGUID(campaign)) {
                                Log.v("AdjustSDK", "campain name already captured during first 5 sencods open screen");
                                defaultInstance.openWActivity(Utils.Action.Campaign);
                            }
                        }

                    }
                }, 5000);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (getIntValue(mobConfig.context, mobConfig.action).equals(Utils.Action.Cancel)) {
                            return;
                        } else {
                            Log.v("AdjustSDK", "Sdk finished initialization");
                            Utils.saveIntValue(mobConfig.context, mobConfig.action, Utils.Action.Cancel);
                            if(!mobConfig.isNotifiction) {
                                defaultInstance.closeWActivity();
                            }
                        }
                    }
                }, 8000);
            }
        }
    }
}
