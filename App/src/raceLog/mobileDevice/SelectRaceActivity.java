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
 * Connects to the server and shows a list of races, that the user can select
 * 
 * @author Mads
 */
public class SelectRaceActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectrace);

		ListView listView = (ListView) findViewById(R.id.listViewRaceList);

		Bundle extras = getIntent().getExtras();

		TextView textViewSelectRace = (TextView) findViewById(R.id.textViewSelectRace);
		String RaceListXML;
		try {
			RaceListXML = HttpInterface.getRaceListXML(this, extras);
			textViewSelectRace.setText(getString(R.string.SelectRace));
			try {

				final SimpleAdapter adapter = XMLAdapter.getAdapter(this, RaceListXML, "Race", new String[] { "ID",
						"name", "startTime", "endTime" }, R.layout.racelistelement, new String[] { "name", "startTime",
						"endTime" }, new int[] { R.id.raceElement_Name, R.id.raceElement_startTime,
						R.id.raceElement_endTime });
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {
					@SuppressWarnings("unchecked")
					@Override
					public void onItemClick(AdapterView<?> parent, View v, int position, long arg3) {
						HashMap<String, String> race = (HashMap<String, String>) adapter.getItem(position);
						int raceId = Integer.parseInt(race.get("ID"));
						Bundle extras = getIntent().getExtras();
						extras.putInt("RaceId", raceId);
						Intent selectCheckPointIntent = new Intent(SelectRaceActivity.this,
								SelectCheckPointActivity.class);
						selectCheckPointIntent.putExtras(extras);
						startActivity(selectCheckPointIntent);
					}
				});
			} catch (Exception e) {
				textViewSelectRace.setText(getString(R.string.InvalidServerResponse) + "\n[" + RaceListXML + "]");
			}

		} catch (ConnectException e) {
			textViewSelectRace.setText(getString(R.string.CannotConnectToServer) + "\n[" + e.getMessage() + "]");
		}
	}
}