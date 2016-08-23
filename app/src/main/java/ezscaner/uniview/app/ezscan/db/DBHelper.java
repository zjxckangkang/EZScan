package ezscaner.uniview.app.ezscan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ezscaner.uniview.app.ezscan.bean.Device;
import ezscaner.uniview.app.ezscan.log.KLog;

/**
 * Created by Administrator on 2016/8/18.
 */
public class DBHelper extends SQLiteOpenHelper {
    // 数据库名称
    public static final String DB_NAME = "EZScan.db";

    // 表名称
    public static final String TABLE_NAME = "device";

    // 数据库版本
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        KLog.e("lvkang db");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        KLog.e("lvkang db");

        //创建表SQL语句
        String sql = "create table " + TABLE_NAME + "(" + Device.ID + " integer primary key autoincrement,"
                + Device.REMARKS + " text," + Device.LOCATION + " text," + Device.SN + " text," + Device.ADD_TIME + " text," + Device.UPDATE_TIME + " text)";
        //执行SQL语句
        db.execSQL(sql);
        KLog.e("lvkang db");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        KLog.e("lvkang db");


    }
}
