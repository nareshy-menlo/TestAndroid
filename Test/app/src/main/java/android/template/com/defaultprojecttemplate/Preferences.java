package android.template.com.defaultprojecttemplate;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class Preferences {

    private static final String PREFERENCE_NAME = "default"; // Todo "default"  should be replaced with propername

    private static Preferences preference;
    private SharedPreferences sharedPreferences;


    public static Preferences getInstance(Context context) {
        if (preference == null) {
            preference = new Preferences(context);
        }
        return preference;
    }

    private Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putString(encryptValue(key), encryptValue(value));
        prefsEditor.apply();
    }

    public void saveIntData(String key, int value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(encryptValue(key), encryptValue(String.valueOf(value)));
        prefsEditor.apply();
    }

    public void saveLongData(String key, long value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putString(encryptValue(key), encryptValue(String.valueOf(value)));
        prefsEditor.apply();
    }

    public void saveBooleanData(String key, boolean value){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putString(encryptValue(key), encryptValue(String.valueOf(value)));
        prefsEditor.apply();
    }

    public String getData(String key) {
        return (String) decryptType(key, "", "");
    }

    public String getDecrptyKey(String key){
        return decryptString(key);
    }

    public int getIntData(String key){
        return (Integer) decryptType(key, 0, -1);
    }

    public int getIntData(String key, int defaultValue){
        return (Integer) decryptType(key, 0, defaultValue);
    }

    public long getLongData(String key){
        return (Long) decryptType(key, 0L, -1L);
    }

    public long getLongData(String key, long sportId){
        return (Long) decryptType(key, 0L, sportId);
    }

    public boolean getBooleanData(String key) {
        return (Boolean) decryptType(key, false, false);
    }

    public void clearData(){
        if(preference != null){
            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            prefsEditor.clear().apply();
        }
    }

    private String encryptValue(String value) {
        return encryptString(value);
    }

    private String encryptString(String message) {
        try {
            String encString = AESCrypt.encrypt(BuildConfig.CRYPTO_KEY, message);
            return encodeCharset(encString);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decryptString(String message) {
        try {
            String decString = removeEncoding(message);
            return AESCrypt.decrypt(BuildConfig.CRYPTO_KEY, decString);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    private String removeEncoding(String value) {
        String encodedString = value;
        encodedString = encodedString.replaceAll("x0P1Xx", "\\+").replaceAll("x0P2Xx", "/").replaceAll("x0P3Xx", "=");
        return encodedString;
    }

    private String encodeCharset(String value) {
        String encodedString = value;
        encodedString = encodedString.replaceAll("\\+", "x0P1Xx").replaceAll("/", "x0P2Xx").replaceAll("=", "x0P3Xx");
        return encodedString;
    }

    private boolean containsEncryptedKey(String encryptedKey) {
        return sharedPreferences.contains(encryptedKey);
    }

    private <T> Object decryptType(String key, Object type, T defaultType) {
        String encKey = encryptString(key);

        if (TextUtils.isEmpty(encKey) || !containsEncryptedKey(encKey)) {
            return defaultType;
        }

        String value = sharedPreferences.getString(encKey, null);
        if (TextUtils.isEmpty(value)) {
            return defaultType;
        }

        String orgValue = decryptString(value);
        if (TextUtils.isEmpty(orgValue)) {
            return defaultType;
        }

        if (type instanceof String) {
            return orgValue;
        } else if (type instanceof Integer) {
            try {
                return Integer.parseInt(orgValue);
            } catch (NumberFormatException e) {
                return defaultType;
            }
        } else if (type instanceof Long) {
            try {
                long actValue =  Long.parseLong(orgValue);
                return  actValue == -1?defaultType:actValue;
            } catch (NumberFormatException e) {
                return defaultType;
            }
        } else if (type instanceof Float) {
            try {
                return Float.parseFloat(orgValue);
            } catch (NumberFormatException e) {
                return defaultType;
            }
        } else if (type instanceof Boolean) {
            return Boolean.parseBoolean(orgValue);
        } else {
            return defaultType;
        }
    }
}