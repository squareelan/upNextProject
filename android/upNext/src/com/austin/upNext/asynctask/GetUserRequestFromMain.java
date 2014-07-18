package com.austin.upNext.asynctask;

import android.os.AsyncTask;

import com.austin.upNext.MainFragment.Callback;
import com.austin.upNext.Business.UpNextUserResult;
import com.austin.upNext.util.RestClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GetUserRequestFromMain extends AsyncTask<Void, Void, UpNextUserResult> {

	private Callback mCallback;
	private String first, last;
	
	public GetUserRequestFromMain(Callback callback, String first, String last) {
		mCallback = callback;
		this.first = first;
		this.last = last;
	}

	@Override
	protected UpNextUserResult doInBackground(Void... params) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String search = String.format("http://www.uhpnext.com/api/getUser/?first=%s&last=%s", 
				first, last);
		String result = RestClient.connect(search);
		JsonElement json = new JsonParser().parse(result);
		UpNextUserResult user = gson.fromJson(json, UpNextUserResult.class);
		return user;
	}

	@Override
	protected void onPostExecute(UpNextUserResult result) {
		mCallback.onComplete();
	}

}
