package ezscaner.uniview.app.ezscan.excel;

import jxl.write.WritableWorkbook;

/**
 * Created by Administrator on 2016/8/19.
 */
public class WritableWorkbookWrapper {
    private WritableWorkbook mWritableWorkbook;
    private String mPath;

    public WritableWorkbookWrapper(String path, WritableWorkbook writableWorkbook) {
        mPath = path;
        mWritableWorkbook = writableWorkbook;
    }

    public WritableWorkbook getWritableWorkbook() {
        return mWritableWorkbook;
    }

    public void setWritableWorkbook(WritableWorkbook writableWorkbook) {
        mWritableWorkbook = writableWorkbook;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }
}
