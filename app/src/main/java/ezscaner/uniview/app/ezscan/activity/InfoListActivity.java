package ezscaner.uniview.app.ezscan.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import ezscaner.uniview.app.ezscan.R;
import ezscaner.uniview.app.ezscan.adapter.InfoListAdapter;
import ezscaner.uniview.app.ezscan.application.BaseApplication;
import ezscaner.uniview.app.ezscan.bean.Device;
import ezscaner.uniview.app.ezscan.db.DBManager;
import ezscaner.uniview.app.ezscan.excel.ExcelUtil;
import ezscaner.uniview.app.ezscan.log.KLog;
import ezscaner.uniview.app.ezscan.utils.DialogUtil;
import ezscaner.uniview.app.ezscan.utils.FileUtil;
import ezscaner.uniview.app.ezscan.utils.ListUtils;
import ezscaner.uniview.app.ezscan.utils.SendEmailUtil;
import ezscaner.uniview.app.ezscan.utils.SharePreferenceUtil;
import ezscaner.uniview.app.ezscan.utils.ToastUtil;
import ezscaner.uniview.app.ezscan.view.EditTextWithDelete;

@EActivity(R.layout.activity_info_list)
public class InfoListActivity extends BaseAct {

    public static boolean isEditMode = false;

    @ViewById
    ListView lvInfo;


    @ViewById
    View llBack;

    @ViewById
    View tvEmpty;


    @ViewById
    View ivEdit;

    @ViewById
    View bottomBar;

    Handler mHandler = new Handler();

    private InfoListAdapter mInfoListAdapter;
    private List<Device> mDevices;
    private RelativeLayout etMailView;
    private EditTextWithDelete etMailAddr;

    @AfterViews
    void initView() {
        mDevices = new ArrayList<>();
        mInfoListAdapter = new InfoListAdapter(mDevices, this);
        lvInfo.setAdapter(mInfoListAdapter);
        lvInfo.setEmptyView(tvEmpty);
        mInfoListAdapter.setOnCheckChangedListener(new InfoListAdapter.OnCheckChangedListener() {
            @Override
            public void onClick() {
                boolean isAllCheck = isAllCheck(mDevices);
                if (!isAllCheck) {
                    ibMultiSelect.setBackgroundResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                } else {
                    ibMultiSelect.setBackgroundResource(R.drawable.ic_check_box_black_24dp);

                }
            }
        });
        lvInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        InfoListActivity.this);
                String[] items = getResources().getStringArray(
                        R.array.deviceMenu);
                builder.setItems(items, new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(BaseApplication.getInstance(), MainActivity_.class);
                                intent.putExtra(MODIFY, mDevices.get(position));
                                startActivity(intent);
                                break;
                            case 1:
                                DialogUtil.showDialog(InfoListActivity.this, "确定删除？", "删除", new MaterialDialog.SingleButtonCallback() {

                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        int result = 0;

                                        result += DBManager.getInstance().deleteDevice(mDevices.get(position).getId());

                                        if (result > 0) {
                                            ToastUtil.longShow(InfoListActivity.this, "删除成功");

                                        } else {
                                            ToastUtil.longShow(InfoListActivity.this, "删除失败");

                                        }

                                        notifyAdapter();
                                    }

                                }, "取消", null);
                                break;

                        }
                    }
                });
                builder.create().show();
            }
        });
        notifyAdapter();
        setReadMode();


