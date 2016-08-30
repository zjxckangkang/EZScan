package ezscaner.uniview.app.ezscan.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Vector;

import ezscaner.uniview.app.ezscan.R;
import ezscaner.uniview.app.ezscan.application.BaseApplication;
import ezscaner.uniview.app.ezscan.constants.KeyConstants;
import ezscaner.uniview.app.ezscan.eventbus.ViewMessage;
import ezscaner.uniview.app.ezscan.log.KLog;
import ezscaner.uniview.app.ezscan.qr_codescan.BeepManager;
import ezscaner.uniview.app.ezscan.qr_codescan.CameraManager;
import ezscaner.uniview.app.ezscan.qr_codescan.CaptureActivityHandler;
import ezscaner.uniview.app.ezscan.qr_codescan.DecodeThread;
import ezscaner.uniview.app.ezscan.qr_codescan.FinishListener;
import ezscaner.uniview.app.ezscan.qr_codescan.InactivityTimer;
import ezscaner.uniview.app.ezscan.qr_codescan.ViewfinderView;
import ezscaner.uniview.app.ezscan.utils.AbStrUtil;
import ezscaner.uniview.app.ezscan.utils.BitmapUtil;
import ezscaner.uniview.app.ezscan.utils.StringUtils;
import ezscaner.uniview.app.ezscan.utils.ToastUtil;

/**
 * 条码二维码扫描功能实现
 */
@EActivity(R.layout.act_qr_code)
public class QRCodeScanAct extends BaseAct implements SurfaceHolder.Callback {
    private static final String TAG = QRCodeScanAct.class.getSimpleName();
    private static final boolean debug = true;
    @ViewById(R.id.aqc_Flash)
    CheckBox mCheckBox;
    private boolean hasSurface;

    @ViewById(R.id.preview_view)
    SurfaceView surfaceView;

    @ViewById
    TextView tvSn;

    // public SharedPreferences mSharedPreferences;// 存储二维码条形码选择的状态
    // public static String currentState;// 条形码二维码选择状态
    private String characterSet;

    private ViewfinderView viewfinderView;
    private BeepManager beepManager;// 声音震动管理器。如果扫描成功后可以播放一段音频，也可以震动提醒，可以通过配置来决定扫描成功后的行为。
    private SurfaceHolder surfaceHolder;
    // private TextView statusView;
    // private TextView scanTextView;
    // private View resultView;
    // private ImageView onecode;
    // private ImageView qrcode;

    /**
     * 活动监控器，用于省电，如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。
     * 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
     */
    private InactivityTimer inactivityTimer;
    private CameraManager cameraManager;
    private Vector<BarcodeFormat> decodeFormats;// 编码格式
    private CaptureActivityHandler mHandler;// 解码线程

    private int mWith;

    private static int COME_FROM_DEVICELIST = 1;
    private static int COME_FROM_ADDDEVICE = 2;
    //纪律扫一扫来自哪个页面
    private int iComeFrom = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        KLog.e();
        Intent intent = getIntent();
        if (intent != null) {
            String str = intent.getStringExtra(KeyConstants.COME_FROM);
            if (!StringUtils.isEmpty(str)) {
                if (str.equalsIgnoreCase("come_from_device_list")) {
                    iComeFrom = COME_FROM_DEVICELIST;
                } else if (str.equalsIgnoreCase("come_from_add_device")) {
                    iComeFrom = COME_FROM_ADDDEVICE;
                }
            }
        }

        initSetting();

        initComponent();
        initEvent();

    }

    @Click(R.id.aqcBtnBack)
    void clickBack() {
        finish();
    }

    @CheckedChange(R.id.aqc_Flash)
    void clickFlash(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            cameraManager.openF();
        } else {
            cameraManager.stopF();
        }

    }

    @Click(R.id.aqc_QRCode)
    void clickFromImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        KLog.i("clickFromImage");
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        KLog.e(resultCode + "");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    tvSn.setText("");
                    String picturePath = getRealFilePath(this, data.getData());
                    // 获得待解析的图片
                    //Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    Bitmap bitmap = getBitmap(picturePath, 1200, 1200);//图像压缩
