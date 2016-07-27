package com.siliconorchard.notification.broadcastreceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;


import com.siliconorchard.notification.activity.MainFragmentActivity;
import com.siliconorchard.notification.asynctask.SendMessageAsync;
import com.siliconorchard.notification.model.ChatMessage;
import com.siliconorchard.notification.model.HostInfo;
import com.siliconorchard.notification.singleton.GlobalDataHolder;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;

import org.json.JSONException;

/**
 * Created by adminsiriconorchard on 4/18/16.
 */
public class ChatMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String message = bundle.getString(Constant.KEY_CLIENT_MESSAGE);
            if(message != null) {
                //Log.e("TAG_LOG","Message: "+message);
            } else {
                Log.e("TAG_LOG","Blank message found");
                return;
            }
            try {
                ChatMessage chatMessage = new ChatMessage(message);
                /*Activity currentActivity = ((AppController)context.getApplicationContext()).getCurrentActivity();
                if(currentActivity instanceof ChatActivity
                        && chatMessage.getType() != ChatMessage.TYPE_REQUEST_INFO
                        && chatMessage.getType() != ChatMessage.TYPE_CREATE_CHANNEL
                        && chatMessage.getType() != ChatMessage.TYPE_JOIN_CHANNEL) {
                    return;
                }*/
                processMessage(context, chatMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage(Context context, ChatMessage receivedMessage) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        String ipAddress = sharedPreferences.getString(Constant.KEY_MY_IP_ADDRESS, null);
        if(ipAddress == null) {
            Log.e("TAG_LOG", "Self Ip address not found");
            return;
        }
        HostInfo hostInfo = new HostInfo();
        hostInfo.setIpAddress(receivedMessage.getIpAddress());
        SendMessageAsync sendMessageAsync = new SendMessageAsync();

        switch (receivedMessage.getType()) {
            case ChatMessage.TYPE_MESSAGE:
                //Toast.makeText(context, "New message arrived", Toast.LENGTH_LONG).show();
                break;
            case ChatMessage.TYPE_ADD_CLIENT:
                break;
            case ChatMessage.TYPE_REQUEST_INFO:
                //Toast.makeText(context, "New message arrived", Toast.LENGTH_LONG).show();
                try {
                    HostInfo hInfo = Utils.getHostInfoFromChatMessage(receivedMessage);
                    if(GlobalDataHolder.getInstance().addToHostList(hInfo, Utils.base64StringToBitmap(receivedMessage.getBase64Image()))) {
                        publishContactModifyNotification(context);
                    }
                    ChatMessage sendingMessage = generateChatMessage(sharedPreferences, ipAddress, ChatMessage.TYPE_RECEIVE_INFO, 0);
                    if(GlobalDataHolder.getInstance() != null) {
                        Bitmap bitmap = GlobalDataHolder.getInstance().getProfilePicBitmap();
                        if(bitmap != null) {
                            sendingMessage.setBase64Image(Utils.bitmapToBase64String(bitmap));
                        } else {
                            sendingMessage.setBase64Image("");
                        }
                    } else {
                        sendingMessage.setBase64Image("");
                    }
                    Log.e("TAG_LOG", sendingMessage.getJsonString());
                    sendMessageAsync.execute(hostInfo, sendingMessage.getJsonString());

                    publishSimilarStatusChatNotification(context, receivedMessage.getStatusChannel(), sharedPreferences);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ChatMessage.TYPE_RECEIVE_INFO:
                try {
                    HostInfo hInfo = Utils.getHostInfoFromChatMessage(receivedMessage);
                    if(GlobalDataHolder.getInstance().addToHostList(hInfo, Utils.base64StringToBitmap(receivedMessage.getBase64Image()))) {
                        publishContactModifyNotification(context);
                    }

                    publishSimilarStatusChatNotification(context, receivedMessage.getStatusChannel(), sharedPreferences);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            case ChatMessage.TYPE_UPDATED_INFO:
                try {
                    HostInfo hInfo = Utils.getHostInfoFromChatMessage(receivedMessage);
                    GlobalDataHolder.getInstance().updateHostList(hInfo, Utils.base64StringToBitmap(receivedMessage.getBase64Image()));
                    publishContactModifyNotification(context);

                    publishSimilarStatusChatNotification(context, receivedMessage.getStatusChannel(), sharedPreferences);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ChatMessage.TYPE_LEFT_APPLICATION:
                try {
                    HostInfo hInfo = Utils.getHostInfoFromChatMessage(receivedMessage);
                    if(GlobalDataHolder.getInstance().removeFromHostList(hInfo)) {
                        publishContactModifyNotification(context);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ChatMessage.TYPE_ONE_TO_ONE_CHAT_REQUEST: {
                try {
                    HostInfo hInfo = Utils.getHostInfoFromChatMessage(receivedMessage);
                    publishChatRequestNotification(context, hInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case ChatMessage.TYPE_ONE_TO_ONE_CHAT_ACCEPT: {
                try {
                    HostInfo hInfo = Utils.getHostInfoFromChatMessage(receivedMessage);
                    hInfo.setIsOnline(true);
                    publishChatAcceptNotification(context, hInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case ChatMessage.TYPE_ONE_TO_ONE_CHAT_DECLINE: {
                try {
                    HostInfo hInfo = Utils.getHostInfoFromChatMessage(receivedMessage);
                    publishChatAcceptNotification(context, hInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private ChatMessage generateChatMessage(SharedPreferences sharedPreferences, String ipAddress, int type, int channelNumber) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(type);
        chatMessage.setIpAddress(ipAddress);
        chatMessage.setDeviceId(sharedPreferences.getString(Constant.KEY_DEVICE_ID, Constant.DEVICE_ID_UNKNOWN));
        chatMessage.setDeviceName(sharedPreferences.getString(Constant.KEY_MY_DEVICE_NAME, Constant.DEVICE_ID_UNKNOWN));
        chatMessage.setStatus(sharedPreferences.getString(Constant.KEY_USER_STATUS, ""));
        chatMessage.setStatusChannel(sharedPreferences.getInt(Constant.KEY_STATUS_CHANNEL, -1));
        switch (type) {
            case ChatMessage.TYPE_CHANNEL_DUPLICATE:
                chatMessage.setChannelNumber(channelNumber);
                break;
        }
        return chatMessage;
    }

    private void publishContactModifyNotification(Context context) {
        Intent intentContactModified = new Intent(Constant.RECEIVER_NOTIFICATION_CONTACT_LIST_MODIFIED);
        intentContactModified.putExtra(Constant.KEY_IS_CONTACT_MODIFIED, true);
        context.sendBroadcast(intentContactModified);
    }

    private void publishChatRequestNotification(Context context, HostInfo hostInfo) {
        Intent intentContactModified = new Intent(Constant.RECEIVER_NOTIFICATION_CHAT_REQUEST);
        intentContactModified.putExtra(Constant.KEY_HOST_INFO, hostInfo);
        context.sendBroadcast(intentContactModified);
    }

    private void publishChatAcceptNotification(Context context, HostInfo hostInfo) {
        Intent intentContactModified = new Intent(Constant.RECEIVER_NOTIFICATION_CHAT_ACCEPT);
        intentContactModified.putExtra(Constant.KEY_HOST_INFO, hostInfo);
        context.sendBroadcast(intentContactModified);
    }

    private void publishSimilarStatusChatNotification(Context context, int clientStatusType, SharedPreferences sharedPreferences) {
        int myStatusType = sharedPreferences.getInt(Constant.KEY_STATUS_CHANNEL,-1);
        if(myStatusType != -1 && myStatusType == clientStatusType) {
            Intent intentContactModified = new Intent(Constant.RECEIVER_NOTIFICATION_SIMILAR_STATUS_CHAT);
            context.sendBroadcast(intentContactModified);
            MainFragmentActivity.setIsSimilarStatusFound(true);
        }
    }
}
