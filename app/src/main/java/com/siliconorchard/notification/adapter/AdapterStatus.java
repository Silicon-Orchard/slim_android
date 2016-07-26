package com.siliconorchard.notification.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.utilities.Constant;

/**
 * Created by adminsiriconorchard on 7/26/16.
 */
public class AdapterStatus extends BaseAdapter {

    private String[] statuses = Constant.STATUSES;

    private LayoutInflater mInflater;
    public AdapterStatus(Activity activity) {
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return statuses.length;
    }

    @Override
    public Object getItem(int position) {
        return statuses[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_status, null);
        TextView textView = (TextView) view.findViewById(R.id.et_status);
        textView.setText(statuses[position]);
        return view;
    }
}
