package com.ving.narcoticinventory;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DrugListCountAdapter extends ArrayAdapter<String> {
	
	private CountObject countItems = null;
	private Context mContext = null;
	
	private static class ViewHolder {
        private TextView itemView;
    }
	
	public DrugListCountAdapter(Context context, CountObject co) {
        super(context, android.R.layout.simple_list_item_1, co.allTitles());
        mContext = context;
        countItems = co;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(android.R.id.text1);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (countItems.title(position) != null) {
            // My layout has only one TextView
                // do whatever you want with your string and long
            viewHolder.itemView.setText(countItems.title(position));
            if (countItems.color(position) != null) {
            	convertView.setBackgroundColor(countItems.color(position));
            } else {
            	convertView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        return convertView;
    }

}
