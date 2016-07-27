package com.siliconorchard.notification.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.siliconorchard.notification.adapter.AdapterChatHistory;
import com.siliconorchard.notification.asynctask.SendMessageAsync;
import com.siliconorchard.notification.model.ChatMessage;
import com.siliconorchard.notification.model.ChatMessageHistory;
import com.siliconorchard.notification.model.HostInfo;
import com.siliconorchard.notification.utilities.Utils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adminsiriconorchard on 6/17/16.
 */
public abstract class ChatActivityBase extends Activity {

    protected TextView mTvTitle;
    protected LinearLayout mLayoutBack;

    protected EditText mEtChat;
    protected ImageView mBtnSend;

    protected List<HostInfo> mListHostInfo;

    protected SharedPreferences mSharedPref;
    protected String myIpAddress;

    //protected TextView mTvRecipientList;
    protected TextView mTvRecipientList;
    protected int channelNumber;

    protected ListView mLvChatHistory;
    protected AdapterChatHistory adapterChatHistory;

    protected void addChatMessage(String name, String msg, boolean isSent, String filePath) {
        ChatMessageHistory chatMessage = new ChatMessageHistory();
        chatMessage.setDeviceName(name);
        chatMessage.setMessage(msg);
        chatMessage.setIsSent(isSent);
        adapterChatHistory.addMessage(chatMessage);
        mLvChatHistory.post(new Runnable() {
            public void run() {
                mLvChatHistory.setSelection(mLvChatHistory.getCount() - 1);
            }
        });
    }

    protected ChatMessage generateChatMessage(String message) {
        ChatMessage chatMessage = generateChatMessage();
        chatMessage.setType(ChatMessage.TYPE_MESSAGE);
        chatMessage.setMessage(message);
        return chatMessage;
    }

    protected ChatMessage generateChatMessage(List<HostInfo> clientList) {
        ChatMessage chatMessage = generateChatMessage();
        chatMessage.setType(ChatMessage.TYPE_CHANNEL_FOUND);
        chatMessage.setClientList(clientList);
        return chatMessage;
    }

    protected ChatMessage generateChatMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setIpAddress(myIpAddress);
        chatMessage.setDeviceId(Utils.getDeviceId(this, mSharedPref));
        chatMessage.setDeviceName(Utils.getDeviceName(mSharedPref));
        chatMessage.setChannelNumber(channelNumber);
        return chatMessage;
    }

    protected HostInfo getHostInfoFromChatMessage(ChatMessage chatMessage) {
        HostInfo hostInfo = new HostInfo();
        hostInfo.setIpAddress(chatMessage.getIpAddress());
        hostInfo.setDeviceId(chatMessage.getDeviceId());
        hostInfo.setDeviceName(chatMessage.getDeviceName());
        return hostInfo;
    }

    protected boolean addToReceiverList(HostInfo hostInfo) {
        if (mListHostInfo == null) {
            mListHostInfo = new ArrayList<>();
        }
        for (int i = 0; i < mListHostInfo.size(); i++) {
            HostInfo hostInfoInList = mListHostInfo.get(i);
            if (hostInfoInList.getIpAddress().equals(hostInfo.getIpAddress())) {
                hostInfoInList.setIsOnline(true);
                updateRecipientInfo();
                return false;
            }
        }
        if (hostInfo.getIpAddress() != null && hostInfo.getIpAddress().length() > 1) {
            hostInfo.setIsOnline(true);
            mListHostInfo.add(hostInfo);
            updateRecipientInfo();
            return true;
        }
        return false;
    }

    protected boolean removeFromReceiverList(HostInfo hostInfo) {
        if (mListHostInfo == null) {
            return false;
        }
        for (int i = 0; i < mListHostInfo.size(); i++) {
            HostInfo hostInfoInList = mListHostInfo.get(i);
            if (hostInfoInList.getIpAddress().equals(hostInfo.getIpAddress())) {
                mListHostInfo.get(i).setIsOnline(false);
                updateRecipientInfo();
                return true;
            }
        }
        return false;
    }

    protected void updateRecipientInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        HostInfo hostInfo;
        for (int i = 0; i<mListHostInfo.size()-1; i++) {
            hostInfo = mListHostInfo.get(i);
            if(hostInfo.isOnline()) {
                stringBuilder.append(hostInfo.getDeviceName());
                stringBuilder.append(" | ");
            }
        }
        hostInfo = mListHostInfo.get(mListHostInfo.size()-1);
        if(hostInfo.isOnline()) {
            stringBuilder.append(hostInfo.getDeviceName());
        }
        mTvRecipientList.setText(stringBuilder.toString());
    }

    protected void sendChannelLeftMessage() {
        ChatMessage chatMessage = generateChatMessage();
        chatMessage.setType(ChatMessage.TYPE_LEFT_CHANNEL);
        try {
            sendBroadCastMessage(chatMessage.getJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void sendChannelFoundMessage() {
        List<HostInfo> clientList = new ArrayList<>();
        ChatMessage chatMessage = generateChatMessage(clientList);
        try {
            String message = chatMessage.getJsonString();
            sendBroadCastMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void sendBroadCastMessage(String message) {
        if (mListHostInfo != null && mListHostInfo.size() > 0) {
            for (int i = 0; i < mListHostInfo.size(); i++) {
                HostInfo receiver = mListHostInfo.get(i);
                if (!receiver.getIpAddress().equals(myIpAddress)) {
                    SendMessageAsync sendMessageAsync = new SendMessageAsync();
                    sendMessageAsync.execute(receiver, message);
                }
            }
        }
    }
}
