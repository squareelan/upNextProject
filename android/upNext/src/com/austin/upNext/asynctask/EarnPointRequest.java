package com.austin.upNext.asynctask;

import java.io.IOException;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.austin.upNext.DialogAlertFragment.Callback;
import com.austin.upNext.Business.UpNextUserResult;
import com.austin.upNext.util.RestClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EarnPointRequest extends AsyncTask<Void, Void, Void> {
	
	private Callback mCallback;
	private String id;
	private String url;
	
	public EarnPointRequest(Callback callback, String id) {
		mCallback = callback;
		this.id = id;
		url = "http://www.uhpnext.com/api/earnPoint";
	}

	@Override
	protected Void doInBackground(Void... params) {
		System.out.println(id);
		postData(url, id);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		mCallback.onComplete();
	}
	
	public void postData(String url, String id) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("user", id));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        System.out.println(response.toString());

	    } catch (ClientProtocolException e) {
	    } catch (IOException e) {
	    }
	}
	
}
