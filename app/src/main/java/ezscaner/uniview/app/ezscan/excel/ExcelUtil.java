package ezscaner.uniview.app.ezscan.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ezscaner.uniview.app.ezscan.bean.Device;
import ezscaner.uniview.app.ezscan.log.KLog;
import ezscaner.uniview.app.ezscan.utils.DateTimeUtil;
import ezscaner.uniview.app.ezscan.utils.SdCardUtil;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by Administrator on 2016/8/19.
 */
public class ExcelUtil {

    public static String appRootPath = SdCardUtil.getAPPRootDirectory();
    public static String SUFFIX = ".xls";

    private static WritableWorkbook createWorkbook(String path) {
        WritableWorkbook writableWorkbook = null;
        if (appRootPath != null) {

            File file = new File(path);
            try {
                OutputStream outputStream = new FileOutputStream(file);
                writableWorkbook = Workbook.createWorkbook(outputStream);
            } catch (Exception e) {
                KLog.e("Exception ");
            }
        }
        return writableWorkbook;
    }

    public static String writeDevicesToWorkbook(List<Device> devices) {
        String path = appRootPath + File.separator
                + DateTimeUtil.getStrDateAndTimeByMSeconds(System.currentTimeMillis()
                , DateTimeUtil.FORMATSSS) + SUFFIX;

        WritableWorkbook writableWorkbook = createWorkbook(path);
        if (writableWorkbook != null) {
            WritableSheet sheet = writableWorkbook.createSheet("资产信息", 0);

            String[] title = {"id", "资产编号", "位置", "添加时间", "修改时间", "备注"};

            for (int i = 0; i < 6; i++) {
                Label titleLabel = new Label(i, 0, title[i]);
                // 将定义好的单元格添加到工作表中
                try {
                    sheet.addCell(titleLabel);
                } catch (WriteException e) {
                    KLog.e("WriteException ");
                }
            }

            for (int i = 0; i < devices.size(); i++) {
                List<Label> labels = new ArrayList<>();
                labels.add(new Label(0, i + 1, devices.get(i).getId() + ""));
                labels.add(new Label(1, i + 1, devices.get(i).getSn()));
                labels.add(new Label(2, i + 1, devices.get(i).getLocation()));
                long addTime = 0;
                long updateTime = 0;
                try {
                    addTime = Long.parseLong(devices.get(i).getAddTime());
                    updateTime = Long.parseLong(devices.get(i).getUpdateTime());
                } catch (Exception e) {
                    KLog.e("Exception");
                }
                labels.add(new Label(3, i + 1, DateTimeUtil.ts2str(addTime)));
                labels.add(new Label(4, i + 1, DateTimeUtil.ts2str(updateTime)));
                labels.add(new Label(5, i + 1, devices.get(i).getRemarks()));
                for (Label label : labels) {
                    try {
                        sheet.addCell(label);
                    } catch (WriteException e) {
                        KLog.e("WriteException ");
                    }
                }

            }
            try {
                writableWorkbook.write();
                writableWorkbook.close();
            } catch (Exception e) {
                KLog.e("Exception ");
            }


        } else {
            return null;
        }

        return path;
    }


}
