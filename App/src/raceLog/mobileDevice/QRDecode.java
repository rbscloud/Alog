package raceLog.mobileDevice;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.androidtest.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

/**
 * Invoke the ZXING API for decoding
 * 
 * @author Mads
 */
public class QRDecode {

	public static void decode(Bitmap bMap) {

		Global.text = null;

		LuminanceSource source = new RGBLuminanceSource(bMap);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Reader reader = new QRCodeReader();
		try {
			Result result = reader.decode(bitmap);
			Global.text = result.getText();
		} catch (NotFoundException e) {

		} catch (ChecksumException e) {
			Log.d("RaceError", "Checksumerror");
		} catch (FormatException e) {
			Log.d("RaceError", "Format exception");
			e.printStackTrace();
		}
	}

	public static class Global {
		public static String text = null;
	}
}
