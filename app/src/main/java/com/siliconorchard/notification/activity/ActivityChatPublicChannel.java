package com.siliconorchard.notification.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.model.ChatMessage;
import com.siliconorchard.notification.model.HostInfo;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;

import org.json.JSONException;

/**
 * Created by adminsiriconorchard on 4/29/16.
 */
public class ActivityChatPublicChannel extends ChatActivityAbstract{

    private String channelName;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initSubView(Bundle bundle) {
        channelName = Constant.STATUSES[channelNumber];
        mTvTitle.setText(channelName);
        mEtChat = (EditText) findViewById(R.id.et_chat);
        //mBtnSend = (Button) findViewById(R.id.btn_send);
        //mTvClientMsg.setText(" ");
        //mTvRecipientList.setText("You joined\n");
        HostInfo channelHost = new HostInfo();
        channelHost.setDeviceName(Utils.getDeviceName(mSharedPref));
        channelHost.setIpAddress(myIpAddress);
        channelHost.setDeviceId(Utils.getDeviceId(this, mSharedPref));
        addToReceiverList(channelHost);
        joinChannel();
    }

    @Override
    protected void processJoinChannelMessage(ChatMessage chatMessage) {
        HostInfo hostInfo = getHostInfoFromChatMessage(chatMessage);
        addToReceiverList(hostInfo);
        sendChannelFoundMessage();
    }

    @Override
    protected void updateRecipientList(ChatMessage chatMessage) {
        HostInfo hostInfo = getHostInfoFromChatMessage(chatMessage);
        addToReceiverList(hostInfo);
    }

    private void joinChannel() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.TYPE_JOIN_CHANNEL);
        chatMessage.setIpAddress(myIpAddress);
        chatMessage.setDeviceName(Utils.getDeviceName(mSharedPref));
        chatMessage.setDeviceId(Utils.getDeviceId(this, mSharedPref));
        chatMessage.setChannelNumber(channelNumber);

        try {
            Utils.sendBroadCastMessageToRegisteredClients(chatMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean hasRecipient() {
        if(mListHostInfo == null || mListHostInfo.size() < 1) {
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
