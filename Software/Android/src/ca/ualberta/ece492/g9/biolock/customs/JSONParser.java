package ca.ualberta.ece492.g9.biolock.customs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

public class JSONParser extends AsyncTask<String, Void, JSONArray> {
	static InputStream iStream = null;
	static JSONArray jarray = null;
	static String json = "";
	JSONCallbackFunction m_callback;

	public JSONParser(JSONCallbackFunction callback) {
		m_callback = callback;
	}
	
	public JSONArray doInBackground(String... url) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url[0]);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				if (builder.charAt(0) != '['){
					builder.insert(0, "[");
					builder.append("]");
				}
			} else {
				Log.e("==>", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// Returns null and lets NewLock handle no connection
			return null;
		} catch (RuntimeException e){
			e.printStackTrace();
		}
		
		try {
			return new JSONArray(builder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void onPostExecute(JSONArray jsonArr) {
		// Parse String to JSON object
		//Log.d("ASYNCTask", jsonArr.toString());
		m_callback.execute(jsonArr);
	}
}