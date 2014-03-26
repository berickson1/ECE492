package ca.ualberta.ece492.g9.biolock.customs;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class GetPicture extends AsyncTask<String, Void, byte[]> {
	private final WeakReference<ImageView> imageViewReference;
	private ProgressDialog loadPictureWait;
	
	public GetPicture(ImageView imageView, Activity caller) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
		loadPictureWait = new ProgressDialog(caller);
    }

	@Override
    protected void onPreExecute() {
		loadPictureWait.setMessage("Loading picture");
		loadPictureWait.show();
    }
	
	public byte[] doInBackground(String... url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url[0]);
		
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		byte[] imageBuff = new byte[1024];
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedInputStream imageIn = new BufferedInputStream(content, 8190);
				ByteArrayBuffer imageBuffer = new ByteArrayBuffer(50);
				int current;
				while ((current = imageIn.read()) != -1){
					imageBuffer.append((byte)current);
				}
				imageBuff = imageBuffer.toByteArray();
			} else {
				Log.e("==>", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			return null;
		} catch (RuntimeException e){
			e.printStackTrace();
		}
		return imageBuff;
	}

	protected void onPostExecute(byte[] image) {
		loadPictureWait.dismiss();
		Bitmap picture = BitmapFactory.decodeByteArray(image, 0, image.length);
		if (imageViewReference != null){
			final ImageView pictureView = imageViewReference.get();
			if (pictureView != null) {
				pictureView.setImageBitmap(picture);
			}
		}		
	}
}