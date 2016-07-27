package com.siliconorchard.notification.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.model.ChatMessageHistory;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adminsiriconorchard on 6/8/16.
 */
public class AdapterChatHistory extends BaseAdapter{

    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<ChatMessageHistory> mListChat;


    public AdapterChatHistory(Activity activity, List<ChatMessageHistory> chatMessageList) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mListChat = chatMessageList;

    }

    @Override
    public int getCount() {
        if(mListChat != null) {
            return mListChat.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mListChat != null) {
            return mListChat.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.item_chat_history, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
            viewHolder.tvMsg = (TextView)convertView.findViewById(R.id.tv_msg);
            viewHolder.llArrowImage = (LinearLayout) convertView.findViewById(R.id.ll_arrow);
            viewHolder.llReceived = (LinearLayout) convertView.findViewById(R.id.ll_received_space);
            viewHolder.llSent = (LinearLayout) convertView.findViewById(R.id.ll_sent_space);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChatMessageHistory chatMessage = mListChat.get(position);
        String name = chatMessage.getDeviceName();
        if(chatMessage.isSent()) {
            viewHolder.tvName.setGravity(Gravity.RIGHT);
            viewHolder.tvMsg.setGravity(Gravity.RIGHT);
            viewHolder.llReceived.setVisibility(View.GONE);
            viewHolder.llSent.setVisibility(View.VISIBLE);
            viewHolder.llArrowImage.setGravity(Gravity.RIGHT);
        } else {
            viewHolder.tvName.setGravity(Gravity.LEFT);
            viewHolder.tvMsg.setGravity(Gravity.LEFT);
            viewHolder.llReceived.setVisibility(View.VISIBLE);
            viewHolder.llSent.setVisibility(View.GONE);
            viewHolder.llArrowImage.setGravity(Gravity.LEFT);
        }
        viewHolder.tvName.setText(name+":");
        viewHolder.tvMsg.setText(chatMessage.getMessage());
        return convertView;
    }


    public void addMessage(ChatMessageHistory chatMessage) {
        if(mListChat == null) {
            mListChat = new ArrayList<>();
        }
        mListChat.add(chatMessage);
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView tvName;
        TextView tvMsg;
        LinearLayout llArrowImage;
        LinearLayout llReceived;
        LinearLayout llSent;
    }
}
