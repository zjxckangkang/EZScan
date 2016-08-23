package ezscaner.uniview.app.ezscan.exception;

import android.content.Context;
import android.util.Log;

import java.lang.Thread.UncaughtExceptionHandler;

import ezscaner.uniview.app.ezscan.log.KLog;

public class ExceptionHandler implements UncaughtExceptionHandler {


    private static Context mContext;

    private static ExceptionHandler exceptionHandler;

    private ExceptionHandler() {
        KLog.i("ExceptionHandler init");
    }

    public static ExceptionHandler getInstance(Context context) {
        if (exceptionHandler == null) {
            exceptionHandler = new ExceptionHandler();
            mContext = context;
        }

        return exceptionHandler;
    }

    public void setExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 异常处理方法
     *
     * @param thread
     * @param ex
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        KLog.i("uncaughtException start");
        handleException(thread, ex);
        android.os.Process.killProcess(android.os.Process.myPid());


    }


    private void printException(Throwable ex) {
        KLog.e("catch exception:");
        KLog.e(Log.getStackTraceString(ex));
        KLog.e("catch exception end");

    }

    // 程序异常处理方法
    private boolean handleException(Thread thread, Throwable ex) {
        KLog.i(true);
        printException(ex);
        return true;
    }


}
