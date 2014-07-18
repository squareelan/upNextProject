package com.austin.upNext.asynctask;

import com.austin.upNext.Business.UpNextBusinessResult;
import com.austin.upNext.MainFragment.Callback;
import com.austin.upNext.util.RestClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.os.AsyncTask;

public class GetCampusBusinessRequest extends AsyncTask<Void, Void, UpNextBusinessResult> {
	
	private Callback mCallback;
	private String name, category, location;
	private double lat, lng;

	public GetCampusBusinessRequest(Callback callback, String name, String category, String location, double lat, double lng) {
		mCallback = callback;
		this.name = name;
		this.category = category;
		this.location = location;
		this.lat = lat;
		this.lng = lng;
	}

	@Override
	protected UpNextBusinessResult doInBackground(Void... params) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String search = parseSearchURI(name, category, location, lat, lng);
		String result = RestClient.connect(search);
		JsonElement json = new JsonParser().parse(result);
		UpNextBusinessResult uBR = gson.fromJson(json, UpNextBusinessResult.class);
		return uBR;
	}

	@Override
	protected void onPostExecute(UpNextBusinessResult result) {
		mCallback.onComplete();
	}
	
	private String parseSearchURI(String name, String category, String location, double lat, double lng) {
		String search = String.format("http://www.uhpnext.com/api/campus_biz/?lat=%f&lng=%f", lat,lng);
		if(name != null) {
			search += "name=" + name;
		} else if (category != null) {
			search += "category=" + category;
		} else if(location != null) {
			search += "location=" + location;
		}
		search = search.replaceAll(" ", "%20");
		return search;
	}

}
