package com.austin.upNext.asynctask;

import android.os.AsyncTask;

import com.austin.upNext.MainFragment.Callback;
import com.austin.upNext.Business.StandardResult;
import com.austin.upNext.util.RestClient;
import com.google.android.gms.drive.internal.m;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GiveHeartRequest extends AsyncTask<Void, Void, StandardResult> {
	
	private Callback mCallback;
	private String bizId, plus;
	
	public GiveHeartRequest(Callback callback, String bizId, String plus) {
		mCallback = callback;
		this.bizId = bizId;
		this.plus = plus;
	}

	@Override
	protected StandardResult doInBackground(Void... params) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String search = String.format("http://www.uhpnext.com/api/updateHearts/?id=%s&plus=%s", bizId, plus);
		String result = RestClient.connect(search);
		JsonElement json = new JsonParser().parse(result);
		StandardResult sr = gson.fromJson(json, StandardResult.class);
		return sr;
	}

	@Override
	protected void onPostExecute(StandardResult result) {
		mCallback.onComplete();
	}	

}
