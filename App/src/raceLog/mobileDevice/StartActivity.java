package raceLog.mobileDevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Start up activity requiring username and password
 * 
 * @author Mads
 */
public class StartActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		// Set default username and password for test demonstration
		EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		editTextUsername.setText("username");
		EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextPassword.setText("password");

		Button buttonGetRaceList = (Button) findViewById(R.id.buttonGetRaceList);
		buttonGetRaceList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Bundle username and password and pass to SelectRaceActivity
				Intent selectRaceIntent = new Intent(StartActivity.this, SelectRaceActivity.class);
				Bundle extras = new Bundle();
				extras.putString("Username", ((EditText) findViewById(R.id.editTextUsername)).getText().toString());
				extras.putString("Password", ((EditText) findViewById(R.id.editTextPassword)).getText().toString());
				selectRaceIntent.putExtras(extras);
				startActivity(selectRaceIntent);
			}
		});
	}
}