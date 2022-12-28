package com.sdk.perelander;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {
    private static final String ALGORITHM = "AES";
    private static final String MODE = "AES/CTR/NoPadding";
    private static String KEY ="";

    public CryptUtil (MobConfig mobConfig){

        String sdk_sved_value = Utils.getValue(mobConfig.context,mobConfig.context.getPackageName());
        Log.v("AdjustSDK", "sdk_saved_value: " + sdk_sved_value.toString());

        KEY = mobConfig.aasdKoMLUKKoueHb;

        String action_pref_key;
        String campagin_pref_key;
        String deeplink_pref_key;
        String attribution_pref_key;

        String user_uuid_adj_key ;
        String params_fb_key;
        String sub_endu_fb_key;
        String params_macros_fb_key;
        String os_token_fb_key;

        String[] values = createAStrings(16,6);

        if(sdk_sved_value.isEmpty()){

            KEY = mobConfig.aasdKoMLUKKoueHb;
            Log.v("AdjustSDK", "First encry KEY: " + KEY);

            action_pref_key = values[0];
            campagin_pref_key = values[1];
            deeplink_pref_key = values[2];
            attribution_pref_key = values[3];

            user_uuid_adj_key = "dF89ogGdc9u9\n";
            params_fb_key = "cU0qsTOb\n";
            sub_endu_fb_key = "clk6jzuGYsc=\n";
            params_macros_fb_key = "cU0qsTObWd+4ZqByOQ==\n";
            os_token_fb_key = "bl8HpDGDY9w=\n";

        }else{

            String[] sdk_saved_values = sdk_sved_value.split(",");

            action_pref_key = sdk_saved_values[0];
            campagin_pref_key = sdk_saved_values[1];
            deeplink_pref_key = sdk_saved_values[2];
            attribution_pref_key = sdk_saved_values[3];

            user_uuid_adj_key = sdk_saved_values[4];
            params_fb_key = sdk_saved_values[5];
            sub_endu_fb_key = sdk_saved_values[6];
            params_macros_fb_key = sdk_saved_values[7];
            os_token_fb_key = sdk_saved_values[8];

            KEY = sdk_saved_values[9];

            Utils.saveIntValue(mobConfig.context,values[0],Utils.getIntValue(mobConfig.context, action_pref_key));
            Utils.removeValue(mobConfig.context, action_pref_key);

            Utils.saveValue(mobConfig.context,values[1],Utils.getValue(mobConfig.context, campagin_pref_key));
            Utils.removeValue(mobConfig.context, campagin_pref_key);

            Utils.saveValue(mobConfig.context,values[2],Utils.getValue(mobConfig.context, deeplink_pref_key));
            Utils.removeValue(mobConfig.context, deeplink_pref_key);

            Utils.saveValue(mobConfig.context,values[3],Utils.getValue(mobConfig.context, attribution_pref_key));
            Utils.removeValue(mobConfig.context, attribution_pref_key);

            action_pref_key = values[0];
            campagin_pref_key = values[1];
            deeplink_pref_key = values[2];
            attribution_pref_key = values[3];

        }

        try {

            user_uuid_adj_key = decrypt(user_uuid_adj_key);
            params_fb_key = decrypt(params_fb_key);
            sub_endu_fb_key = decrypt(sub_endu_fb_key);
            params_macros_fb_key = decrypt(params_macros_fb_key);
            os_token_fb_key = decrypt(os_token_fb_key);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.v("AdjustSDK", "decrypt error  " );
        }

        mobConfig.action = action_pref_key;
        mobConfig.campaign = campagin_pref_key;
        mobConfig.deeplink = deeplink_pref_key;
        mobConfig.attribution = attribution_pref_key;
        mobConfig.user_uuid = user_uuid_adj_key;
        mobConfig.param_s = params_fb_key;
        mobConfig.sub_endu = sub_endu_fb_key;
        mobConfig.params_macros = params_macros_fb_key;
        mobConfig.os_token = os_token_fb_key;


        try {

            KEY = values[5];

            user_uuid_adj_key = encrypt(user_uuid_adj_key);
            params_fb_key = encrypt(params_fb_key);
            sub_endu_fb_key = encrypt(sub_endu_fb_key);
            params_macros_fb_key = encrypt(params_macros_fb_key);
            os_token_fb_key = encrypt(os_token_fb_key);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.e("AdjustSDK", "encrypt error  " );
        }

        String sdk_values =
                        action_pref_key.concat( ",") +
                        campagin_pref_key.concat( ",") +
                        deeplink_pref_key.concat( ",") +
                        attribution_pref_key.concat( ",") +
                        user_uuid_adj_key.concat( ",") +
                        params_fb_key.concat( ",") +
                        sub_endu_fb_key.concat( ",") +
                        params_macros_fb_key.concat( ",") +
                        os_token_fb_key.concat( ",") + KEY;

        Log.v("AdjustSDK", "sdk_values: " + sdk_values.toString());

        Utils.saveValue(mobConfig.context,mobConfig.context.getPackageName(),sdk_values);

    }

    public static  String encrypt(String value ) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Log.v("AdjustSDK", "encrypt KEY  " + KEY );

        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(KEY.getBytes()));
        byte[] values = cipher.doFinal(value.getBytes());
        return Base64.encodeToString(values, Base64.DEFAULT);
    }

    public static  String decrypt(String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IllegalBlockSizeException {

        Log.v("AdjustSDK", "decrypt KEY  " + KEY );

        byte[] values = Base64.decode(value, Base64.DEFAULT);
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(KEY.getBytes()));
        return new String(cipher.doFinal(values));
    }

    public static String[] createAStrings(int stringLength, int sizeOfStringArray) {

        String allCharStringContains = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        int maximum = allCharStringContains.length();
        StringBuffer stringBufferObj;
        String[] stringArray = new String[sizeOfStringArray];
        Random randomObj = new Random();
        for (int i = 0; i < sizeOfStringArray; i++) {

            stringBufferObj = new StringBuffer();

            for (int j = 0; j < stringLength; j++) {

                int createdRandomChar = randomObj.nextInt(maximum);
                stringBufferObj.append(allCharStringContains.charAt(createdRandomChar));

            }
            stringArray[i] = stringBufferObj.toString();
        }

        return stringArray;

    }

}