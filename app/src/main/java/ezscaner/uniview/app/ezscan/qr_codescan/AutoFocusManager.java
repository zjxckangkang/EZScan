/*
 * Copyright (C) 2012 ZXing authors
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

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;

import ezscaner.uniview.app.ezscan.utils.AbScreenUtil;

/**
 * 由于对焦不是一次性完成的任务（手抖），而系统提供的对焦仅有Camera.autoFocus()方法，
 * 因此需要一个线程来不断调用Camera.autoFocus()直到用户满意按下快门为止
 */
final class AutoFocusManager implements Camera.AutoFocusCallback {

    private static final String TAG = AutoFocusManager.class.getSimpleName();

    private static final long AUTO_FOCUS_INTERVAL_MS = 2000L;
    private static final int FOCUS_AREA_HALF_WIDTH = 200;
    private static final int FOCUS_AREA_HALF_HEIGHT = 200;
    private static final Collection<String> FOCUS_MODES_CALLING_AF;

    static {
        FOCUS_MODES_CALLING_AF = new ArrayList<>(2);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO);
    }

    private boolean stopped;
    private boolean focusing;
    private final boolean useAutoFocus;
    private final Camera camera;
    private AsyncTask<?, ?, ?> outstandingTask;
    private int centerX;
    private int centerY;
    private Context mContext;

    AutoFocusManager(Context context, Camera camera) {
        this.mContext = context;
        this.camera = camera;
        String currentFocusMode = camera.getParameters().getFocusMode();
        useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
        centerX = AbScreenUtil.getScreenWidth(mContext) / 2;
        centerY = AbScreenUtil.getScreenHeight(mContext) / 2;

        Log.i(TAG, "Current focus mode '" + currentFocusMode + "'; use auto focus? " + useAutoFocus);
        start();
    }

    @Override
    public synchronized void onAutoFocus(boolean success, Camera theCamera) {
        camera.cancelAutoFocus();
        focusing = false;

        if (success) {
            autoFocusAgainLater(true);
        } else {
            autoFocusAgainLater(false);

        }

    }

    private synchronized void autoFocusAgainLater(boolean needWait) {
        if (!stopped && outstandingTask == null) {
            AutoFocusTask newTask = new AutoFocusTask(needWait);
            try {
                newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                outstandingTask = newTask;
            } catch (RejectedExecutionException ree) {
                Log.w(TAG, "Could not request auto focus", ree);
            }
        }
    }

    synchronized void start() {
        if (useAutoFocus) {
            outstandingTask = null;
            if (!stopped && !focusing) {
                try {
//                    camera.cancelAutoFocus();
//                    Camera.Parameters parameters = camera.getParameters();
//
//                    if (parameters.getMaxNumFocusAreas() > 0) {
//                        List<Camera.Area> areas = new ArrayList<>();
//                        areas.add(new Camera.Area(new Rect(centerX - FOCUS_AREA_HALF_WIDTH
//                                , centerY - FOCUS_AREA_HALF_HEIGHT
//                                , centerX + FOCUS_AREA_HALF_WIDTH
//                                , centerY + FOCUS_AREA_HALF_HEIGHT), 600));
//                        parameters.setFocusAreas(areas);
//                        camera.setParameters(parameters);
//                    }

                    camera.cancelAutoFocus();
                    camera.autoFocus(this);
                    focusing = true;
                } catch (RuntimeException re) {
                    // Have heard RuntimeException reported in Android 4.0.x+; continue?
                    Log.w(TAG, "Unexpected exception while focusing", re);
                    // Try again later to keep cycle going
                    autoFocusAgainLater(false);
                }
            }
        }
    }

    private synchronized void cancelOutstandingTask() {
        if (outstandingTask != null) {
            if (outstandingTask.getStatus() != AsyncTask.Status.FINISHED) {
                outstandingTask.cancel(true);
            }
            outstandingTask = null;
        }
    }

    synchronized void stop() {
        stopped = true;
        if (useAutoFocus) {
            cancelOutstandingTask();
            // Doesn't hurt to call this even if not focusing
            try {
                camera.cancelAutoFocus();
            } catch (RuntimeException re) {
                // Have heard RuntimeException reported in Android 4.0.x+; continue?
                Log.w(TAG, "Unexpected exception while cancelling focusing", re);
            }
        }
    }

    private final class AutoFocusTask extends AsyncTask<Object, Object, Object> {
        boolean needWait;

        public AutoFocusTask(boolean needWait) {
            this.needWait = needWait;
        }

        @Override
        protected Object doInBackground(Object... voids) {
            try {
                Thread.sleep(AUTO_FOCUS_INTERVAL_MS);
            } catch (InterruptedException e) {
                // continue
            }
            start();
            return null;
        }
    }

}

