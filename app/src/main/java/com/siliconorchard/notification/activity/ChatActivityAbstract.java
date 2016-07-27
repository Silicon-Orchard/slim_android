package com.siliconorchard.notification.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.adapter.AdapterChatHistory;
import com.siliconorchard.notification.model.ChatMessage;
import com.siliconorchard.notification.utilities.Constant;

import org.json.JSONException;

/**
 * Created by adminsiriconorchard on 5/3/16.
 */
public abstract class ChatActivityAbstract extends ChatActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        initView();
        initListeners();
    }

    private void initView() {
        mSharedPref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        myIpAddress = bundle.getString(Constant.KEY_MY_IP_ADDRESS, null);
        channelNumber = bundle.getInt(Constant.KEY_CHANNEL_NUMBER, 0);

        mLvChatHistory = (ListView) findViewById(R.id.lv_chat_history);
        mTvTitle = (TextView) findViewById(R.id.tv_title_name);
        mLayoutBack = (LinearLayout) findViewById(R.id.layout_back);
        mTvTitle.setText("Channel: " + channelNumber);

        mEtChat = (EditText) findViewById(R.id.et_chat);
        mBtnSend = (ImageView) findViewById(R.id.btn_send);

        mTvRecipientList = (TextView) findViewById(R.id.lv_recipient_list);
        initChatHistoryList();

        initSubView(bundle);
    }

    private void initChatHistoryList() {
        adapterChatHistory = new AdapterChatHistory(this, null);
        mLvChatHistory.setAdapter(adapterChatHistory);
    }

    private void initListeners() {
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasRecipient()) {
                    Toast.makeText(ChatActivityAbstract.this, "There are no recipient to this chat room", Toast.LENGTH_LONG).show();
                    return;
                }
                String msg = mEtChat.getText().toString().trim();
                if (msg == null || msg.length() <= 0) {
                    return;
                }
                addChatMessage("Me", msg, true, null);
                mEtChat.setText("");
                try {
                    sendBroadCastMessage(generateChatMessage(msg).getJsonString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        mLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivityAbstract.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(Constant.SERVICE_NOTIFICATION_STRING_CHAT_FOREGROUND));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendChannelLeftMessage();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String message = bundle.getString(Constant.KEY_CLIENT_MESSAGE);
                try {
                    ChatMessage chatMessage = new ChatMessage(message);
                    if (channelNumber != chatMessage.getChannelNumber()) {
                        return;
                    }
                    switch (chatMessage.getType()) {
                        case ChatMessage.TYPE_MESSAGE:
                            addChatMessage(chatMessage.getDeviceName(), chatMessage.getMessage(), false, null);
                            addToReceiverList(getHostInfoFromChatMessage(chatMessage));
                            break;
                        case ChatMessage.TYPE_JOIN_CHANNEL:
                            processJoinChannelMessage(chatMessage);
                            break;
                        case ChatMessage.TYPE_LEFT_CHANNEL:
                            removeFromReceiverList(getHostInfoFromChatMessage(chatMessage));
                            break;

                        case ChatMessage.TYPE_CHANNEL_FOUND:
                            updateRecipientList(chatMessage);
                            break;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    protected abstract int getLayoutID();
    protected abstract void initSubView(Bundle bundle);
    protected abstract void processJoinChannelMessage(ChatMessage chatMessage);
    protected abstract void updateRecipientList(ChatMessage chatMessage);
    protected abstract boolean hasRecipient();
}
