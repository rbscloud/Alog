package raceLog.mobileDevice;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;

/**
 * Holds a list of LogItems and a CommitList and handles checkpoint generation as well as restoring from checkpoint 
 * @author Mads
 *
 */
public class LogList {

	private ArrayList<LogItem> logList = new ArrayList<LogItem>();
	private Context context;
	private File nextCheckPointFile;
	private File checkPointFile;
	private CommitList commitList;
	private RaceLogActivity raceLogActivity;
	private String ids;

	public LogList(RaceLogActivity raceLogActivity, int raceId, int checkPointId) {
		this.context = raceLogActivity;
		this.raceLogActivity = raceLogActivity;
		this.ids = ""+raceId+checkPointId;
		commitList = new CommitList(raceLogActivity, this);
		String checkPointFileName = "CheckPoint_" + raceId + "_" + checkPointId + ".csv";
		checkPointFile = new File(context.getExternalFilesDir(null), checkPointFileName);
		nextCheckPointFile = new File(context.getExternalFilesDir(null), "Next" + checkPointFileName);
		
		checkExternalStorage();
		//Try to load list from a stored checkpoint
		try {
			loadFromCheckPoint();
		} catch (Exception e) {
			raceLogActivity.printError(context.getString(R.string.CheckPointLoadFailed) + "\n[" + e.getMessage() + "]", true);
		}
	}
	
	/**
	 * Returns the raceid and checkpoint id of this list
	 * @return
	 */
	public String getId(){
		return ids;
	}

	/**
	 * Load list from checkpoint
	 * @throws Exception
	 */
	private void loadFromCheckPoint() throws Exception {
		if (checkPointFile != null && checkPointFile.exists()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(checkPointFile)));
			String line;
			while ((line = reader.readLine()) != null) {
				add(new LogItem(line));
			}
		}
	}

	/**
	 * - Adds a LogItem object to this list
	 * - Makes a checkpoint
	 * - adds the LogItem object to CommitList
	 * @param logItem
	 * @throws Exception
	 */
	public void add(LogItem logItem) throws Exception {
		logList.add(logItem);
		makeCheckPoint();
		if(!logItem.isCommitted()){
			commitList.add(logItem);
		}
	}

	/**
	 * Makes a checkpoint in the following steps
	 * - check that external storage is available
	 * - save list to NextCheckPoint[RaceId]_[CheckPointId].csv
	 * - load and verify saved list
	 * - rename file to overwrite previous checkpoint
	 * @throws Exception
	 */
	public synchronized void makeCheckPoint() throws Exception {
		checkExternalStorage();
		saveListToFile();
		try {
			verifyNextCheckPointFile();
			updateCheckPointFile();
		} catch (Exception e) {
			raceLogActivity.printError(context.getString(R.string.CheckPointSaveFailed) + "\n[" + e.getMessage() + "]", true);
		}
	}

	/**
	 * Saves the current list to NextCheckPoint[RaceId]_[CheckPointId].csv
	 */
	private synchronized void saveListToFile() {
		try {
			OutputStream os = new FileOutputStream(nextCheckPointFile);
			BufferedOutputStream bof = new BufferedOutputStream(os);
			bof.write(this.toCSV().getBytes());
			bof.close();
			os.close();
		} catch (IOException e) {
			raceLogActivity.printError(context.getString(R.string.CheckPointSaveFailed) + "\n[" + e.getMessage() + "]", true);
		}
	}

	/**
	 * Load save list and compare it to the current list 
	 * @throws Exception Thrown if saved list does not match current list
	 */
	private synchronized void verifyNextCheckPointFile() throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(nextCheckPointFile)));
		String line;
		int itemNr = 0;
		while ((line = reader.readLine()) != null) {
			LogItem logItem = logList.get(itemNr++);
			String[] e = line.split(",");
			if (!e[0].equals("" + logItem.getRaceId()) || !e[1].equals("" + logItem.getCheckpointId())
					|| !e[2].equals("" + logItem.getTeamId())
					|| !e[3].equals(LogItem.dateFormat.format(logItem.getTime()))
					|| !e[4].equals("" + logItem.getPoint())) {
				throw new Exception("Log list saved incorrectly");
			}
		}
		reader.close();
	}

	/**
	 * Rename temporary checkpoint file to overwrite previous checkpoint
	 */
	private synchronized void updateCheckPointFile() {
		if (checkPointFile != null) {
			checkPointFile.delete();
		}
		nextCheckPointFile.renameTo(checkPointFile);
	}

	/**
	 * Return the current list as comma separated values
	 * @return
	 */
	private String toCSV() {
		String CSVList = "";
		for (LogItem li : logList) {
			CSVList += li.toCSV();
		}
		return CSVList;
	}


	/**
	 * Check if external storage is available, readable and writable
	 * Shows an error message in case of problems
	 */
	private void checkExternalStorage() {
		try {
			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// We can read and write the media
				return;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				// We can only read the media
				throw new IOException("External storage read only");
			} else {
				// Something else is wrong. It may be one of many other states,
				// but all we need
				// to know is we can neither read nor write
				throw new IOException("External storage not ready");
			}
		} catch (IOException e) {
			raceLogActivity.printError(context.getString(R.string.LocalBackupError) + "\n[" + e.getMessage() + "]", true);
		}
	}

	public ArrayList<LogItem> getList() {
		return logList;
	}

}
