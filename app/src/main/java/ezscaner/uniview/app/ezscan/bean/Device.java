package ezscaner.uniview.app.ezscan.bean;

import java.io.Serializable;

/**
 * Created by kangkang on 16/6/3.
 */
public class Device implements Serializable {

    public static String ID = "id";
    public static String SN = "sn";
    public static String LOCATION = "location";
    public static String REMARKS = "remarks";
    public static String ADD_TIME = "addTime";
    public static String UPDATE_TIME = "updateTime";


    private int id;
    private String sn;

    private String location;

    private String remarks;

    private String addTime;

    private String updateTime;

    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Device() {
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