//        updateData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyAdapter();
    }


    private void notifyAdapter() {
        mDevices.clear();
        mDevices.addAll(DBManager.getInstance().getDevices());
        mInfoListAdapter.notifyDataSetChanged();
        checkEmpty();

    }

    private void setEditMode() {
        ivEdit.setBackgroundResource(R.drawable.ic_call_missed_outgoing_white_48dp);
        bottomBar.setVisibility(View.VISIBLE);
        mInfoListAdapter.notifyDataSetChanged();

    }

    private void setReadMode() {
        if (!ListUtils.isListEmpty(mDevices)) {
            for (Device device : mDevices) {
                device.setChecked(false);
            }
        }
        ibMultiSelect.setBackgroundResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        ivEdit.setBackgroundResource(R.drawable.ic_mode_edit_white_48dp);
        bottomBar.setVisibility(View.GONE);
        mInfoListAdapter.notifyDataSetChanged();

    }

    @ViewById
    ImageButton ibMultiSelect;

    @Click(R.id.ibMultiSelect)
    void allCheck() {

        boolean isAllCheck = isAllCheck(mDevices);
        for (Device device : mDevices) {
            device.setChecked(!isAllCheck);
        }
        if (!isAllCheck) {
            ibMultiSelect.setBackgroundResource(R.drawable.ic_check_box_black_24dp);
        } else {
            ibMultiSelect.setBackgroundResource(R.drawable.ic_check_box_outline_blank_black_24dp);

        }
        mInfoListAdapter.notifyDataSetChanged();
    }


    @Click(R.id.llEdit)
    void edit() {
        if (isEditMode) {
            setReadMode();
            isEditMode = !isEditMode;

        } else {
            if (!ListUtils.isListEmpty(mDevices)) {
                setEditMode();
                isEditMode = !isEditMode;
            } else {
                ToastUtil.longShow(this, "请先添加资产");
            }

        }


    }

    private boolean isAllCheck(List<Device> devices) {

        if (ListUtils.isListEmpty(devices)) {
            return true;
        } else {
            for (Device device : devices) {
                if (!device.isChecked()) {
                    return false;
                }
            }
        }
        return true;
    }


    private boolean isCheckEmpty(List<Device> devices) {
        boolean isEmpty = true;

        if (ListUtils.isListEmpty(devices)) {
            return true;
        } else {
            for (Device device : devices) {
                if (device.isChecked()) {
                    isEmpty = false;
                }
            }
        }
        return isEmpty;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isEditMode = false;
    }

    @Click(R.id.ibShare)
    void share() {

        final List<Device> devices = mInfoListAdapter.getDevices();
        if (isCheckEmpty(devices)) {
            ToastUtil.longShow(this, "请选择资产");
            return;
        }

        etMailView = (RelativeLayout) View.inflate(this, R.layout.et_mail, null);
        etMailAddr = (EditTextWithDelete) etMailView.findViewById(R.id.etMailAddr);
        etMailAddr.setTextSize(16);
        String lastUsedMailAddr = SharePreferenceUtil.getInstance().get(LAST_USED_MAIL_ADDR, "");
        etMailAddr.setText(lastUsedMailAddr);
        etMailAddr.setAfterTextChangedListener(new EditTextWithDelete.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                SharePreferenceUtil.getInstance().write(LAST_USED_MAIL_ADDR, s.toString());

            }
        });


        DialogUtil.showDialog(this, "请输入邮箱地址", "发送", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                mProgressDialog = DialogUtil.showProgressDialog(InfoListActivity.this, "请稍候", ProgressDialog.STYLE_HORIZONTAL);

                sendExcel(etMailAddr.getText().toString());
            }
        }, "取消", null, etMailView);
    }

    @Click(R.id.ibDelete)
    void delete() {
        final List<Device> devices = mInfoListAdapter.getDevices();
        if (isCheckEmpty(devices)) {
            ToastUtil.longShow(this, "请选择资产");
            return;
        }

        DialogUtil.showDialog(this, "确定删除？", "删除", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                showProgressDialog();
                int result = 0;

                for (Device device : devices) {
                    if (device.isChecked()) {
                        result += DBManager.getInstance().deleteDevice(device.getId());
                    }
                }

                if (result > 0) {
                    ToastUtil.longShow(InfoListActivity.this, "删除成功");

                } else {
                    ToastUtil.longShow(InfoListActivity.this, "删除失败");

                }

                notifyAdapter();
                dismissProgressDialog();
            }
        }, "取消", null);


    }

    private void checkEmpty() {
        if (ListUtils.isListEmpty(mDevices)) {
            ibMultiSelect.setBackgroundResource(R.drawable.ic_check_box_outline_blank_black_24dp);
            setReadMode();
        }

    }


    private ProgressDialog mProgressDialog = null;

    private void clearCache(String path){
        FileUtil.deleteFile(path);
    }

    @Background
    void sendExcel(final String mailAddr) {
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(2);
        ToastUtil.longShow(InfoListActivity.this, mHandler, "正在生成Excel");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Device> devices = new ArrayList<>();
        for (Device device : mDevices) {
            if (device.isChecked()) {
                devices.add(device);
            }
        }

        final String path = ExcelUtil.writeDevicesToWorkbook(devices);
        KLog.e(path);
        if (path == null) {
            ToastUtil.longShow(this, "生成失败");
            DialogUtil.dismissDialog(mProgressDialog);
            return;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SendEmailUtil sendEmailUtil = new SendEmailUtil();
        sendEmailUtil.sendMail(this, path, new String[]{mailAddr}, new SendEmailUtil.MailListener() {
            @Override
            public void startSend() {
                mProgressDialog.setProgress(1);
                ToastUtil.longShow(InfoListActivity.this, mHandler, "正在发送");

            }

            @Override
            public void succeedSend() {
                mProgressDialog.setProgress(2);
                ToastUtil.longShow(InfoListActivity.this, mHandler, "发送成功");

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failSend() {
                ToastUtil.longShow(InfoListActivity.this, mHandler, "发送失败");

            }

            @Override
            public void timeOut() {
                ToastUtil.longShow(InfoListActivity.this, mHandler, "发送超时");

            }

            @Override
            public void onFinish() {
                clearCache(path);
                DialogUtil.dismissDialog(mProgressDialog);
            }
        });
    }

    @Click(R.id.llBack)
    void back() {
        finish();
    }

//    @Override
//    public void onEventMainThread(APIMessage apiMessage) {
//
//    }
//
//    @Override
//    public void onEventMainThread(ViewMessage viewMessage) {
//
//    }
}
