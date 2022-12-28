package com.sdk.perelander;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    @IntDef({Action.Deeplink, Action.Campaign, Action.Cancel, Action.Notification, Action.SecondLaunch, Action.Pending})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Action {
        int Deeplink = 1;
        int Campaign = 2;
        int Cancel = 3;
        int Notification = 4;
        int SecondLaunch = 5;
        int Pending = 6;
    }

    public static boolean isValidGUID(String str) {
        return isVGUID(str);
    }

    private static boolean isVGUID(String str) {

        str = str.split("_")[0];

        if (str == null) {
            return false;
        }

        return Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$").matcher(str).matches();
    }

    public static boolean isValidNaming(String str) {
        return isVNaming(str);
    }

    private static boolean isVNaming(String str) {
        final String regex = "(?<=\\[\\[)(.*)(?=\\]\\])";

        final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            str = "[[" + matcher.group(1) + "]]";
        }else{
            return false;
        }

        return Pattern.compile("\\[\\[(?<key>[^_]+)_?(?<sub1>[^_]+)?_?(?<sub2>[^_]+)?_?(?<sub3>[^_]+)?_?(?<sub4>[^_]+)?_?(?<sub5>[^_]+)?\\]\\]").matcher(str).matches();
    }

    public static void saveValue(Context context, String title, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences( md5(context.getPackageName()), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(title, value);
        editor.apply();
    }

    public static String getValue(Context context, String title) {
        SharedPreferences sharedPref = context.getSharedPreferences( md5(context.getPackageName()), MODE_PRIVATE);
        return sharedPref.getString(title, "");
    }


    public static void saveIntValue(Context context, String title, Integer value) {
        SharedPreferences sharedPref = context.getSharedPreferences( md5(context.getPackageName()), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(title, value);
        editor.apply();
    }

    public static Integer getIntValue(Context context, String title) {
        SharedPreferences sharedPref = context.getSharedPreferences( md5(context.getPackageName()), MODE_PRIVATE);
        return sharedPref.getInt(title, 0);
    }

    public static void removeValue(Context context, String title) {
        SharedPreferences sharedPref = context.getSharedPreferences( md5(context.getPackageName()), MODE_PRIVATE);
        sharedPref.edit().remove(title).apply();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {

            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}