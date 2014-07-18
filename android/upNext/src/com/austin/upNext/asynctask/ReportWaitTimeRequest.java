package com.austin.upNext.asynctask;

import android.os.AsyncTask;

import com.austin.upNext.DialogAlertFragment.Callback;
import com.austin.upNext.Business.StandardResult;
import com.austin.upNext.util.RestClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ReportWaitTimeRequest extends AsyncTask<Void, Void, StandardResult> {
	
	private Callback mCallback;
	private String bizId, reportedBy, busyness;
	private int wait, crowd;
	
	public ReportWaitTimeRequest(Callback callback, String bizId, int wait, int crowd, String reportedBy, String busyness) {
		mCallback = callback;
		this.bizId = bizId;
		this.wait = wait;
		this.crowd = crowd;
		this.reportedBy = reportedBy;
		this.busyness = busyness;
	}

	@Override
	protected StandardResult doInBackground(Void... params) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String search = String.format("http://www.uhpnext.com/api/reportWaitTime/?id=%s&wait=%d&crowd=%d&user=%s&busyness=%s", 
				bizId, wait, crowd, reportedBy, busyness);
		String result = RestClient.connect(search);
		JsonElement json = new JsonParser().parse(result);
		StandardResult sR = gson.fromJson(json, StandardResult.class);
		return sR;
	}

	@Override
	protected void onPostExecute(StandardResult result) {
		mCallback.onComplete();
	}

}
