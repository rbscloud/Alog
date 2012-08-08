package raceLog.mobileDevice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Surface view used on ScannerActivity to show preview. Handles preview
 * callback
 * 
 * @author Mads
 */
class Preview extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder surfaceHolder;
	public Camera camera;
	private boolean scanning = false;

	Preview(Context context) {
		super(context);
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();
			setDisplayOrientation(camera, 90);
		} catch (Exception e) {
			if (camera != null) {
				surfaceDestroyed(holder);
			}
			Camera.open();
		}
		try {
			camera.setPreviewDisplay(holder);
			camera.setPreviewCallback(new PreviewCallback() {
				public void onPreviewFrame(byte[] data, Camera arg1) {
					decodePreview(data);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Decode data preview image data
	 * 
	 * @param data
	 */
	private void decodePreview(byte[] data) {
		if (scanning == true) {
			try {
				Camera.Parameters parameters = camera.getParameters();
				Size size = parameters.getPreviewSize();

				final int[] rgb = decodeYUV420SP(data, size.height, size.width);

				parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				camera.setParameters(parameters);

				Bitmap bmp = Bitmap.createBitmap(rgb, size.width, size.height, Bitmap.Config.ARGB_8888);

				ScannerActivity scannerActivity = (ScannerActivity) getContext();
				try {
					QRDecode.decode(bmp);
					scannerActivity.checkResult(QRDecode.Global.text);
				} catch (Exception e) {
					scannerActivity.checkResult("Scan result: " + QRDecode.Global.text);
				}
			} catch (Exception e) {
				Log.d("Preview", "" + e.getCause() + e.getMessage());
			}
		}
	}

	/**
	 * Converts preview image format to rgb
	 * 
	 * @param yuv420sp preview image
	 * @param width
	 * @param height
	 * @return rgb formatted image
	 */
	public int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
		final int frameSize = width * height;
		int min = 255;
		int max = 0;
		int rgb[] = new int[width * height];
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);

				int greyLevel = (int) (0.3 * r + 0.59 * g + 0.11 * b);
				min = Math.min(min, greyLevel);
				max = Math.max(max, greyLevel);
			}
		}
		return rgb;
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.

		try {
			Camera.Parameters parameters = camera.getParameters();
			List<Size> previewSizes = parameters.getSupportedPreviewSizes();
			Size bestPreviewSize = previewSizes.get(0);
			for (Size s : previewSizes) {
				if (s.width < bestPreviewSize.width
						|| (s.width == bestPreviewSize.width && Math.abs(s.width - s.height) < Math
								.abs(bestPreviewSize.width - bestPreviewSize.height))) {
					bestPreviewSize = s;
				}
			}

			parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
			parameters.set("rotation", 90);
			camera.setParameters(parameters);
		} catch (Exception e) {
			Log.d("Preview-surfaceChanged", e.getMessage());
		}
		scan();
	}

	/**
	 * Starts scanning
	 */
	public void scan() {
		if (camera != null) {
			camera.stopPreview();
			camera.startPreview();
			scanning = true;
		}
	}

	/**
	 * pause scanning
	 */
	public void pause() {
		camera.stopPreview();
		scanning = false;
	}

}