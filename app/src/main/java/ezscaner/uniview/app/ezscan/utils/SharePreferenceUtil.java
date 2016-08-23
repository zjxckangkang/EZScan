package ezscaner.uniview.app.ezscan.utils;

import android.content.Context;
import android.content.SharedPreferences;

import ezscaner.uniview.app.ezscan.application.BaseApplication;
import ezscaner.uniview.app.ezscan.log.KLog;

/**
 * Created by kangkang on 16/6/5.
 */
public class SharePreferenceUtil {

    private static SharePreferenceUtil INSTANCE;
    private String FILE_NAME = "default";
    private static SharedPreferences sSharedPreferences;

    public static SharePreferenceUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (SharePreferenceUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SharePreferenceUtil();
                }
            }
        }
        return INSTANCE;
    }

    private SharePreferenceUtil() {
        sSharedPreferences = BaseApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

    }

    public void write(String key, Object object) {
        SharedPreferences.Editor editor = sSharedPreferences.edit();

        if (object instanceof String) {
            editor.putString(key, object.toString());

        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);

        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);

        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);

        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);

        } else {
            KLog.e("write error ");

        }

        editor.apply();

    }

    public String get(String key, String defaultValue) {
        return sSharedPreferences.getString(key, defaultValue);
    }

}
