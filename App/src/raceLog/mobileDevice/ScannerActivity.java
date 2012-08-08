package raceLog.mobileDevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity used to scan QR codes
 * 
 * @author Mads
 */
public class ScannerActivity extends Activity {

	Preview preview;
	Button buttonClick;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanner);

		preview = new Preview(this);

		((FrameLayout) findViewById(R.id.preview)).addView(preview);
	}

	@Override
	protected void onResume() {
		super.onResume();
		preview.scan();
	}

	@Override
	protected void onPause() {
		super.onPause();
		preview.pause();
	}

	/**
	 * Checks if the result contains a valid team id and returns to
	 * RaceLogActivity
	 * 
	 * @param result
	 */
	public synchronized void checkResult(final String result) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView textView = (TextView) findViewById(R.id.textView1);
				if (result == null) {
					textView.setText(getString(R.string.scanning));
				} else {
					textView.setText(result + "\n" + getString(R.string.InvalidTeamId));
				}
			}
		});
		//String token[];
		//token = result.split("/");
		//String realResult = token[token.length-1];
		String realResult = result.substring(40, 43);
		int checksum = (Integer.parseInt(realResult.substring(0, 1)) + Integer.parseInt(realResult.substring(1, 2)) + Integer
		.parseInt(realResult.substring(2, 3))) % 10;
		realResult = realResult + Integer.toString(checksum);
		
		if (LogItem.checkTeamId(realResult)) {
			preview.pause();
			LinearLayout background = (LinearLayout) findViewById(R.id.layoutScanner);
			background.setBackgroundColor(getResources().getColor(R.color.Succeed));
			Intent mIntent = new Intent();
			mIntent.putExtra("TeamId", realResult);
			setResult(RESULT_OK, mIntent);
			finish();
		}
	}

}
