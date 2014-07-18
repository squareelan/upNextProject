package com.austin.upNext;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabParentFragment extends SherlockFragment {
	
	private static String TAG = "TabParentFragment";
	
	private FragmentTabHost mTabHost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		setHasOptionsMenu(true);
		mTabHost = new FragmentTabHost(getActivity());
		mTabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_tabhost);
		mTabHost.addTab(mTabHost.newTabSpec("BusinessList").setIndicator("Search"), MainFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("MyWaitList").setIndicator("My Profile"), MyProfileFragment.class, null);		
		return mTabHost;
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.main_action_menu, menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_refresh:
//	            MainFragment.upNextSearch(getActivity());
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView");
		mTabHost = null;
    }
	
}
