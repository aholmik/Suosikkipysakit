package com.karhatsu.suosikkipysakit.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;

import android.os.AsyncTask;

import com.karhatsu.suosikkipysakit.domain.Stop;
import com.karhatsu.suosikkipysakit.util.AccountInformation;

public class StopRequest extends AsyncTask<String, Void, Stop> {

	private static final String BASE_URL = "http://api.reittiopas.fi/hsl/prod/";

	private OnStopRequestReady notifier;
	private boolean ready;
	private Stop result;

	public StopRequest(OnStopRequestReady notifier) {
		this.notifier = notifier;
	}

	@Override
	protected Stop doInBackground(String... stopCode) {
		try {
			return getData(stopCode[0]);
		} catch (StopNotFoundException e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(Stop result) {
		this.result = result;
		ready = true;
		notifyAboutResult();
	}

	public void setOnStopRequestReady(OnStopRequestReady notifier) {
		this.notifier = notifier;
		if (ready) {
			notifyAboutResult();
		}
	}

	private void notifyAboutResult() {
		if (notifier != null) {
			notifier.notifyStopRequested(result);
		}
	}

	private Stop getData(String stopCode) throws StopNotFoundException {
		try {
			String json = readStopDataAsJson(stopCode);
			return new StopJSONParser().parse(json);
		} catch (StopNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String readStopDataAsJson(String stopCode)
			throws MalformedURLException, IOException, ProtocolException,
			JSONException {
		InputStream is = null;
		try {
			HttpURLConnection conn = createConnection(stopCode);
			conn.connect();
			is = conn.getInputStream();
			return readStream(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	private HttpURLConnection createConnection(String stopCode)
			throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL(getStopRequestUrl(stopCode));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		return conn;
	}

	private String getStopRequestUrl(String stopCode) {
		return BASE_URL + "?request=stop&user="
				+ AccountInformation.getUserName() + "&pass="
				+ AccountInformation.getPassword() + "&format=json&code="
				+ stopCode;
	}

	public String readStream(InputStream stream) throws IOException,
			UnsupportedEncodingException {
		Scanner scanner = new Scanner(stream).useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : "";
	}

}
