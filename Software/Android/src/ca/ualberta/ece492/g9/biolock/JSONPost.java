package ca.ualberta.ece492.g9.biolock;

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

public class JSONPost extends AsyncTask<String, Void, Void> {
	static InputStream iStream = null;
	static JSONArray jarray = null;
	static String json = "";

	public Void doInBackground(String... url) {

		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url[0]);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("json", url[1]));
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(httpPost);
			// check whether post was successful or not
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e){
			// No connection to server
			this.cancel(true);
		}
		return null;
	}
}