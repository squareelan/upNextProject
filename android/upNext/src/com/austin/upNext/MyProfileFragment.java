package com.austin.upNext;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.austin.upNext.Business.UpNextUserResult;
import com.austin.upNext.Business.User;
import com.austin.upNext.asynctask.GetUserRequest;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class MyProfileFragment extends SherlockFragment {
	
	public interface Callback {
		void onComplete();
	}
	
	private static final String TAG = "MyPointFragment";
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private ProfilePictureView profilePictureView;
	private UiLifecycleHelper uiHelper;
	private TextView user_name, user_point;
	private static String userFirst;
	private static String userLast;
	private static String userName;
	private GetUserRequest user_request;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
	        // Get the user's data
	        makeMeRequest(session);
	    }
		View view = inflater.inflate(R.layout.my_profile, container, false);
		profilePictureView = (ProfilePictureView) view.findViewById(R.id.profile_picture);
		profilePictureView.setCropped(true);
		
		user_name = (TextView)view.findViewById(R.id.user_name);
		user_point = (TextView)view.findViewById(R.id.user_point);
		
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REAUTH_ACTIVITY_CODE) {
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    getUserRequest();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	private void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                    profilePictureView.setProfileId(user.getId());
	                    user_name.setText(user.getName());
	                	userFirst = user.getFirstName();
	                	userLast = user.getLastName();
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	}
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        makeMeRequest(session);
	    }
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private void getUserRequest() {
		user_request = new GetUserRequest(new Callback() {
			
			@Override
			public void onComplete() {
				try {
					UpNextUserResult result = user_request.get(1000, TimeUnit.MILLISECONDS);
					User user = result.getUser();
					user_point.setText(String.format("Total Points: %d", user.getPoint()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
				
			}
		}, userFirst, userLast);
		user_request.execute();
	}

}
