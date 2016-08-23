package ezscaner.uniview.app.ezscan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import ezscaner.uniview.app.ezscan.R;
import ezscaner.uniview.app.ezscan.bean.Device;
import ezscaner.uniview.app.ezscan.db.DBManager;
import ezscaner.uniview.app.ezscan.eventbus.APIMessage;
import ezscaner.uniview.app.ezscan.eventbus.ViewMessage;
import ezscaner.uniview.app.ezscan.log.KLog;
import ezscaner.uniview.app.ezscan.utils.AbStrUtil;
import ezscaner.uniview.app.ezscan.utils.DialogUtil;
import ezscaner.uniview.app.ezscan.utils.ToastUtil;
import ezscaner.uniview.app.ezscan.view.EditTextWithDelete;


@EActivity(R.layout.activity_main)
public class MainActivity extends BaseAct {


    @ViewById
    Button btQuery;

    @ViewById
    View rlQuery;


    @ViewById
    Button btSN;

    @ViewById
    Button btAdd;

    @ViewById
    EditTextWithDelete etSN;

    @ViewById
    EditTextWithDelete etRemarks;

    @ViewById
    EditTextWithDelete etAddress;

    @ViewById
    View llBack;


    @ViewById
    TextView tvNum;

    @Click(R.id.llBack)
    void back() {
        finish();
    }

    @Click(R.id.btQuery)
    void query() {
        Intent intent = new Intent(this, InfoListActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.btSN)
    void scan() {
        Intent intent = new Intent(this, QRCodeScanAct_.class);
        startActivity(intent);
    }

    @AfterViews
    void initViews() {
        updateNum();
        initEditMode();

    }

    private void updateNum() {
        int count = DBManager.getInstance().getDevicesCount();
        tvNum.setText("" + count);
    }

    @Click(R.id.btAdd)
    void add() {
        if (AbStrUtil.isEmpty(etSN.getText().toString())) {
            ToastUtil.longShow(this, "请输入资产编号");
            return;

        }

        if (isEditMode) {
            updateDevice();
            return;
        }

        if (DBManager.getInstance().getDevice(etSN.getText().toString()) == null) {
            Device device = new Device();
            device.setSn(etSN.getText().toString());
            device.setRemarks(etRemarks.getText().toString());
            device.setLocation(etAddress.getText().toString());
            if (!isEditMode) {
                device.setAddTime(System.currentTimeMillis() + "");
            }
            device.setUpdateTime(System.currentTimeMillis() + "");
            long result = DBManager.getInstance().addDevice(device);
            onEndSaveOrAdd(result, true);

        } else {
            DialogUtil.showDialog(this, "该编号的资产已存在，是否覆盖？", "覆盖", new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    updateDevice();
                }
            }, "取消", null);
        }


    }

    private void generateTestDevices() {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Device device = new Device();
            device.setSn(i + "--SN--");
            device.setRemarks(i + "--Remarks--");
            device.setLocation(i + "--Location--");
            device.setAddTime(System.currentTimeMillis() + "");
            device.setUpdateTime(System.currentTimeMillis() + "");

            devices.add(device);
        }
        long result = DBManager.getInstance().addDevices(devices);
        KLog.e(result + "");
    }

    private void updateDevice() {
        Device device = new Device();
        device.setSn(etSN.getText().toString());
        device.setRemarks(etRemarks.getText().toString());
        device.setLocation(etAddress.getText().toString());
//                    device.setAddTime(System.currentTimeMillis() + "");
        device.setUpdateTime(System.currentTimeMillis() + "");
        int result = 0;
        if (isEditMode) {
            device.setId(mDevice.getId());
            result = DBManager.getInstance().updateDeviceByID(device);
        } else {
            result = DBManager.getInstance().updateDeviceBySN(device);

        }
        onEndSaveOrAdd(result, false);
        KLog.e("result", result + "");
    }


    private void onEndSaveOrAdd(long result, boolean isAdd) {
        if (isAdd) {
            if (result > 0) {
                ToastUtil.longShow(this, "添加成功");
                etSN.setText("");
                etRemarks.setText("");
                etAddress.setText("");
            } else {
                ToastUtil.longShow(this, "添加失败");

            }

        } else {
            if (result > 0) {
                ToastUtil.longShow(this, "修改成功");
                etSN.setText("");
                etRemarks.setText("");
                etAddress.setText("");
                if (isEditMode) {
                    finish();
                }
            } else {

                ToastUtil.longShow(this, "修改失败");
            }

        }

        updateNum();
    }

    private boolean isEditMode = false;
    private Device mDevice = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDevice = (Device) bundle.getSerializable(MODIFY);
            if (mDevice != null) {
                KLog.e(MODIFY);
                isEditMode = true;
            }
        }
    }

    private void initEditMode() {
        if (isEditMode) {
            btAdd.setBackgroundResource(R.drawable.ic_save_black_48dp);
            rlQuery.setVisibility(View.INVISIBLE);
            etAddress.setText(mDevice.getLocation());
            etRemarks.setText(mDevice.getRemarks());
            etSN.setText(mDevice.getSn());
            tvLeft.setVisibility(View.GONE);
            tvCenter.setVisibility(View.VISIBLE);
            llBack.setVisibility(View.VISIBLE);
        }

    }

    @ViewById
    View tvLeft;

    @ViewById
    View tvCenter;

    @Override
    public void onEventMainThread(APIMessage apiMessage) {

    }

    @Override
    public void onEventMainThread(ViewMessage viewMessage) {
        if (viewMessage != null) {
            KLog.e(viewMessage.event);
            switch (viewMessage.event) {
                case R.id.scan_result: {
                    etSN.setText(viewMessage.data.toString());
                }
            }
        }
    }

}
