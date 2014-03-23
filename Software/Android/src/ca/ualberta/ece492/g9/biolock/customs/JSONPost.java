package ca.ualberta.ece492.g9.biolock.customs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.json.JSONArray;
import org.json.JSONException;


import android.os.AsyncTask;
import android.util.Log;

public class JSONPost extends AsyncTask<String, Void, JSONArray> {
	static InputStream iStream = null;
	static JSONArray jarray = null;
	static String json = "";

	JSONCallbackFunction m_callback;

	public JSONPost(JSONCallbackFunction callback) {
		m_callback = callback;
	}

	public JSONArray doInBackground(String... url) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url[0]);
		Integer postResponse = -1;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("type", url[1]));
			nameValuePairs.add(new BasicNameValuePair("json", url[2]));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse postRequest = client.execute(httpPost);
			postResponse = postRequest.getStatusLine().getStatusCode();
			if (postResponse == 200) {
				HttpEntity entity = postRequest.getEntity();
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
			e.printStackTrace();
		} catch (RuntimeException e) {
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