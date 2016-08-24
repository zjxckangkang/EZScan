
package ezscaner.uniview.app.ezscan.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import ezscaner.uniview.app.ezscan.constants.PublicConstants;

public class FileUtil {
    private static final boolean debug = true;

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        File oldfile = new File(oldPath);
        if (oldfile.exists()) { // 文件存在时
            InputStream inStream = null;
            FileOutputStream fs = null;
            // 读入原文件
            try {
                inStream = new FileInputStream(oldPath);
                fs = new FileOutputStream(newPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                byte[] buffer = new byte[1444];
                int bytesum = 0;
                int byteread = 0;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String writeToFile(String filePath, String content)
            throws IOException {

        File errFile = new File(filePath);

        if (!errFile.exists()) {
            errFile.createNewFile();
        }

        final FileOutputStream output = new FileOutputStream(errFile, true);
        output.write(content.getBytes());
        output.flush();
        output.close();
        return filePath;
    }

    public static String write(Context context, String filename, String content)
            throws IOException {
        if (!filename.endsWith(".txt")) {
            filename = filename + ".txt";
        }
        FileOutputStream fos = context.openFileOutput(filename,
                Context.MODE_PRIVATE + Context.MODE_APPEND);
        fos.write(content.getBytes());
        fos.flush();
        fos.close();
        return context.getFilesDir().getPath() + "/" + filename;
    }

    /**
     * 写入 错误信息
     *
     * @param throwable
     * @param printStream
     */
    private static void writeStackTrace(Throwable throwable,
                                        PrintStream printStream) {
        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        Throwable cause = throwable;
        while (cause != null) {
            cause.printStackTrace(printStream);
            cause = cause.getCause();
        }
    }

    /**
     * 写入设备和系统详情
     *
     * @param os
     * @throws IOException
     */
    private static void writeBuildDetails(OutputStream os) throws IOException {
        final StringBuilder result = new StringBuilder();
        final Field[] fields_build = Build.class.getFields();
        // 设备信息
        for (final Field field : fields_build) {
            result.append(field.getName()).append("=");
            try {
                result.append(field.get(null).toString());
            } catch (Exception e) {
                result.append("N/A");
            }
            result.append("\n");
        }
        // 版本信息
        final Field[] fields_version = Build.VERSION.class.getFields();
        for (final Field field : fields_version) {
            result.append(field.getName()).append("=");
            try {
                result.append(field.get(null).toString());
            } catch (Exception e) {
                result.append("N/A");
            }
            result.append("\n");
        }
        os.write(result.toString().getBytes());
    }

    public static String read(Context context, String fileName) {
        String content = "";
        if (!fileName.endsWith(".txt")) {
            fileName = fileName + ".txt";
        }
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            byte buf[] = new byte[1024];
            while ((len = fis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            fis.close();
            content = bos.toString();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    static byte[] lock;

    static {
        lock = new byte[0];
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            boolean isdelete = file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                boolean isdelete = file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            boolean delete = file.delete();
        }
    }

    public static boolean deleteFile(final String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }

        boolean isDelete = false;

        try {
            File dir = new File(path);
            boolean isFile = dir.isFile();
            if (isFile) {
                isDelete = dir.delete();
            } else {
                File[] fs = dir.listFiles();
                if (fs != null) {
                    final int size = fs.length;
                    for (int i = 0; i < size; i++) {
                        isDelete = fs[i].delete();
                    }
                }
                isDelete = dir.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isDelete;
    }

    public static byte[] file2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static void byte2File(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param contex
     * @param assetName
     * @return
     * @throws IOException
     */
    public static InputStream getInputStreamByAssetFileName(Context contex,
                                                            String assetName) throws IOException {
        InputStream in = contex.getResources().getAssets().open(assetName);
        return in;
    }

    /**
     * 除去文件后缀
     *
     * @param fileName
     * @return
     */
    public static String cutSuffixName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return PublicConstants.EMPTY_STRING;
        }
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return PublicConstants.EMPTY_STRING;
        }
        fileName = fileName.substring(0, index);
        return fileName;
    }

    /**
     * 导出图片到相册
     *
     * @param context
     * @param picPath  原图片绝对路径
     * @param fileName 导出后文件的文件名，不带后缀
     */
    public static void exportPicToAlbum(Context context, String picPath, String fileName) {
        String[] strs = picPath.split("\\."); // 路径拆分，获取后缀名

        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);//系统相册路径
        File fCamera = new File(dcim.getPath(), "Camera");
        // 判断目录是否存在
        if (!fCamera.exists()) {
            // 创建目录
            if (!fCamera.mkdirs()) {
                return;
            }
        }

        String dicmpath = dcim.toString() + "/Camera/" + fileName + "." + strs[strs.length - 1];
        FileUtil.copyFile(picPath, dicmpath);
        //发送广播通知系统更新相册
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + dicmpath)));

    }

}
