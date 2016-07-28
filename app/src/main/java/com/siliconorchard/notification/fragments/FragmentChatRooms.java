package com.siliconorchard.notification.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.activity.ActivityChatPublicChannel;
import com.siliconorchard.notification.adapter.AdapterChatRoom;
import com.siliconorchard.notification.adapter.AdapterStatus;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;

/**
 * Created by adminsiriconorchard on 7/28/16.
 */
public class FragmentChatRooms extends FragmentBase{

    private ListView mLvChatRooms;

    @Nullable
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_rooms, container, false);
    }

    @Override
    protected void initView(View view) {
        mLvChatRooms = (ListView) view.findViewById(R.id.lv_chat_rooms);
        AdapterChatRoom adapterStatus = new AdapterChatRoom(getActivity());
        mLvChatRooms.setAdapter(adapterStatus);
    }

    @Override
    protected void initListeners() {
        mLvChatRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startChatActivity(position);
            }
        });
    }

    private void startChatActivity(int position) {
        Intent chatActivity = new Intent(getActivity(), ActivityChatPublicChannel.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_CHANNEL_NUMBER, position);
        bundle.putString(Constant.KEY_MY_IP_ADDRESS, Utils.getDeviceIpAddress());
        chatActivity.putExtras(bundle);
        startActivity(chatActivity);
    }
}
