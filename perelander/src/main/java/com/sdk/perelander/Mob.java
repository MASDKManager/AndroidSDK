package com.sdk.perelander;


import android.app.Activity;
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

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                Log.e("AdjustSDK", "onStateChanged: " + event.toString());

                if (activityState.equals(("ON_STOP")) && event.toString().equals("ON_START")) {
                    Log.e("AdjustSDK", "startCounter onStateChanged" );

                }

                activityState = event.toString();
            }
        });
    }

    public static void onResume(Activity activity) {
        Log.e("AdjustSDK", "startCounter onResume" );

        MobInstance mobInstance = Mob.getDefaultInstance();
        mobInstance.setOnResumeActivity(activity);
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

}
