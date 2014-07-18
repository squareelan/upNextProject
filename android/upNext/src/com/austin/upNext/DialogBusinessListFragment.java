package com.austin.upNext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class DialogBusinessListFragment extends SherlockDialogFragment {
	
	private OnBusinessSelectedListener mCallback;
	
	public interface OnBusinessSelectedListener {
		void selectedBusiness(String name, String id);
	}
	
	private static String TAG = "DialogBusinessListFragment";
	private static String[] bizNames, bizIds;
	private static DialogBusinessListFragment currentDialog;
	
	public static DialogBusinessListFragment newInstance(String[] bizs, String[] ids) {
		DialogBusinessListFragment dbf = new DialogBusinessListFragment();
		bizNames = bizs;
		bizIds = ids;
		currentDialog = dbf;
		return dbf;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.i(TAG, "onCreateDialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
		LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.float_button_dialog, null);
		
		ListView dialog = (ListView)view.findViewById(R.id.business_list_dialog);
		dialog.setAdapter(new ItemAdapter(getActivity()));
		dialog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				mCallback.selectedBusiness(bizNames[pos], bizIds[pos]);							
				currentDialog.dismiss();
			}
		});
	    builder.setTitle(R.string.select_waitlist)
	    	   .setView(dialog);
	    return builder.create();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
            mCallback = (OnBusinessSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

	class ItemAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;
		
		public ItemAdapter(Context context) {
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		private class ViewHolder {
			public TextView businessName;
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
				view = mInflater.inflate(R.layout.dialog_row_business, parent, false);
				holder = new ViewHolder();
				holder.businessName = (TextView) view.findViewById(R.id.businessName);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.businessName.setText((position + 1) + ". " + bizNames[position]);
			return view;
		}
	}
	
}
