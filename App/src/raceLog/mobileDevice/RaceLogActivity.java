package raceLog.mobileDevice;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity in which logs i entered
 * 
 * @author Mads
 */
public class RaceLogActivity extends Activity {
	private String TAG = getClass().getName();
	private LogList logList;
	private int checkPointId;
	private int raceId;
	private TextView errorLog;
	private EditText editTextTeamId;
	private EditText editTextPoint;
	private Toast toast = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.racelog);
		errorLog = (TextView) findViewById(R.id.textViewErrorLog);
		editTextTeamId = ((EditText) findViewById(R.id.editTextTeamId));
		editTextPoint = ((EditText) findViewById(R.id.editTextPoint));

		clearFields();
		Bundle extras = getIntent().getExtras();
		raceId = extras.getInt("RaceId");
		checkPointId = extras.getInt("CheckPointId");

		ImageButton buttonScan = (ImageButton) findViewById(R.id.imageButtonScan);
		buttonScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent scannerIntent = new Intent(RaceLogActivity.this, ScannerActivity.class);
				startActivityForResult(scannerIntent, 0);
			}
		});

		Button buttonOk = (Button) findViewById(R.id.buttonOk);
		buttonOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int point = Integer.parseInt("0" + editTextPoint.getText().toString());
				String teamId = editTextTeamId.getText().toString();

				try {
					logList.add(new LogItem(raceId, checkPointId, teamId, new Date(), point));
				} catch (Exception e) {
					printError(getString(R.string.CheckPointFailed), true);
				}
				clearFields();
				updateTextViewLog();
			}
		});

		Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearFields();
			}
		});

		editTextTeamId.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				enableOk(LogItem.checkTeamId(editTextTeamId.getText().toString()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		errorLog.setText("");
	}

	/**
	 * Clears point and team id fields
	 */
	private void clearFields() {
		editTextPoint.setText("");
		editTextTeamId.setText("");
		enableOk(false);
	}

	/**
	 * Enables the ok button if a valid team id is scanned/entered
	 * 
	 * @param enable
	 */
	private void enableOk(boolean enable) {
		Button buttonOk = (Button) findViewById(R.id.buttonOk);
		buttonOk.setEnabled(enable);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (logList == null || !logList.getId().equals("" + raceId + checkPointId)) {
			logList = new LogList(this, raceId, checkPointId);
		}
	}

	/**
	 * Prints an error message to the user
	 * 
	 * @param errorMessage Error message to show
	 * @param toast true: An temporary overlay messages is shown
	 */
	public void printError(final String errorMessage, final boolean toast) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				errorLog.setText("Error log:\n" + errorMessage);
				if (toast) {
					showToast(errorMessage);
				}
			}
		});
	}

	public void clearErrorLog() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				errorLog.setText("");

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateTextViewLog();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String teamId = data.getStringExtra("TeamId");
			editTextTeamId.setText("" + teamId);
			Log.d(TAG, editTextTeamId.getText().toString());
		}
	}

	/**
	 * Updates the status text view in the UI thread
	 */
	public void updateTextViewLog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				String committed = "";
				String notCommitted = "";
				for (LogItem l : logList.getList()) {
					if (l.isCommitted()) {
						committed = l.toString() + "\n" + committed;
					} else {
						notCommitted = l.toString() + "\n" + notCommitted;
					}
				}
				Log.d(TAG, "updateTExtViewLog");
				String log = "";
				if (notCommitted.length() > 0) {
					log = getResources().getString(R.string.WaitingForCommit) + ":\n" + notCommitted;
				}
				log += "\n" + getResources().getString(R.string.Committed) + ":\n" + committed;
				try {
					((TextView) findViewById(R.id.textViewLog)).setText(log);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Shows an temporary message on top of background screen.
	 * 
	 * @param message
	 */
	public void showToast(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (toast == null) {
					toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
				} else {
					toast.setText(message);
				}
				toast.show();
			}
		});
	}
}
