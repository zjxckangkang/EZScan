/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ezscaner.uniview.app.ezscan.qr_codescan;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.SymbolSet;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;

import ezscaner.uniview.app.ezscan.R;
import ezscaner.uniview.app.ezscan.activity.QRCodeScanAct;

final class DecodeHandler extends Handler {

    private static final String TAG = DecodeHandler.class.getSimpleName();

    private final QRCodeScanAct activity;

    private MultiFormatReader multiFormatReader;

    private ImageScanner mScanner;

    private boolean running = true;

    DecodeHandler(QRCodeScanAct activity, Map<DecodeHintType, Object> hints) {
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
        this.mScanner = new ImageScanner();
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message message) {
        if (!running) {
            return;
        }
        switch (message.what) {
            case R.id.decode:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case R.id.quit:
                running = false;
                Looper.myLooper().quit();
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it
     * took. For efficiency, reuse the same reader objects from one decodeLocalPicByZXing to
     * the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {

//        KLog.e("decode");
        //构造存储图片的Image
        Image mResult = new Image(width, height, "Y800");//YUV 灰度
        //设置Image的数据资源
        mResult.setData(data);
        //获取扫描结果的代码
        int mResultCode = mScanner.scanImage(mResult);
        //如果代码不为0，表示扫描成功
        Handler handler = activity.getHandler();
        if (mResultCode != 0) {
            SymbolSet Syms = mScanner.getResults();
            // Don't log the barcode contents for security.
            if (handler != null) {
                Message message = Message.obtain(handler,
                        R.id.decode_succeeded_byzbar, Syms);
                Bundle bundle = new Bundle();
                message.setData(bundle);
                message.sendToTarget();
            }

        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            }
        }
//        else {
//            long start = System.currentTimeMillis();
//            Result rawResult = null;
//
//            byte[] rotatedData = new byte[data.length];
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++)
//                    rotatedData[x * height + height - y - 1] = data[x + y * width];
//            }
//            int tmp = width;
//            width = height;
//            height = tmp;
//
//            PlanarYUVLuminanceSource source = activity.getCameraManager()
//                    .buildLuminanceSource(rotatedData, width, height);
//            if (source != null) {
//                BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
//                try {
//                    // 预览界面最终取到的是个bitmap，然后对其进行解码
//                    rawResult = multiFormatReader.decodeWithState(bitmap);
//                } catch (ReaderException re) {
//                    // continue
//                } finally {
//                    multiFormatReader.reset();
//                }
//            }
//
//            Handler handler = activity.getHandler();
//            KLog.e(rawResult);
//            if (rawResult != null) {
//                // Don't log the barcode contents for security.
//                long end = System.currentTimeMillis();
//                Log.d(TAG, "Found barcode in " + (end - start) + " ms");
//                if (handler != null) {
//                    Message message = Message.obtain(handler,
//                            R.id.decode_succeeded, rawResult);
//                    Bundle bundle = new Bundle();
//                    bundleThumbnail(source, bundle);
//                    message.setData(bundle);
//                    message.sendToTarget();
//                }
//            } else {
//                if (handler != null) {
//                    Message message = Message.obtain(handler, R.id.decode_failed);
//                    message.sendToTarget();
//                }
//            }
//        }


    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource source,
                                        Bundle bundle) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height,
                Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
        bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width
                / source.getWidth());
    }

    public void setHints(Hashtable<DecodeHintType, Object> paramHashtable) {
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(paramHashtable);
    }
}
