package ezscaner.uniview.app.ezscan.application;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ezscaner.uniview.app.ezscan.constants.KeyConstants;
import ezscaner.uniview.app.ezscan.eventbus.APIMessage;
import ezscaner.uniview.app.ezscan.eventbus.ViewMessage;
import ezscaner.uniview.app.ezscan.exception.ExceptionHandler;
import ezscaner.uniview.app.ezscan.log.KLog;
import ezscaner.uniview.app.ezscan.utils.SharePreferenceUtil;

/**
 * Created by kangkang on 16/6/5.
 */
public class BaseApplication extends Application {
    private static BaseApplication INSTANCE;

    public BaseApplication() {
        super();
    }

    public static BaseApplication getInstance() {
        return INSTANCE;
    }

    //记录最近一个启动的MainActivity类型，是否是编辑类型
    public static boolean isEdit = false;
    private boolean autoSave = false;
    private boolean continueScan = false;

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
        SharePreferenceUtil.getInstance().write(KeyConstants.AUTO_SAVE, autoSave);

    }

    public boolean isContinueScan() {
        return continueScan;
    }

    public void setContinueScan(boolean continueScan) {
        this.continueScan = continueScan;
        SharePreferenceUtil.getInstance().write(KeyConstants.CONTINUE_SCAN, continueScan);

    }

    static {
        System.loadLibrary("iconv");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        autoSave = SharePreferenceUtil.getInstance().get(KeyConstants.AUTO_SAVE, false);
        continueScan = SharePreferenceUtil.getInstance().get(KeyConstants.CONTINUE_SCAN, false);

        ExceptionHandler.getInstance(this).setExceptionHandler();
        EventBus.getDefault().register(this);

//        EventBusUtil.getInstance().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(APIMessage apiMessage) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ViewMessage viewMessage) {
        KLog.e(viewMessage.event);

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
