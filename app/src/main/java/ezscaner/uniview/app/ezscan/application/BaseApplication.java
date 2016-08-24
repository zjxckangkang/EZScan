package ezscaner.uniview.app.ezscan.application;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ezscaner.uniview.app.ezscan.eventbus.APIMessage;
import ezscaner.uniview.app.ezscan.eventbus.ViewMessage;
import ezscaner.uniview.app.ezscan.exception.ExceptionHandler;
import ezscaner.uniview.app.ezscan.log.KLog;

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

    @Override
    public void onCreate() {
        super.onCreate();
        ExceptionHandler.getInstance(this).setExceptionHandler();
        EventBus.getDefault().register(this);

//        EventBusUtil.getInstance().register(this);
        INSTANCE = this;
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
