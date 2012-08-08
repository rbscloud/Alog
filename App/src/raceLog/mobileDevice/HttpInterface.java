package raceLog.mobileDevice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

/**
 * This class is used as interface between the server and the application
 * 
 * @author Mads
 */
public class HttpInterface {

	public static String sendLogURL = "http://adventure.flab.dk/operator/submitLog.php";
	public static String getRaceListURL = "http://adventure.flab.dk/operator/retriveRace.php";
	public static String getcheckPointListURL = "http://adventure.flab.dk/operator/retriveRace.php";

	/**
	 * Checks if the data connection is available and usable
	 * 
	 * @param context
	 * @return true: Internet connection ok
	 */
	private static boolean checkInternetConnection(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks data connection and sends a LogItem to the server
	 * 
	 * @param context
	 * @param logItem
	 * @return response from server
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String sendLogItem(Context context, LogItem logItem) throws ClientProtocolException, IOException {
		if (!checkInternetConnection(context)) {
			throw new ConnectException("Connection Not available");
		}
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("raceId", "" + logItem.getRaceId()));
		pairs.add(new BasicNameValuePair("checkpointId", "" + logItem.getCheckpointId()));
		pairs.add(new BasicNameValuePair("teamId", "" + logItem.getTeamId()));
		pairs.add(new BasicNameValuePair("time", "" + (logItem.getTime().getTime() / 1000)));
		pairs.add(new BasicNameValuePair("point", "" + logItem.getPoint()));
		pairs.add(new BasicNameValuePair("username", "username"));
		pairs.add(new BasicNameValuePair("password", "password"));

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(sendLogURL);
		post.setEntity(new UrlEncodedFormEntity(pairs));
		HttpResponse response = client.execute(post);
		return GetText(response.getEntity().getContent());
	}

	/**
	 * Extracts the text from the response content
	 * 
	 * @param in inputstream
	 * @return response text
	 */
	public static String GetText(InputStream in) {
		String text = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			text = sb.toString();
		} catch (Exception ex) {
		} finally {
			try {

				in.close();
			} catch (Exception ex) {
			}
		}
		return text;
	}

	/**
	 * Requests and returns a list of races available for the current user
	 * 
	 * @param context
	 * @param extras bundle containing username and password
	 * @return XML list containing races
	 * @throws ConnectException
	 */
	public static String getRaceListXML(Context context, Bundle extras) throws ConnectException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("type", "races"));
		pairs.add(new BasicNameValuePair("username", extras.getString("Username")));
		pairs.add(new BasicNameValuePair("password", extras.getString("Password")));

		return getXML(context, getRaceListURL, pairs);
	}

	/**
	 * Requests and returns a list of checkpoints in the selected race
	 * 
	 * @param context
	 * @param extras bundle containing username, password and race id
	 * @return XML list containing checkpoints
	 * @throws ConnectException
	 */
	public static String getCheckPointRaceListXML(Context context, Bundle extras) throws ConnectException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("username", extras.getString("Username")));
		pairs.add(new BasicNameValuePair("password", extras.getString("Password")));

		pairs.add(new BasicNameValuePair("type", "checkpoints"));
		pairs.add(new BasicNameValuePair("raceId", "" + extras.getInt("RaceId")));

		return getXML(context, getcheckPointListURL, pairs);
	}

	/**
	 * Checks data connection and requests a XML response from a server
	 * 
	 * @param context
	 * @param url
	 * @param postVariables list of variables to post
	 * @return XML response
	 * @throws ConnectException
	 */
	private static String getXML(Context context, String url, List<NameValuePair> postVariables)
			throws ConnectException {
		if (!checkInternetConnection(context)) {
			throw new ConnectException("Connection Not available");
		}
		String line = null;

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(postVariables));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			line = EntityUtils.toString(httpEntity);
		} catch (UnsupportedEncodingException e) {
			line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
		} catch (MalformedURLException e) {
			line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
		} catch (IOException e) {
			line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
		}
		return line;

	}
}
