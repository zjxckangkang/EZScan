package ezscaner.uniview.app.ezscan.qr_codescan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

import java.util.List;

/**
 * 需要用到的权限 uses-permission android:name="android.permission.FLASHLIGHT"
 * uses-permission android:name="android.permission.CAMERA"
 * 
 * @author zhangxiaowei
 *
 */
public class FlashLamp {
	private static final boolean debug = true;
	private static final String WAKE_LOCK_TAG = "TORCH_WAKE_LOCK";
	static Context mContext;
	private static Camera mCamera;
	private WakeLock wakeLock;
	private boolean lightOn;
	private static boolean previewOn;

	public static FlashLamp getInstance(Context context) {
		mContext = context;
		getCamera();
		startPreview();
		return new FlashLamp();
	}

	private static void startPreview() {
		if (!previewOn && mCamera != null) {
			mCamera.startPreview();
			previewOn = true;
		}
	}

	private static void getCamera() {
		if (mCamera == null) {
			try {
				// mCamera = Camera.open();
				mCamera = CameraManager.getCamera();
			} catch (RuntimeException e) {

			}
		}
	}

	@SuppressLint("ShowToast")
	public void turnLightOn() {
		if (mCamera == null) {
			Toast.makeText(mContext, "Camera not found", Toast.LENGTH_LONG);
			return;
		}

		lightOn = true;
		Camera.Parameters parameters = null;
		try {
			parameters = mCamera.getParameters();
		} catch (RuntimeException e) {
		}
		if (parameters == null) {
			Toast.makeText(mContext, "相机被占用",
					Toast.LENGTH_SHORT).show();
			return;
		}

		List<String> flashModes = parameters.getSupportedFlashModes();
		// Check if camera flash exists
		if (flashModes == null) {
			// Use the screen as a flashlight (next best thing)
			// button.setBackgroundColor(COLOR_WHITE);
			return;
		}

		String flashMode = parameters.getFlashMode();
		if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
			// Turn on the flash
			if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(parameters);
				// button.setBackgroundColor(COLOR_LIGHT);
				startWakeLock();
			} else {
				Toast.makeText(mContext, "Flash mode (torch) not supported",
						Toast.LENGTH_LONG);
				// Use the screen as a flashlight (next best thing)
			}
		}
	}

	private void startWakeLock() {
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) mContext
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					WAKE_LOCK_TAG);
		}

		wakeLock.acquire();
	}

	private void stopWakeLock() {
		if (wakeLock != null) {
			wakeLock.release();
		}
	}

	public void turnLightOff() {
		if (lightOn) {
			// set the background to dark
			lightOn = false;
			if (mCamera == null) {
				return;
			}

			Parameters parameters = mCamera.getParameters();
			if (parameters == null) {
				return;
			}

			List<String> flashModes = parameters.getSupportedFlashModes();
			String flashMode = parameters.getFlashMode();

			// Check if camera flash exists
			if (flashModes == null) {
				return;
			}

			if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
				// Turn off the flash
				if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					mCamera.setParameters(parameters);
					stopWakeLock();
				} else {
				}
			}
		}
	}

	private void stopPreview() {
		if (previewOn && mCamera != null) {
			mCamera.stopPreview();
			previewOn = false;
		}
	}
}
