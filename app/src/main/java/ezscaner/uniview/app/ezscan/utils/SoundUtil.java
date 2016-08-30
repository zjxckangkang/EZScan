package ezscaner.uniview.app.ezscan.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import ezscaner.uniview.app.ezscan.R;
import ezscaner.uniview.app.ezscan.log.KLog;

/**
 * Created by Administrator on 2016/8/26.
 */
public class SoundUtil {

    public static void beep(Context context) {
        MediaPlayer mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        AssetFileDescriptor file = context.getResources()
                .openRawResourceFd(R.raw.beep);
        try {
            mp.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            // mp.setVolume(leftVolume, rightVolume)
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            KLog.i();
        }
    }
}
