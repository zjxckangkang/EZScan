package ezscaner.uniview.app.ezscan.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Administrator on 2016/8/26.
 */
public class VibratorUtil {

    public static void shake(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }

}
