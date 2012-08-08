package raceLog.mobileDevice;

import java.net.ConnectException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Connects to the server and show a list of check point, which the user can
 * select
 * 
 * @author Mads
 */
public class SelectCheckPointActivity extends Activity {

	@Override
	protected void onStart() {
		super.onStart();

		ListView listView = (ListView) findViewById(R.id.listViewCheckPoints);

		TextView textViewSelectCheckPoint = (TextView) findViewById(R.id.textViewSelectCheckPoint);

		String checkPointListXML;
		try {
			checkPointListXML = HttpInterface.getCheckPointRaceListXML(this, getIntent().getExtras());
			try {

				final SimpleAdapter adapter = XMLAdapter.getAdapter(this, checkPointListXML, "CheckPoint",
						new String[] { "ID", "name" }, R.layout.chekpointlistelement, new String[] { "ID", "name" },
						new int[] { R.id.checkPointElement_id, R.id.checkPointElement_Name });
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						Bundle extras = getIntent().getExtras();
						HashMap<String, String> checkPoint = (HashMap<String, String>) adapter.getItem(position);
						int checkPointId = Integer.parseInt(checkPoint.get("ID"));
						extras.putInt("CheckPointId", checkPointId);

						Intent raceLogIntent = new Intent(SelectCheckPointActivity.this, RaceLogActivity.class);
						raceLogIntent.putExtras(extras);
						startActivity(raceLogIntent);
					}
				});
			} catch (Exception e) {
				textViewSelectCheckPoint.setText(getString(R.string.InvalidServerResponse) + "\n[" + checkPointListXML
						+ "]");
			}

		} catch (ConnectException e) {
			textViewSelectCheckPoint.setText(getString(R.string.CannotConnectToServer) + "\n[" + e.getMessage() + "]");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectcheckpoint);
	}

}