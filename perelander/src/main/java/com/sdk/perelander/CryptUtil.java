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
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {
    private static final String ALGORITHM = "AES";
    private static final String MODE = "AES/CTR/NoPadding";
    private static String KEY ="";

    public CryptUtil (MobConfig mobConfig){

        String sdk_sved_value = Utils.getValue(mobConfig.context,mobConfig.context.getPackageName());
        Log.v("AdjustSDK", "sdk_sved_value: " + sdk_sved_value.toString());

        KEY = mobConfig.aasdKoMLUKKoueHb;

        String action;
        String campagin;
        String deeplink;
        String attribution;

        String params_value;
        String sub_endu_value;
        String params_macros_value;

        String user_uuid ;
        String params;
        String sub_endu;
        String params_macros;
        String os_token;
        String[] values = createAStrings(16,9);

        if(sdk_sved_value.isEmpty()){

            KEY = mobConfig.aasdKoMLUKKoueHb;

            action = values[0];
            campagin = values[1];
            deeplink = values[2];
            attribution = values[3];

            params_value = values[4];
            sub_endu_value = values[5];
            params_macros_value = values[6];

            user_uuid = "dF89ogGdc9u9\n";
            params = "cU0qsTOb\n";
            sub_endu = "clk6jzuGYsc=\n";
            params_macros = "cU0qsTObWd+4ZqByOQ==\n";
            os_token = "bl8HpDGDY9w=\n";


        }else{

            String[] sdk_saved_values = sdk_sved_value.split(",");

            action = sdk_saved_values[0];
            campagin = sdk_saved_values[1];
            deeplink = sdk_saved_values[2];
            attribution = sdk_saved_values[3];

            params_value = sdk_saved_values[4];
            sub_endu_value = sdk_saved_values[5];
            params_macros_value = sdk_saved_values[6];

            user_uuid = sdk_saved_values[7];
            params = sdk_saved_values[8];
            sub_endu = sdk_saved_values[9];
            params_macros = sdk_saved_values[10];
            os_token = sdk_saved_values[11];

            KEY = sdk_saved_values[12];

            Utils.saveIntValue(mobConfig.context,values[0],Utils.getIntValue(mobConfig.context, action));
            Utils.removeValue(mobConfig.context, action);

            Utils.saveValue(mobConfig.context,values[1],Utils.getValue(mobConfig.context, campagin));
            Utils.removeValue(mobConfig.context, campagin);

            Utils.saveValue(mobConfig.context,values[2],Utils.getValue(mobConfig.context, deeplink));
            Utils.removeValue(mobConfig.context, deeplink);

            Utils.saveValue(mobConfig.context,values[3],Utils.getValue(mobConfig.context, attribution));
            Utils.removeValue(mobConfig.context, attribution);

            Utils.saveValue(mobConfig.context,values[4],Utils.getValue(mobConfig.context, params_value));
            Utils.removeValue(mobConfig.context, params_value);

            Utils.saveValue(mobConfig.context,values[5],Utils.getValue(mobConfig.context, sub_endu_value));
            Utils.removeValue(mobConfig.context, sub_endu_value);

            Utils.saveValue(mobConfig.context,values[6],Utils.getValue(mobConfig.context, params_macros_value));
            Utils.removeValue(mobConfig.context, params_macros_value);

            action = values[0];
            campagin = values[1];
            deeplink = values[2];
            attribution = values[3];

            params_value = values[4];
            sub_endu_value = values[5];
            params_macros_value = values[6];

        }

        try {
           String os_token3 = encrypt("os_token");
            user_uuid = decrypt(user_uuid);
            params = decrypt(params);
            sub_endu = decrypt(sub_endu);
            params_macros = decrypt(params_macros);
            os_token = decrypt(os_token);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.v("AdjustSDK", "decrypt error  " );
        }

        mobConfig.action = action;
        mobConfig.campaign = campagin;
        mobConfig.deeplink = deeplink;
        mobConfig.attribution = attribution;
        mobConfig.user_uuid = user_uuid;
        mobConfig.param_s = params;
        mobConfig.sub_endu = sub_endu;
        mobConfig.params_macros = params_macros;
        mobConfig.os_token = os_token;
        mobConfig.param_s_title = params_value;
        mobConfig.sub_endu_title = sub_endu_value;
        mobConfig.params_macros_title = params_macros_value;

        String params_saved = Utils.getValue(mobConfig.context, params_value);

        try {
            if(!params_saved.isEmpty()) {
                mobConfig.params = decrypt(Utils.getValue(mobConfig.context, params_value));
                mobConfig.appUrl = decrypt(Utils.getValue(mobConfig.context, sub_endu_value));
                mobConfig.paramsMacrosValues = decrypt(Utils.getValue(mobConfig.context, params_macros_value));
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.v("AdjustSDK", "decrypt error  " );
        }

        try {

            KEY = values[8];

            user_uuid = encrypt(user_uuid);
            params = encrypt(params);
            sub_endu = encrypt(sub_endu);
            params_macros = encrypt(params_macros);
            os_token = encrypt(os_token);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.e("AdjustSDK", "encrypt error  " );
        }

        String sdk_values =
                        action.concat( ",") +
                        campagin.concat( ",") +
                        deeplink.concat( ",") +
                        attribution.concat( ",") +
                        params_value.concat( ",") +
                        sub_endu_value.concat( ",") +
                        params_macros_value.concat( ",") +
                        user_uuid.concat( ",") +
                        params.concat( ",") +
                        sub_endu.concat( ",") +
                        params_macros.concat( ",") +
                        os_token.concat( ",") + KEY;

        Log.v("AdjustSDK", "sdk_values: " + sdk_values.toString());

        Utils.saveValue(mobConfig.context,mobConfig.context.getPackageName(),sdk_values);

    }

    public static  String encrypt(String value ) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(KEY.getBytes()));
        byte[] values = cipher.doFinal(value.getBytes());
        return Base64.encodeToString(values, Base64.DEFAULT);
    }

    public static  String decrypt(String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IllegalBlockSizeException {
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