//                    Result resultString = decodeLocalPicByZXing(bitmap);
//                    KLog.e(resultString);


                    KLog.e("output width", bitmap.getWidth());
                    KLog.e("output height", bitmap.getHeight());

                    SymbolSet symbols = decodeLocalPicByZBar(bitmap);

                    if (symbols != null) {
                        handleDecode(symbols);
                    } else {
                        ToastUtil.shortShow(getApplicationContext(), getString(R.string.qrcode_unknow));
                    }

                    bitmap.recycle();


                    break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context contxt
     * @param uri     uri
     * @return the file path or null
     */

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    /**
     * 根据路径获得图片并压缩，返回bitmap
     *
     * @param filePath 路径
     */
    public static Bitmap getBitmap(String path, int maxWidth, int maxHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;

        KLog.e("input width", width);
        KLog.e("input height", height);

        int outWidth = 0;
        int outHeight = 0;
        if (width > maxWidth && height > maxHeight) {
            outWidth = maxWidth;
            outHeight = height * outWidth / width;
            int tempOutHeight = outHeight;
            if (outHeight > maxHeight) {
                outHeight = maxHeight;
                outWidth = outWidth * maxHeight / tempOutHeight;
            }

        } else if (width > maxWidth) {
            outWidth = maxWidth;
            outHeight = height * outWidth / width;

        } else if (height > maxHeight) {
            outHeight = maxHeight;
            outWidth = width * outHeight / height;
        } else {
            outWidth = width;
            outHeight = height;

        }
        opts.inJustDecodeBounds = false;

        opts.outHeight = outHeight;
        opts.outWidth = outWidth;

        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), outWidth, outHeight, true);
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap
     *
     * @param filePath 路径
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);


        //宽度小于1080px则不压缩，samsung 1080上图片压缩以后识别不了
        if (options.outWidth <= 1080) {
            return BitmapFactory.decodeFile(filePath);
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值
     *
     * @param options   原始图参数
     * @param reqWidth  期望宽度
     * @param reqHeight 期望高度
     * @return 缩放值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    /**
     * 解析QR图内容
     *
     * @param bm 二维码
     * @return com.google.zxing.Result
     */
    public Result decodeLocalPicByZXing(Bitmap bm) {
        Hashtable<DecodeHintType, String> hints;
        BinaryBitmap bb;
        QRCodeReader reader;

        hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        reader = new QRCodeReader();

        int[] data = new int[bm.getHeight() * bm.getWidth()];
        bm.getPixels(data, 0, bm.getWidth(), 0, 0, bm.getWidth(),
                bm.getHeight());

        RGBLuminanceSource source = new RGBLuminanceSource(bm.getWidth(),
                bm.getHeight(), data);

        bb = new BinaryBitmap(new HybridBinarizer(source));
        try {
            return reader.decode(bb, hints);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public SymbolSet decodeLocalPicByZBar(Bitmap bm) {

        byte[] data = BitmapUtil.getYUV420sp(bm.getWidth(), bm.getHeight(), bm);
        int width = bm.getWidth();
        int height = bm.getHeight();


        //构造存储图片的Image
        Image mResult = new Image(width, height, "Y800");//第三个参数不知道是干嘛的
        //设置Image的数据资源
        mResult.setData(data);
        ImageScanner imageScanner = new ImageScanner();

        int mResultCode = imageScanner.scanImage(mResult);

        //获取扫描结果的代码
        //如果代码不为0，表示扫描成功
        if (mResultCode != 0) {
            return imageScanner.getResults();
        }

        return null;
    }

    /**
     * 初始化窗口设置
     */
    private void initSetting() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕处于点亮状态
        // window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 竖屏
    }

    /**
     * 初始化功能组件
     */
    private void initComponent() {
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this, R.raw.beep);
        cameraManager = new CameraManager(getApplication());
    }


    /**
     * 初始化点击切换扫描类型事件
     */
    private void initEvent() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mWith = displayMetrics.widthPixels * 3 / 5;
    }

    /**
     * 初始设置扫描类型（最后一次使用类型）
     */
    private void setScanType() {
        viewfinderView.setVisibility(View.VISIBLE);
        qrcodeSetting();
    }

    /**
     * 主要对相机进行初始化工作
     */
    @Override
    protected void onResume() {
        super.onResume();
        inactivityTimer.onActivity();
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);
        surfaceHolder = surfaceView.getHolder();
        setScanType();
        resetStatusView();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            // 如果SurfaceView已经渲染完毕，会回调surfaceCreated，在surfaceCreated中调用initCamera()
            surfaceHolder.addCallback(this);
        }
        // 加载声音配置，其实在BeemManager的构造器中也会调用该方法，即在onCreate的时候会调用一次
        beepManager.updatePrefs(R.raw.beep);
        // 恢复活动监控器
        inactivityTimer.onResume();
        // mFlashLamp=FlashLamp.getInstance(mContext);
        // mFlashLamp.turnLightOn();

    }

    @AfterViews
    void init() {
        tvSn.setText("");
    }

    /**
     * 展示状态视图和扫描窗口，隐藏结果视图
     */
    private void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * 初始化摄像头。打开摄像头，检查摄像头是否被开启及是否被占用
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG,
                    "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the mHandler starts the preview, which can also throw a
            // RuntimeException.
            if (mHandler == null) {
                mHandler = new CaptureActivityHandler(this, decodeFormats,
                        characterSet, cameraManager);
            }
            // decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 若摄像头被占用或者摄像头有问题则跳出提示对话框
     */
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(null);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    /**
     * 暂停活动监控器,关闭摄像头
     */
    @Override
    protected void onPause() {
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        // 暂停活动监控器
        inactivityTimer.onPause();
        // 关闭摄像头
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    /**
     * 停止活动监控器,保存最后选中的扫描类型
     */
    @Override
    protected void onDestroy() {
        // 停止活动监控器
        inactivityTimer.shutdown();
        // saveScanTypeToSp();
        super.onDestroy();
        cameraManager.stopF();
        // mFlashLamp.turnLightOff();
    }


    /**
     * 获取扫描结果
     *
     * @param rawResult
     * @param barcode
     * @param scaleFactor
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();

        String text = rawResult.getText();
        KLog.e(text);
        EventBus.getDefault().post(new ViewMessage(R.id.scan_result, text));
//        EventBusUtil.getInstance().post();

        finish();
    }

    public void handleDecode(SymbolSet symbols) {
        inactivityTimer.onActivity();
        if (symbols != null && symbols.size() > 0) {
            for (Symbol symbol : symbols) {
                String text = symbol.getData();
                KLog.e(text);
                if (!AbStrUtil.isEmpty(text)) {
                    EventBus.getDefault().post(new ViewMessage(R.id.scan_result, text));
                    tvSn.setText(text);

                    if (!BaseApplication.getInstance().isContinueScan()||BaseApplication.isEdit) {
                        finish();
                    } else {
                        KLog.e("request");
                        //比较容易出现频繁扫描成功的情况
                        restartPreviewAfterDelay(300);
                    }
                    return;
                }

            }

        }

//        for (Symbol symbol : symbols) {
//            if (symbol.getType() == Symbol.CODE128 || symbol.getType() == Symbol.QRCODE ||
//                    symbol.getType() == Symbol.CODABAR || symbol.getType() == Symbol.ISBN10 ||
//                    symbol.getType() == Symbol.ISBN13 || symbol.getType() == Symbol.DATABAR ||
//                    symbol.getType() == Symbol.DATABAR_EXP || symbol.getType() == Symbol.I25)
//
//            {


//            }
//        }

    }


    /**
     * 获取读取的字符串
     *
     * @param text
     * @return
     */
    private String getEquipmentId(String text) {
        int d = text.indexOf("?") + 1;
        String substring = text.substring(d, text.length());
        return substring;
    }


    /**
     * 显示扫描结果
     *
     * @param rawResult
     * @param barcode
     */
    @SuppressWarnings("unused")
    private void handleDecodeInternally(Result rawResult, Bitmap barcode) {
        // statusView.setVisibility(View.GONE);
        viewfinderView.setVisibility(View.GONE);

    }


    private void qrcodeSetting() {
        decodeFormats = new Vector<BarcodeFormat>(7);
        decodeFormats.clear();
        decodeFormats.add(BarcodeFormat.QR_CODE);
        decodeFormats.add(BarcodeFormat.DATA_MATRIX);
        decodeFormats.addAll(DecodeThread.ONE_D_FORMATS);
        // scanTextView.setText(R.string.scan_qr);
        if (null != mHandler) {
            mHandler.setDecodeFormats(decodeFormats);
        }

        viewfinderView.refreshDrawableState();
        cameraManager.setManualFramingRect(mWith, mWith);
        viewfinderView.refreshDrawableState();

    }

    private void onecodeSetting() {
        decodeFormats = new Vector<BarcodeFormat>(7);
        decodeFormats.clear();
        decodeFormats.addAll(DecodeThread.ONE_D_FORMATS);
        // scanTextView.setText(R.string.scan_one);
        if (null != mHandler) {
            mHandler.setDecodeFormats(decodeFormats);
        }

        viewfinderView.refreshDrawableState();
        cameraManager.setManualFramingRect(mWith, mWith);
        viewfinderView.refreshDrawableState();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG,
                    "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    /**
     * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
     */
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    /**
     * 在经过一段延迟后重置相机以进行下一次扫描。 成功扫描过后可调用此方法立刻准备进行下次扫描
     *
     * @param delayMS
     */
    public void restartPreviewAfterDelay(long delayMS) {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
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
