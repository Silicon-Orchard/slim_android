package com.siliconorchard.notification.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.utilities.Constant;

/**
 * Created by adminsiriconorchard on 7/26/16.
 */
public class AdapterChatRoom extends BaseAdapter {

    private String[] statuses = Constant.STATUSES;

    private static final int[] ICON_IDS = {
            R.drawable.ic_chat_ping_pong,
            R.drawable.ic_chat_chess,
            R.drawable.ic_chat_lunch,
            R.drawable.ic_chat_coffee,
            R.drawable.ic_chat_pool,
            R.drawable.ic_chat_football,
            R.drawable.ic_chat_board_game,
            R.drawable.ic_chat_hangout,
            R.drawable.ic_chat_walk,
            R.drawable.ic_chat_run
    };

    private LayoutInflater mInflater;
    public AdapterChatRoom(Activity activity) {
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
        View view = mInflater.inflate(R.layout.item_chat_room, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_status);
        textView.setText(statuses[position]);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_status);
        imageView.setImageResource(ICON_IDS[position]);
        return view;
    }
}
