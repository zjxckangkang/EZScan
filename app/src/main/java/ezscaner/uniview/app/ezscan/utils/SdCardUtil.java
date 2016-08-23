package ezscaner.uniview.app.ezscan.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/8/19.
 */
public class SdCardUtil {
    private static String SDCARD_FILE_NAME = "EZScan";

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getAPPRootDirectory() {
        boolean exist = isSdCardExist();
        if (!exist) {
            return null;
        }

        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String appRootDirectory = sdPath + File.separator + SDCARD_FILE_NAME;

        if (checkAndCreateFile(appRootDirectory, true)) {
            return appRootDirectory;
        } else {
            return null;
        }
    }


    /**
     * 检查并且创建文件
     *
     * @param filePathString 文件路径
     * @param isDirectory    是否是文件夹
     * @return true 文件存在 false 文件不存在
     */
    public static boolean checkAndCreateFile(String filePathString, boolean isDirectory) {
        if (StringUtils.isEmpty(filePathString)) {
            return false;
        }
        File file = new File(filePathString);

        if (!file.exists()) {

            if (isDirectory){
                return  file.mkdirs();

            }else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return file.exists();
            }

        } else {
            //类型检查
            return file.isDirectory() == isDirectory;
        }
    }
}
