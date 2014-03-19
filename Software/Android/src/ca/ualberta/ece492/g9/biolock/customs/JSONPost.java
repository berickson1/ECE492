package ca.ualberta.ece492.g9.biolock.customs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;


import android.os.AsyncTask;

public class JSONPost extends AsyncTask<String, Void, Integer> {
	static InputStream iStream = null;
	static JSONArray jarray = null;
	static String json = "";

	JSONCallbackFunction m_callback;

	public JSONPost(JSONCallbackFunction callback) {
		m_callback = callback;
	}

	public Integer doInBackground(String... url) {
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url[0]);
		Integer postResponse = -1;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("json", url[2]));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse postRequest = client.execute(httpPost);
			postResponse = postRequest.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			// No connection to server
			e.printStackTrace();
			// Returns failed & will be handled by the caller
			return -1;
		}
		return postResponse;
	}

	protected void onPostExecute(Integer response) {
		// Returns post response
		m_callback.execute(response);
	}
}