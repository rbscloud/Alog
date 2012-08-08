package raceLog.mobileDevice;

import java.util.ArrayList;

/**
 * Holds a list of uncommited LogItem objects and the thread that commits
 * LogItems
 * 
 * @author Mads
 */
public class CommitList {

	private ArrayList<LogItem> commitList = new ArrayList<LogItem>();
	CommitThread commitThread;

	public CommitList(RaceLogActivity raceLogActivity, LogList logList) {
		commitThread = new CommitThread(this, raceLogActivity, logList);
		commitThread.start();
	}

	/**
	 * Adds a LogItem object to the list and notifies commitThread
	 * 
	 * @param logItem
	 */
	public synchronized void add(LogItem logItem) {
		commitList.add(logItem);
		synchronized (commitThread) {
			commitThread.notify();
		}
	}

	/**
	 * Returns if commitList contains no elements
	 * 
	 * @return true: commitList is empty
	 */
	public boolean isEmpty() {
		return commitList.isEmpty();
	}

	/**
	 * Removes the specified LogItem from commitList
	 * 
	 * @param logItem
	 * @return
	 */
	public synchronized boolean remove(LogItem logItem) {
		return commitList.remove(logItem);
	}

	/**
	 * returns the LogItem at the specified position in commitList
	 * 
	 * @param index
	 * @return
	 */
	public synchronized LogItem get(int index) {
		return commitList.get(index);
	}
}

/**
 * Commits LogItems from commitList
 * 
 * @author Mads
 */
class CommitThread extends Thread {

	private CommitList commitList;
	private RaceLogActivity raceLogActivity;
	private int waitTime = 60;
	private LogList logList;

	public CommitThread(CommitList commitList, RaceLogActivity raceLogActivity, LogList logList) {
		this.commitList = commitList;
		this.raceLogActivity = raceLogActivity;
		this.logList = logList;
	}

	@Override
	public void run() {
		while (true) {
			while (!commitList.isEmpty()) {
				try {
					// Commit head of commitList
					LogItem logItem = commitList.get(0);
					String response = HttpInterface.sendLogItem(raceLogActivity, logItem);
					if (response.contains("Log submitted")) {
						// commit succeded
						logItem.setCommitted(true);
						commitList.remove(logItem);
						logList.makeCheckPoint();
						raceLogActivity.updateTextViewLog();
					} else {
						throw new Exception(response);
					}
				} catch (final Exception e) {
					// Probably connection not available error. Wait 60 seconds
					// and try again
					waitTime = 60;
					raceLogActivity.showToast("Cannot commit:\n" + e.getMessage());
					while (waitTime > 0) {
						// Update counter on RaceLogActivity once every second
						raceLogActivity.printError("Cannot commit. I will try again after " + waitTime-- + " seconds",
								false);
						try {
							synchronized (this) {
								wait(1000);
							}
						} catch (InterruptedException e1) {
						}
					}

				}
			}
			// Commit list empty = all LogItems committed successfully.
			raceLogActivity.clearErrorLog();
			// Block
			try {
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
