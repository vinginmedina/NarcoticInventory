package com.ving.narcoticinventory;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

public class DrugListAdapter extends BaseExpandableListAdapter {
	
	private Context mContext;
	private LayoutInflater inflater;
    private ArrayList<Drug> drugList;
    private MyApplication myApp;
 
    public DrugListAdapter(Context context, ArrayList<Drug> lst, MyApplication myApp){
    	mContext = context;
    	drugList = lst;
    	this.myApp = myApp;
        inflater = LayoutInflater.from(context);
    }
    
    @Override
    //counts the number of group/parent items so the list knows how many times calls getGroupView() method
    public int getGroupCount() {
        return drugList.size();
    }
 
    @Override
    //counts the number of children items so the list knows how many times calls getChildView() method
    public int getChildrenCount(int i) {
        return drugList.get(i).getTransactionList().size();
    }
 
    @Override
    //gets the title of each parent/group
    public Object getGroup(int i) {
        return drugList.get(i);
    }
 
    @Override
    //gets the name of each item
    public Object getChild(int i, int i1) {
        return drugList.get(i).getTransaction(i1);
    }
 
    @Override
    public long getGroupId(int i) {
        return i;
    }
 
    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }
 
    @Override
    public boolean hasStableIds() {
        return true;
    }
    
    @Override
    public void onGroupExpanded(final int groupPosition) {
        super.onGroupExpanded(groupPosition);
        Drug drug = (Drug) getGroup(groupPosition);
        if (drug.getTransactionList().size() > 0) {
        	myApp.scrollDrugAdapter(groupPosition);
        }
   }
 
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
 
    	final Drug drug = (Drug) getGroup(i);
    	String drugText = drug.name() + " " + drug.label(Drug.CURRENT);
        if (view == null) {
            view = inflater.inflate(R.layout.drug_list_item, viewGroup,false);
        }
 
        Button btn = (Button) view.findViewById(R.id.addTrans);
        btn.setFocusable(false);
        btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	myApp.getActivity().addTransaction(drug);
            }
        });
        
        TextView textView = (TextView) view.findViewById(R.id.listDrugView);
        textView.setText(drugText);
        if (myApp.getTextSettings() != null) {
        	textView.setTextColor(myApp.getTextSettings().getColor(ColorAndSize.DRUG_HEADING_ROW));
        	textView.setTextSize(myApp.getTextSettings().getTextSize(ColorAndSize.DRUG_HEADING_ROW));
        }
        return view;
    }
 
    @Override
    public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {
    	final Drug drug = (Drug) getGroup(i);
    	final Transaction trans = (Transaction) getChild(i, i1);
        final String transText = trans.label(Transaction.DATEACTION) + "\n" + trans.label(Transaction.DETAIL);
        if (view == null) {
       		view = inflater.inflate(R.layout.trans_list_item, viewGroup, false);
        }
 
        Button btn = (Button) view.findViewById(R.id.makeError);
        TextView textView = (TextView) view.findViewById(R.id.listTransView);
        btn.setFocusable(false);
        textView.setText(transText);
        if (trans.inError()) {
        	textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        	textView.setTextColor(myApp.getTextSettings().getColor(ColorAndSize.TRANSACTION_ERROR_ROW));
        	textView.setTextSize(myApp.getTextSettings().getTextSize(ColorAndSize.TRANSACTION_ERROR_ROW));
        	btn.setAlpha(0);
        	btn.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View v) {
	            }
	        });
        } else {
        	textView.setPaintFlags(textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        	textView.setTextColor(myApp.getTextSettings().getColor(ColorAndSize.TRANSACTION_DETAIL_ROW));
        	textView.setTextSize(myApp.getTextSettings().getTextSize(ColorAndSize.TRANSACTION_DETAIL_ROW));
        	if (trans.getAction() == Transaction.REMSTOCK) {
        		btn.setAlpha(0);
            	btn.setOnClickListener(new Button.OnClickListener() {
    	            public void onClick(View v) {
    	            }
    	        });
        	} else {
	        	btn.setAlpha(100);
		        btn.setOnClickListener(new Button.OnClickListener() {
		            public void onClick(View v) {
		            	myApp.getActivity().errorTransaction(drug, trans);
		            }
		        });
        	}
        }

        return view;
        
    }
 
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
 
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        /* used to make the notifyDataSetChanged() method work */
        super.registerDataSetObserver(observer);
    }

}
