package com.austin.upNext;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SplashFragment extends Fragment {
	
	private static final String TAG = "SplashFragment";
	
	private Button skipLoginButton;
	private SkipLoginCallback skipLoginCallback;
	
	public interface SkipLoginCallback {
		void onSkipLoginPressed();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.splash, container, false);
		skipLoginButton = (Button) view.findViewById(R.id.skipLogin);
		skipLoginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(skipLoginCallback != null) {
					skipLoginCallback.onSkipLoginPressed();
				}
			}
		});
		
		return view;
	}
	
	public void setSkipLoginCallback(SkipLoginCallback callback) {
		skipLoginCallback = callback;
	}
}
