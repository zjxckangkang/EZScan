package ezscaner.uniview.app.ezscan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import ezscaner.uniview.app.ezscan.application.BaseApplication;
import ezscaner.uniview.app.ezscan.bean.Device;
import ezscaner.uniview.app.ezscan.db.DBManager;
import ezscaner.uniview.app.ezscan.eventbus.APIMessage;
import ezscaner.uniview.app.ezscan.eventbus.ViewMessage;
import ezscaner.uniview.app.ezscan.log.KLog;
import ezscaner.uniview.app.ezscan.utils.AbStrUtil;
import ezscaner.uniview.app.ezscan.utils.DialogUtil;
import ezscaner.uniview.app.ezscan.utils.SharePreferenceUtil;
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
    CheckBox cbAutoSave;

    @ViewById
    View llBack;

    @ViewById
    TextView tvNum;

//    @Click(R.id.ivMore)
//    void more() {
//        List<String> strings = new ArrayList<>();
//        strings.add("清除生成的Excel");
//        strings.add("访问Excel文件夹");
//        MaterialDialog dialog = new MaterialDialog.Builder(this)
//                .items(strings)
//                .itemsCallback(new MaterialDialog.ListCallback() {
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                        switch (position) {
//                            case 0:
//
//                                try {
//                                    File rootFile = new File(SdCardUtil.appRootPath);
//                                    for (File file : rootFile.listFiles()) {
//                                        FileUtil.deleteFile(file);
//                                    }
//                                    ToastUtil.longShow(MainActivity.this, "清除成功");
//                                } catch (Exception e) {
//                                    KLog.e("Exception");
//                                    ToastUtil.longShow(MainActivity.this, "清除失败");
//                                }
//
//
//                                break;
//                            case 1:
////                                Uri uri = Uri.fromFile(new File(SdCardUtil.appRootPath));
////                                Intent mIntent = new Intent();
////                                mIntent.setAction(Intent.ACTION_VIEW);
////                                mIntent.setDataAndType(uri,"file/*");
//////                                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//////                                mIntent.setAction("android.intent.action.VIEW");
//////                                mIntent.setData(uri);
////                                startActivity(mIntent);
//                                File file = new File(SdCardUtil.appRootPath).listFiles()[0];
//
//                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                intent.setDataAndType(Uri.fromFile(new File(SdCardUtil.appRootPath)), "directory/*");
//                                try {
//                                    startActivity(intent);
//                                    startActivity(Intent.createChooser(intent,"选择浏览工具"));
//                                } catch (ActivityNotFoundException e) {
//                                   KLog.e("ActivityNotFoundException");
//                                }
//                                break;
//
//                        }
//                    }
//                })
//                .build();
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams layoutParams = window.getAttributes();
//        window.setGravity(Gravity.RIGHT | Gravity.TOP);
//        layoutParams.x = 0; // 新位置X坐标
//        layoutParams.y = 0; // 新位置Y坐标
//        layoutParams.width = 700; // 宽度
//        window.setAttributes(layoutParams);
//        dialog.show();
//
//    }

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
        initListener();
        updateNum();
        initEditMode();
    }


    @ViewById
    TextView tvSaveState;


    private void initListener() {
        cbAutoSave.setChecked(SharePreferenceUtil.getInstance().get(AUTO_SAVE, false));
        cbAutoSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharePreferenceUtil.getInstance().write(AUTO_SAVE, isChecked);
            }
        });
        tvSaveState.setVisibility(View.INVISIBLE);
        etSN.setAfterTextChangedListener(new EditTextWithDelete.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (AbStrUtil.isEmpty(s.toString())) {
                    tvSaveState.setVisibility(View.INVISIBLE);
                } else {
                    tvSaveState.setVisibility(View.VISIBLE);
                    Device device = DBManager.getInstance().getDevice(etSN.getText().toString());
                    if (device != null) {
                        tvSaveState.setText("已保存");
                    } else {
                        tvSaveState.setText("未保存");

                    }
                }

            }
        });
    }

    private void updateSaveState() {
        String sn = etSN.getText().toString();
        etSN.setText("");
        etSN.setText(sn);
    }

    private void updateNum() {
        int count = DBManager.getInstance().getDevicesCount();
        tvNum.setText("" + count);
    }

    @Click(R.id.btAdd)
    void add() {
        final String sn = etSN.getText().toString();
        if (AbStrUtil.isEmpty(sn)) {
            ToastUtil.longShow(this, "请输入资产编号");
            return;

        }

        if (isEditMode) {
            KLog.e();
            updateDevice(sn);
            return;
        }

        if (DBManager.getInstance().getDevice(sn) == null) {
            Device device = new Device();
            device.setSn(sn);
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
                    updateDevice(sn);
                }
            }, "取消", null);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApplication.isEdit = isEditMode;
        updateNum();
        updateSaveState();
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

    private void updateDevice(String sn) {
        Device device = new Device();
        device.setSn(sn);
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
//                etSN.setText("");
                etRemarks.setText("");
//                etAddress.setText("");
            } else {
                ToastUtil.longShow(this, "添加失败");

            }

        } else {
            if (result > 0) {
                ToastUtil.longShow(this, "修改成功");
//                etSN.setText("");
                etRemarks.setText("");
//                etAddress.setText("");
                if (isEditMode) {
                    finish();
                }
            } else {

                ToastUtil.longShow(this, "修改失败");
            }

        }

        updateSaveState();
        updateNum();
    }

    private boolean isEditMode = false;
    private Device mDevice = null;

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            super.onBackPressed();
            return;
        }

        DialogUtil.showDialog(this, "确定退出？", "退出", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                finish();
            }
        }, "取消", null);
    }

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

        //由于复用了MainActivity，导致在修改资产信息时，栈低的MainActivity也会受到消息

        //编辑Activity，却不是编辑
        if (isEditMode && !BaseApplication.isEdit) {
            return;
        }

        //添加Activity，却不是添加
        if (!isEditMode && BaseApplication.isEdit) {
            return;
        }

        if (viewMessage != null) {
            KLog.e(viewMessage.event);
            switch (viewMessage.event) {
                case R.id.scan_result: {
                    etSN.setText(viewMessage.data.toString());
                    if (cbAutoSave.isChecked()) {
                        add();
                    }

                }
            }
        }
    }

}
