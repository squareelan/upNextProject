package com.austin.upNext;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.austin.upNext.Business.StandardResult;
import com.austin.upNext.asynctask.EarnPointRequest;
import com.austin.upNext.asynctask.ReportWaitTimeRequest;

public class DialogAlertFragment extends SherlockDialogFragment {
	
	public interface Callback {
		void onComplete();
		void onFail();
	}
	
	private static String TAG = "DialogAlertFragment";
	private static String bizName, bizId, reportedBy, busyness;
	private static ReportWaitTimeRequest report;
	private static EarnPointRequest _earnPoint;
	private ImageView busy, not_busy;
	private int wait, crowd;
	private Integer[] images;
	private Spinner crowdChoices, waitChoices;
	private int busy_checker, not_busy_checker;
	
	public static DialogAlertFragment newInstance(String biz, String id) {
		DialogAlertFragment adf = new DialogAlertFragment();
		bizName = biz;
		bizId = id;
		return adf;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.update_dialog, null);
		busy_checker = 0;
		not_busy_checker = 0;
		
		busy = (ImageView)view.findViewById(R.id.busy_button);
		busy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getSherlockActivity(), "BUSY", Toast.LENGTH_SHORT).show();
				if(busy_checker == 0 && not_busy_checker == 0) {
					busy.setImageResource(R.drawable.button_busy_clicked);
					busy_checker = 1;
				} else if(not_busy_checker == 1 && busy_checker == 0) {
					busy.setImageResource(R.drawable.button_busy_clicked);
					not_busy.setImageResource(R.drawable.button_not_busy);
					busy_checker = 1;
					not_busy_checker = 0;
				} else if(busy_checker == 1 && not_busy_checker == 0) {
					busy.setImageResource(R.drawable.button_busy);
					busy_checker = 0;
				}
			}
		});
		
		busy.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				busy.setImageResource(R.drawable.button_busy_onclick);
				return false;
			}
		});
		
		not_busy = (ImageView)view.findViewById(R.id.not_busy_button);
		not_busy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getSherlockActivity(), "NOT BUSY", Toast.LENGTH_SHORT).show();
				if(not_busy_checker == 0 && busy_checker == 0) {
					not_busy.setImageResource(R.drawable.button_not_busy_clicked);
					not_busy_checker = 1;
				} else if(not_busy_checker == 0 && busy_checker == 1){
					not_busy.setImageResource(R.drawable.button_not_busy_clicked);
					busy.setImageResource(R.drawable.button_busy);
					not_busy_checker = 1;
					busy_checker = 0;
				} else if(not_busy_checker == 1 && busy_checker == 0) {
					not_busy.setImageResource(R.drawable.button_not_busy);
					not_busy_checker = 0;
				}
			}
		});
		
		not_busy.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				not_busy.setImageResource(R.drawable.button_not_busy_onclick);
				return false;
			}
		});
		
		crowdChoices = (Spinner)view.findViewById(R.id.crowd_spinner);
		crowdChoices.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				crowd = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		waitChoices = (Spinner)view.findViewById(R.id.wait_spinner);
		waitChoices.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				wait = position;				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});

		builder.setTitle(String.format("%s", bizName))
		    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		        	//TODO: check if user selected both crowd & flow
		        	Toast.makeText(getSherlockActivity(), "Thank you for update! \nYou have earned 1 point!", Toast.LENGTH_SHORT).show();
		        	reportWaitTime();
		        	earnPoint();
		        }
		    })
		    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		            // User cancelled the dialog
		        }
		    })
		    .setView(view);
		 // Create the AlertDialog object and return it
		 return builder.create();
	}
	
	private void reportWaitTime() {
		report = new ReportWaitTimeRequest(new Callback() {
			
			@Override
			public void onFail() {}
			
			@Override
			public void onComplete() {
				try {
					StandardResult sR = report.get(1000, TimeUnit.MILLISECONDS);
					Calendar rightNow = Calendar.getInstance();
					int java_hour = rightNow.get(Calendar.HOUR_OF_DAY);
					int java_minute = rightNow.get(Calendar.MINUTE) +2;
					String reportedTime = Integer.toString(java_hour) + ":" + Integer.toString(java_minute);
					MainFragment.getTimeDiff(reportedTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
			}
		}, bizId, wait, crowd, MainFragment.getUserName(), getBusyness(busy_checker));
		report.execute();
	}
	
	//TODO: check if user is logged in or not.
	private void earnPoint() {
		_earnPoint = new EarnPointRequest(new Callback() {
			
			@Override
			public void onFail() {}
			
			@Override
			public void onComplete() {
				
			}
		}, MainFragment.getUserId());
		_earnPoint.execute();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MainFragment.upNextCampusBusinessSearch();
	}
	
	private String getBusyness(int busy_checker) {
		if(busy_checker == 1) {
			return "busy";
		} else {
			return "not_busy";
		}
	}
	
}
