package ezscaner.uniview.app.ezscan.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ezscaner.uniview.app.ezscan.constants.APIEventConstants;
import ezscaner.uniview.app.ezscan.constants.KeyConstants;
import ezscaner.uniview.app.ezscan.constants.PublicConstants;
import ezscaner.uniview.app.ezscan.constants.ViewEventConstants;
import ezscaner.uniview.app.ezscan.eventbus.APIMessage;
import ezscaner.uniview.app.ezscan.eventbus.ViewMessage;
import ezscaner.uniview.app.ezscan.log.KLog;
import ezscaner.uniview.app.ezscan.utils.DialogUtil;

/**
 * Created by Administrator on 2016/7/13.
 */
@EActivity
public class BaseAct extends Activity implements KeyConstants, PublicConstants, APIEventConstants, ViewEventConstants {
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        EventBusUtil.getInstance().register(this);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(APIMessage apiMessage) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ViewMessage viewMessage) {
        KLog.e(viewMessage.event);

    }

    ProgressDialog mProgressDialog = null;

    @UiThread
    void showProgressDialog() {
        mProgressDialog = DialogUtil.showProgressDialog(this, "请稍候");
    }

    @UiThread
    void dismissProgressDialog() {
        DialogUtil.dismissDialog(mProgressDialog);
    }

}
