package ezscaner.uniview.app.ezscan.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import ezscaner.uniview.app.ezscan.R;
import ezscaner.uniview.app.ezscan.activity.InfoListActivity;
import ezscaner.uniview.app.ezscan.bean.Device;
import ezscaner.uniview.app.ezscan.log.KLog;
import ezscaner.uniview.app.ezscan.utils.AbStrUtil;
import ezscaner.uniview.app.ezscan.utils.DateTimeUtil;

/**
 * Created by kangkang on 16/6/4.
 */
public class InfoListAdapter extends BaseAdapter {
    private List<Device> mDevices;
    private Context mContext;
    private OnCheckChangedListener mOnCheckChangedListener;

    public void setOnCheckChangedListener(OnCheckChangedListener onCheckChangedListener) {
        mOnCheckChangedListener = onCheckChangedListener;
    }

    public InfoListAdapter(List<Device> devices, Context context) {
        mDevices = devices;
        mContext = context;
    }

    public List<Device> getDevices() {
        return mDevices;
    }

    @Override
    public int getCount() {
        if (mDevices == null) {
            return 0;
        }
        return mDevices.size();
    }

    @Override
    public Device getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_info_list, null);
            holder = new Holder();
            holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            holder.tvRemarks = (TextView) convertView.findViewById(R.id.tvRemarks);
            holder.tvSN = (TextView) convertView.findViewById(R.id.tvSN);
            holder.tvAddTime = (TextView) convertView.findViewById(R.id.tvAddTime);
            holder.tvUpdateTime = (TextView) convertView.findViewById(R.id.tvUpdateTime);
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }
        final Device device = getItem(position);
        holder.tvAddress.setText("位置  " + device.getLocation());
        holder.tvSN.setText("编号  " + device.getSn());
        try {
            long addTime = Long.parseLong(device.getAddTime());
            holder.tvAddTime.setText("时间  " + DateTimeUtil.ts2str(addTime));

        } catch (Exception e) {
            KLog.e("Exception");
            holder.tvAddTime.setText("");
        }
//        holder.tvUpdateTime.setText("修改于:" + device.getUpdateTime());
        holder.tvUpdateTime.setVisibility(View.GONE);

        if (AbStrUtil.isEmpty(device.getRemarks())) {
            holder.tvRemarks.setVisibility(View.GONE);
        } else {
            holder.tvRemarks.setVisibility(View.VISIBLE);
            holder.tvRemarks.setText("备注  " + device.getRemarks());

        }

        holder.cb.setOnCheckedChangeListener(null);
        holder.cb.setChecked(device.isChecked());
        if (InfoListActivity.isEditMode) {
            holder.cb.setVisibility(View.VISIBLE);
            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    device.setChecked(isChecked);
                    if (mOnCheckChangedListener != null) {
                        mOnCheckChangedListener.onClick();
                    }
                }
            });
        } else {
            holder.cb.setVisibility(View.GONE);

        }

        return convertView;
    }

    public interface OnCheckChangedListener {
        void onClick();
    }

    private class Holder {
        private TextView tvSN;
        private TextView tvAddress;
        private TextView tvRemarks;
        private TextView tvAddTime;
        private TextView tvUpdateTime;
        private CheckBox cb;
    }
}
