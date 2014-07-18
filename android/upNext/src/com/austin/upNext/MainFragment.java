package com.austin.upNext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.austin.upNext.Business.Business;
import com.austin.upNext.Business.UpNextBusinessResult;
import com.austin.upNext.Business.UpNextUserResult;
import com.austin.upNext.Business.User;
import com.austin.upNext.asynctask.GetCampusBusinessRequest;
import com.austin.upNext.asynctask.GetUserRequestFromMain;
import com.austin.upNext.asynctask.GiveHeartRequest;
import com.austin.upNext.util.EditTextWithDelete;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class MainFragment extends SherlockFragment {
	
	public interface Callback {
		void onComplete();
		void onFail();
	}
	
	private static final String TAG = "MainFragment";
	private static Context context;
	private static EditTextWithDelete searchBar;
	private static String keyword = "", name, category, location;
	private static GetCampusBusinessRequest upNextBusinessSearch;
	private GetUserRequestFromMain user_request;
	private static GiveHeartRequest giveHearts;
	private static String[] bizNames, bizIds, bizCategories, bizLocations, auto_complete_string, 
		bizAddress, wait, crowd, busyness, reportedBys;
	private static Double[] distances;
	private static Integer[] hearts;
	private static double longitude;
	private static double latitude;
	private static PullToRefreshListView listView;
	private static String userFirst, userLast, userName;
	private static List<String> bizName_list, bizCategory,bizLocation, lastReported, reportedBy;
	private static String user_id;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback sessionCallback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");	
		context = getSherlockActivity();
		Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	        makeMeRequest(session);
	    }
		
	    View view = inflater.inflate(R.layout.business_listing, container, false);
	    searchBar = (EditTextWithDelete)view.findViewById(R.id.searchBar);
		searchBar.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
		searchBar.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keycode, KeyEvent event) {
				if(keycode == KeyEvent.KEYCODE_ENTER) {
					keyword = searchBar.getText().toString();					
					determineWhichKeyword(keyword);
					upNextCampusBusinessSearch();
					InputMethodManager imm = (InputMethodManager)getSherlockActivity().getSystemService(
						      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
		
		listView = (PullToRefreshListView) view.findViewById(R.id.list);
		((PullToRefreshListView) listView).setAdapter(new ItemAdapter(getActivity()));
		listView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				upNextCampusBusinessSearch();
				Toast.makeText(getSherlockActivity(), "Refreshing", Toast.LENGTH_LONG).show();
			}
		});
	    return view;
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	    }
	}
	
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
	    setHasOptionsMenu(true);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    // Show the Up button in the action bar:
	    getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_refresh:
	        	upNextCampusBusinessSearch();
	        	Toast.makeText(getSherlockActivity(), "Refreshing", Toast.LENGTH_SHORT).show();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    Log.d(TAG, "onResume");
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
	    uiHelper.onResume();
	    getUserLocation();
	    upNextCampusBusinessSearch();	    
	    getUserRequest();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    Log.d(TAG, "onPause");
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    Log.d(TAG, "onDestroy");
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    Log.d(TAG, "onSaveInstanceState");
	    uiHelper.onSaveInstanceState(outState);
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
	                	userFirst = user.getFirstName();
	                	userLast = user.getLastName();
	                	userName = user.getUsername();
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	} 
	
	private void getProfilePicture(final Session session) {
		Bundle params = new Bundle();
		params.putBoolean("redirect", false);
		params.putString("height", "200");
		params.putString("type", "square");
		params.putString("width", "200");
		/* make the API call */
		new Request(
		    session,
		    "/me/picture",
		    params,
		    HttpMethod.GET,
		    new Request.Callback() {
		        public void onCompleted(Response response) {
		            /* handle the result */
		        	
		        }
		    }
		).executeAsync();
	}
	
	class ItemAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;
		int count = 0;
		String lastLocation;
		boolean isGoingBackUp = false;
		boolean[] heartToggle = new boolean[20];
		
		public ItemAdapter(Context context) {
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			heartToggle = setHeartChecker(19);
		}

		private class ViewHolder {
			public TextView businessName;
			public TextView category;
			public TextView lastReported;
			public TextView distance;
			public TextView addressLocation;
			public TextView reportedBy;
			public TextView likeNumber;
			public ImageView wait,crowd, update, like, busyness, redHeart;
		}

		@Override
		public int getCount() {
			if(bizNames == null) {
				return 0;
			}
			return bizNames.length;
		}			

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;			
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				view = mInflater.inflate(R.layout.row_business, parent, false);
				holder.businessName = (TextView) view.findViewById(R.id.businessName);
				holder.category = (TextView)view.findViewById(R.id.category);
				holder.lastReported = (TextView)view.findViewById(R.id.lastReported);
				holder.reportedBy = (TextView)view.findViewById(R.id.reporter_name);
				holder.distance = (TextView)view.findViewById(R.id.distance);
				holder.update = (ImageView)view.findViewById(R.id.update_button);
				holder.like  = (ImageView)view.findViewById(R.id.heart_button);
				holder.crowd = (ImageView)view.findViewById(R.id.crowd);
				holder.busyness = (ImageView)view.findViewById(R.id.busyness);
				holder.wait = (ImageView)view.findViewById(R.id.wait);
				holder.likeNumber = (TextView)view.findViewById(R.id.likeNumber);
				holder.redHeart = (ImageView)view.findViewById(R.id.heart);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.businessName.setText(bizNames[position]);
			holder.category.setText(bizCategories[position]);
			String no_data = "-1";
			if(lastReported.get(position).equals(no_data)) {
				holder.lastReported.setText("No Data");
			} else {
				String time = getTimeDiff(lastReported.get(position));
				holder.lastReported.setText(time);
			}
			if(!reportedBy.get(position).equals(no_data)) {
				holder.reportedBy.setText(String.format("Reported By %s", reportedBy.get(position)));
			}
			holder.distance.setText(distances[position].toString() + " mi");
			holder.busyness.setImageResource(busynessImage(busyness[position]));
			holder.crowd.setImageResource(crowdImage(crowd[position]));
			holder.wait.setImageResource(waitImage(wait[position]));
			holder.likeNumber.setText(String.valueOf(hearts[position]));
			holder.update.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					DialogAlertFragment daf = DialogAlertFragment.newInstance(bizNames[position], bizIds[position]);
					daf.show(getFragmentManager(), "DialogAlertFragment");					
				}
			});
			
			holder.like.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!heartToggle[position]) {
						holder.like.setImageResource(R.drawable.button_heart_clicked_red_3);
						holder.redHeart.setImageResource(R.drawable.heart_red);
						
						//fast local update
						int current = hearts[position];
						int updated = current + 1;
						hearts[position] = updated;
						holder.likeNumber.setText(String.valueOf(updated));
						
						//actual request
						giveHearts(bizIds[position], "true");
						heartToggle[position] = true;
					} else {
						holder.like.setImageResource(R.drawable.button_heart_unclicked);
						holder.redHeart.setImageResource(R.drawable.heart);
						
						//fast local update
						int current = hearts[position];
						int updated = current - 1;
						hearts[position] = updated;
						holder.likeNumber.setText(String.valueOf(updated));
						
						//actual request
						giveHearts(bizIds[position], "false");
						heartToggle[position] = false;
					}					
					//BAD BAD BAD but works
					upNextCampusBusinessSearch();
				}
			});
			return view;
		}
	}
	
	private int busynessImage(String busy) {
		if(busy.equals("busy")) {
			return R.drawable.busy;
		} else if(busy.equals("not_busy")) {
			return R.drawable.not_busy;
		} else {
			return R.drawable.not_busy;
		}			
	}
	
	private int crowdImage(String crowd) {
		int result;
		int crowdSize = Integer.parseInt(crowd);
		switch(crowdSize) {
			case 0:
				result = R.drawable.button_0_5ppl;
				break;
			case 1:
				result = R.drawable.button_5_10ppl;
				break;				
			case 2:
				result = R.drawable.button_10_20_ppl;
				break;
			case 3:
				result = R.drawable.button_20_30_ppl;
				break;
			case 4:
				result = R.drawable.button_30_plus_ppl;
				break;
			default:
				result = R.drawable.button_0_5ppl;
				break;
		}
		return result;
	}
	
	private int waitImage(String wait) {
		int result;
		int waitTime = Integer.parseInt(wait);
		switch(waitTime) {
			case 0:
				result = R.drawable.button_0_5min;
				break;
			case 1:
				result = R.drawable.button_5_10_mins;
				break;				
			case 2:
				result = R.drawable.button_10_20_mins;
				break;
			case 3:
				result = R.drawable.button_20_30_mins;
				break;
			case 4:
				result = R.drawable.button_30_plus_mins;
				break;
			default:
				result = R.drawable.button_0_5min;
				break;
		}
		return result;
	}
	
	public static void upNextCampusBusinessSearch() {
		upNextBusinessSearch = new GetCampusBusinessRequest(new Callback() {
			
			@Override
			public void onFail() {}
			
			@Override
			public void onComplete() {
				try {
					UpNextBusinessResult result = upNextBusinessSearch.get(1000, TimeUnit.MILLISECONDS);
					List<Business> biz = result.getBusinesses();
					List<String> _idList = new ArrayList<String>();
					List<String>_wait = new ArrayList<String>();
					List<Double> _distances = result.getDistances();
					List<String> _busyness = new ArrayList<String>();
					List<Integer>_hearts = new ArrayList<Integer>();
					List<String>_crowd = new ArrayList<String>();
					lastReported = new ArrayList<String>();
					reportedBy = new ArrayList<String>();
					bizName_list = new ArrayList<String>();								
					bizCategory = new ArrayList<String>();
					bizLocation = new ArrayList<String>();
										
					for(Business b: biz) {						
						bizName_list.add(b.getName());												
						_idList.add(b.get_id());
						bizCategory.add(b.getCategory());
						bizLocation.add(b.getLocation());
						lastReported.add(b.getLastReported());
						reportedBy.add(b.getReportedBy());
						_wait.add(b.getWait());
						_crowd.add(b.getCrowd());
						_hearts.add(b.getHearts());
						_busyness.add(b.getBusyness());
					}
					
					bizNames = bizName_list.toArray(new String[bizName_list.size()]);
					bizIds = _idList.toArray(new String[_idList.size()]);
					bizCategories = bizCategory.toArray(new String[bizCategory.size()]);
					bizLocations = bizLocation.toArray(new String[bizLocation.size()]);
					reportedBys = reportedBy.toArray(new String[reportedBy.size()]);
					distances = _distances.toArray(new Double[_distances.size()]);
					wait = _wait.toArray(new String[_wait.size()]);
					busyness = _busyness.toArray(new String[_busyness.size()]);
					crowd = _crowd.toArray(new String[_crowd.size()]);
					hearts = _hearts.toArray(new Integer[_hearts.size()]);
					
					setAutoComplete();					
					listView.invalidateViews();
					listView.onRefreshComplete();					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				} finally {
					name = null;
					category = null;
					location = null;
				}
			}
		}, name, category, location, latitude, longitude);
		upNextBusinessSearch.execute();
	}
	
	private static void giveHearts(String bizId, String plus) {
		giveHearts = new GiveHeartRequest(new Callback() {
			
			@Override
			public void onFail() {}
			
			@Override
			public void onComplete() {
				listView.invalidateViews();
				listView.onRefreshComplete();
			}
		}, bizId, plus);
		giveHearts.execute();
	}
	
	private void getUserRequest() {
		user_request = new GetUserRequestFromMain(new Callback() {
			
			@Override
			public void onComplete() {
				try {
					UpNextUserResult result = user_request.get(1000, TimeUnit.MILLISECONDS);
					User user = result.getUser();
					user_id = user.get_id();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				}				
			}

			@Override
			public void onFail() {}
			
		}, userFirst, userLast);
		user_request.execute();
	}
	
	private void determineWhichKeyword(String keyword) {		
		if(bizName_list.contains(keyword)) {
			if(bizLocation.contains(keyword)) {
				location = keyword;
				name = null;
				category = null;
			} else {
				name = keyword;
				category = null;
				location = null;
			}
		} else if(bizCategory.contains(keyword)) {			
			category = keyword;
			location = null;
			name = null;
		} else if(bizLocation.contains(keyword)) {
			location = keyword;
			name = null;
			category = null;
		}
	}
	
	public static String getTimeDiff(String time) {
		// "14:59"
		Calendar rightNow = Calendar.getInstance();
		int java_hour = rightNow.get(Calendar.HOUR_OF_DAY);
		int java_minute = rightNow.get(Calendar.MINUTE)+2;
		String[] times = time.split(":"); // 0 is hour 1 is minute
		String hour = times[0];
		String min = times[1];
		int hour_in_int = Integer.parseInt(hour);
		int min_in_int = Integer.parseInt(min);
		//current hour should be always greater than reported_hour
		if(java_hour == hour_in_int) {
			//reported in same hour
			if(java_minute == min_in_int) {
				return "0 min ago";
			} else if(java_minute - min_in_int == 1) {
				return "1 min ago";
			} else {
				int diff = java_minute - min_in_int;
				return String.format("%s mins ago", diff);
			}
		} else {
			//reported in different hour
			if(java_hour - hour_in_int == 1) {
				return "1 hour ago";
			} else {
				int diff = java_hour - hour_in_int;
				return String.format("%s hours ago", diff);
			}			
		}		
	}
	
	private static boolean[] setHeartChecker(int size) {
		boolean[] result = new boolean[size];
		for(int i=0; i< size; i++) {
			result[i] = false;
		}
		return result;
	}
	
	private static void setAutoComplete() {
		ArrayAdapter<?> adapter = new ArrayAdapter<Object>(context, android.R.layout.simple_list_item_1, bizNames);
		searchBar.setAdapter(adapter);
	}
	
	private void getUserLocation() {
		// Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

	    // Create a criteria object to retrieve provider
	    Criteria criteria = new Criteria();

	    // Get the name of the best provider
	    String provider = locationManager.getBestProvider(criteria, true);

	    // Get Current Location
	    Location myLocation = locationManager.getLastKnownLocation(provider);

	    // Get latitude of the current location
	    latitude = myLocation.getLatitude();

	    // Get longitude of the current location
	    longitude = myLocation.getLongitude();
	}

 	public static String getUserFirst() {
		return userFirst;
	}

	public static String getUserLast() {
		return userLast;
	}
	
	public static String getUserId() {
		return user_id;
	}
	
	public static String getUserName() {
		return userName;
	}
	
}
