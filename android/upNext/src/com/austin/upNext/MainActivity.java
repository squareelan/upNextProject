package com.austin.upNext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MainActivity extends SherlockFragmentActivity implements DialogBusinessListFragment.OnBusinessSelectedListener {
	
	private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";
	private static final String TAG = "MainActivity";
	
	private static final int SPLASH = 0;
	private static final int MAIN = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS + 1;
	
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private static boolean isResumed = false, userSkippedLogin = false;
    private MenuItem settings;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.d(TAG, "onCreate");
	    if (savedInstanceState != null) {
            userSkippedLogin = savedInstanceState.getBoolean(USER_SKIPPED_LOGIN_KEY);
        }
	    
	    uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        FragmentManager fm = getSupportFragmentManager();
        SplashFragment splashFragment = (SplashFragment) fm.findFragmentById(R.id.splashFragment);
        fragments[SPLASH] = splashFragment;
        fragments[MAIN] = fm.findFragmentById(R.id.mainFragment);
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
        
        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();
        
        splashFragment.setSkipLoginCallback(new SplashFragment.SkipLoginCallback() {
            @Override
            public void onSkipLoginPressed() {
                userSkippedLogin = true;
                showFragment(MAIN, false);
            }
        });
        showFragment(SPLASH, false);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
        if (fragments[MAIN].isVisible()) {
	        if (menu.size() == 0) {
	            settings = menu.add(R.string.settings);
	        }
	        return true;
	    } else {
	        menu.clear();
	        settings = null;
	    }
	    return false;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
		if (item.equals(settings)) {
	        showFragment(SETTINGS, true);
	        return true;
	    }
		//handles overflow action bar menu
		switch(item.getItemId()) {
			case R.id.action_overflow:
				showFragment(SETTINGS, true);
				return true;
			case R.id.action_refresh:
				MainFragment.upNextCampusBusinessSearch();
				Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_LONG).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        uiHelper.onResume();
        isResumed = true;
        
        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        uiHelper.onPause();
        isResumed = false;
    }
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        uiHelper.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

        outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
    }
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (isResumed) {
            FragmentManager manager = getSupportFragmentManager();
            int backStackSize = manager.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            // check for the OPENED state instead of session.isOpened() since for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
            if (state.equals(SessionState.OPENED)) {
                showFragment(MAIN, false);
            } else if (state.isClosed()) {
                showFragment(SPLASH, false);
            }
        }
    }
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
	
	@Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.d(TAG, "onResumeFrag");
        Session session = Session.getActiveSession();
        isResumed = true;
        if (session != null && session.isOpened()) {
            // if the session is already open, try to show the selection fragment
            showFragment(MAIN, false);
            userSkippedLogin = false;
        } else if (userSkippedLogin) {
            showFragment(MAIN, false);
        } else {
            // otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
            showFragment(SPLASH, false);
        }
    }

	@Override
	public void selectedBusiness(String name, String id) {
		DialogAlertFragment daf = DialogAlertFragment.newInstance(name, id);
		daf.show(getSupportFragmentManager(), "DialogAlertFragment");
	}

}
