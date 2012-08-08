package raceLog.mobileDevice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains a log entry
 * 
 * @author Mads
 */
public class LogItem {
	private int raceId;
	private int checkpointId;
	private String teamId;
	private Date time;
	private int point;
	private boolean committed;
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

	public LogItem(int raceId, int checkpointId, String teamId, Date time, int point) {
		super();
		this.raceId = raceId;
		this.checkpointId = checkpointId;
		this.teamId = teamId;
		this.time = time;
		this.point = point;
		this.committed = false;
	}

	/**
	 * Checks that a team id is valid
	 * 
	 * @param teamId
	 * @return true: the team id is valid
	 */
	public static boolean checkTeamId(String teamId) {
		try {
			return teamId != null
					&& teamId.length() == 4
					&& (Integer.parseInt(teamId.substring(0, 1)) + Integer.parseInt(teamId.substring(1, 2)) + Integer
							.parseInt(teamId.substring(2, 3))) % 10 == Integer.parseInt(teamId.substring(3, 4));
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Instantiate a LogItem object from a csv line of a checkpoint file
	 * 
	 * @param csvLine
	 * @throws ParseException
	 */
	public LogItem(String csvLine) throws ParseException {
		String[] e = csvLine.split(",");
		raceId = Integer.parseInt(e[0]);
		checkpointId = Integer.parseInt(e[1]);
		teamId = e[2];
		time = dateFormat.parse(e[3]);
		point = Integer.parseInt(e[4]);
		committed = e[5].equals("true");
	}

	@Override
	public String toString() {
		if (point > 0) {
			return dateFormat.format(time) + ", team: " + teamId + ", " + point + " point";
		} else {
			return dateFormat.format(time) + ", team: " + teamId;
		}
	}

	public int getRaceId() {
		return raceId;
	}

	public int getCheckpointId() {
		return checkpointId;
	}

	public String getTeamId() {
		return teamId;
	}

	public Date getTime() {
		return time;
	}

	public int getPoint() {
		return point;
	}

	public boolean isCommitted() {
		return committed;
	}

	/**
	 * Returns the object as a comma separated value line
	 * 
	 * @return
	 */
	public String toCSV() {
		return raceId + "," + checkpointId + "," + teamId + "," + dateFormat.format(time) + "," + point + ","
				+ committed + "\n";
	}

	public void setCommitted(boolean committed) {
		this.committed = committed;
	}
}
