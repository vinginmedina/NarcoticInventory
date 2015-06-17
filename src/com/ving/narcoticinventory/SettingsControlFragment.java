package com.ving.narcoticinventory;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SettingsControlFragment extends ListFragment {
	private ManageConfiguration myActivity = null;
	private int mCurOption = 0;
	
	@Override
    public void onInflate(Activity myActivity, AttributeSet attrs, Bundle icicle) {
    	super.onInflate(myActivity, attrs, icicle);
    }
	
	@Override
    public void onAttach(Activity myActivity) {
    	super.onAttach(myActivity);
    	this.myActivity = (ManageConfiguration)myActivity;
    }
	
	@Override
    public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);
        if (icicle != null) {
            mCurOption = icicle.getInt("curOption", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater myInflater, ViewGroup container, Bundle icicle) {
    	return super.onCreateView(myInflater, container, icicle);
    }
    
    @Override
    public void onActivityCreated(Bundle icicle) {
        super.onActivityCreated(icicle);

        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                ManageConfiguration.OPTIONS));

        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setSelection(mCurOption);
    }

    @Override
    public void onStart() {
    	super.onStart();
    }

    @Override
    public void onResume() {
    	super.onResume();
    }

    @Override
    public void onPause() {
    	super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        icicle.putInt("curOption", mCurOption);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        myActivity.pickOption(pos);
    	mCurOption = pos;
    }

    @Override
    public void onStop() {
    	super.onStop();
    }

    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    }

    @Override
    public void onDetach() {
    	super.onDetach();
    	myActivity = null;
    }

}
