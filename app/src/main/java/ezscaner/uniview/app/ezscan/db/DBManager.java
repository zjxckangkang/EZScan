package ezscaner.uniview.app.ezscan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ezscaner.uniview.app.ezscan.application.BaseApplication;
import ezscaner.uniview.app.ezscan.bean.Device;
import ezscaner.uniview.app.ezscan.log.KLog;
import ezscaner.uniview.app.ezscan.utils.AbStrUtil;

/**
 * Created by Administrator on 2016/8/16.
 */
public class DBManager {
    private static DBManager INSTANCE;
    private final static byte[] lock = new byte[0];
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    private DBManager() {
        KLog.e("lvkang db");

        mContext = BaseApplication.getInstance();
        mSQLiteDatabase = new DBHelper(mContext).getWritableDatabase();

    }

    public static DBManager getInstance() {
        if (INSTANCE == null) {
            synchronized (lock) {
                if (INSTANCE == null) {
                    INSTANCE = new DBManager();
                }
            }
        }
        return INSTANCE;
    }

    public long addDevice(Device device) {
        KLog.e();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Device.SN, device.getSn());
        contentValues.put(Device.LOCATION, device.getLocation());
        contentValues.put(Device.REMARKS, device.getRemarks());
        contentValues.put(Device.ADD_TIME, device.getAddTime());
        contentValues.put(Device.UPDATE_TIME, device.getUpdateTime());
        if (mSQLiteDatabase.insert(DBHelper.TABLE_NAME, null, contentValues) >= 0) {
            return 1;
        } else {
            return -1;
        }

    }

    public int updateDeviceByID(Device device) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Device.SN, device.getSn());
        contentValues.put(Device.LOCATION, device.getLocation());
        contentValues.put(Device.REMARKS, device.getRemarks());
//        contentValues.put(Device.ADD_TIME, device.getAddTime());
        contentValues.put(Device.UPDATE_TIME, device.getUpdateTime());
        KLog.e(device.getId() + "");
        return mSQLiteDatabase.update(DBHelper.TABLE_NAME, contentValues, Device.ID + " = ?",
                new String[]{device.getId() + ""});
    }

    public int updateDeviceBySN(Device device) {
        ContentValues contentValues = new ContentValues();
//        contentValues.put(Device.SN, device.getSn());
        contentValues.put(Device.LOCATION, device.getLocation());
        contentValues.put(Device.REMARKS, device.getRemarks());
//        contentValues.put(Device.ADD_TIME, device.getAddTime());
        contentValues.put(Device.UPDATE_TIME, device.getUpdateTime());
        return mSQLiteDatabase.update(DBHelper.TABLE_NAME, contentValues, Device.SN + " = ?",
                new String[]{device.getSn()});
    }

    public List<Device> getDevices() {
        //Cursor对象返回查询结果
        Cursor cursor = mSQLiteDatabase.query(DBHelper.TABLE_NAME, null,
                null, null, null, null, Device.ID + " ASC", null);
        List<Device> devices = new ArrayList<>();
        while (cursor.moveToNext()) {
            Device device = new Device();
            device.setLocation(cursor.getString(cursor.getColumnIndex(Device.LOCATION)));
            device.setRemarks(cursor.getString(cursor.getColumnIndex(Device.REMARKS)));
            device.setSn(cursor.getString(cursor.getColumnIndex(Device.SN)));
            device.setId(cursor.getInt(cursor.getColumnIndex(Device.ID)));
            device.setAddTime(cursor.getString(cursor.getColumnIndex(Device.ADD_TIME)));
            device.setUpdateTime(cursor.getString(cursor.getColumnIndex(Device.UPDATE_TIME)));
            devices.add(device);
        }
        cursor.close();
        return devices;

    }


    public int getDevicesCount() {
        //Cursor对象返回查询结果
        Cursor cursor = mSQLiteDatabase.query(DBHelper.TABLE_NAME, null,
                null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;

    }

    public Device getDevice(String sn) {
        if (AbStrUtil.isEmpty(sn)) {
            KLog.e("sn == null");
            return null;
        }
        KLog.e(sn);
        //Cursor对象返回查询结果
        Cursor cursor = mSQLiteDatabase.query(DBHelper.TABLE_NAME, null,
                "sn = ?", new String[]{sn}, null, null, null, null);
        Device device = null;
        if (cursor.moveToFirst()) {
            device = new Device();
            device.setLocation(cursor.getString(cursor.getColumnIndex(Device.LOCATION)));
            device.setRemarks(cursor.getString(cursor.getColumnIndex(Device.REMARKS)));
            device.setSn(cursor.getString(cursor.getColumnIndex(Device.SN)));
            device.setId(cursor.getInt(cursor.getColumnIndex(Device.ID)));
            device.setAddTime(cursor.getString(cursor.getColumnIndex(Device.ADD_TIME)));
            device.setUpdateTime(cursor.getString(cursor.getColumnIndex(Device.UPDATE_TIME)));
        }
        cursor.close();
        return device;
    }

    public int deleteDevice(int id) {
        return mSQLiteDatabase.delete(DBHelper.TABLE_NAME, "id = ?", new String[]{id + ""});
    }

    public long addDevices(List<Device> devices) {
        long count = 0;
        for (Device device : devices) {
            KLog.e("count", count);
            count += addDevice(device);
        }

        return count;

    }

    public void close() {
        mSQLiteDatabase.close();
    }


}


