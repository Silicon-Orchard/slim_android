package com.siliconorchard.notification.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.model.ChatMessage;
import com.siliconorchard.notification.singleton.GlobalDataHolder;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/**
 * Created by adminsiriconorchard on 7/13/16.
 */
public class SplashActivity extends Activity {

    private String ipAddress;
    private SharedPreferences mSharedPref;
    private AlertDialog mAlertDialog;

    private ProgressBar mProgress;
    private static final int MAX_PROGRESS_BAR = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        mSharedPref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        ipAddress = Utils.getDeviceIpAddress();
        mProgress = (ProgressBar) findViewById(R.id.loading_bar);

        initApp();
    }

    private void initApp() {
        GlobalDataHolder.getInstance().setListHostInfo(null);
        Utils.startServerService(this);
        fetchProfilePicture();
    }

    private void fetchProfilePicture() {
        if(GlobalDataHolder.getInstance().getProfilePicBitmap() != null) {
            return;
        }
        try {
            Bitmap bitmap = Utils.decodeImageFile(Constant.BASE_PATH + Constant.MY_PP + File.separator + Constant.PROFILE_PIC_NAME, -1);
            if(bitmap != null) {
                GlobalDataHolder.getInstance().setProfilePicBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ipAddress == null || ipAddress.length()<5) {
            showWifiNotEnabledDialog();
        } else {
            sendBroadcastRequestInfo();
            showFakeProgress();
        }

    }



    private void showFakeProgress() {
        mProgress.setMax(MAX_PROGRESS_BAR);
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for(int i = 0; i<=MAX_PROGRESS_BAR; i++) {
                    try {
                        Thread.sleep(20L);
                        publishProgress(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                int progress = values[0];
                mProgress.setProgress(progress);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Intent main_intent = new Intent(SplashActivity.this, MainFragmentActivity.class);
                startActivity(main_intent);
            }
        }.execute();
    }

    private void showWifiNotEnabledDialog() {
        AlertDialog.Builder  builder = Utils.createAlertDialog(this, R.string.wifi_not_enabled,
                R.string.error_wifi_not_enabled_please_enable);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                    SplashActivity.this.finish();
                }
            }
        });
        mAlertDialog = builder.create();
        mAlertDialog.show();
        mAlertDialog.getWindow().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.shape_voice_activity_bg));
    }


    private ChatMessage generateChatMessageBasics() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setDeviceId(Constant.DEVICE_ID_UNKNOWN);
        chatMessage.setIpAddress(ipAddress);
        chatMessage.setDeviceName(Utils.getDeviceName(mSharedPref));
        chatMessage.setStatus(mSharedPref.getString(Constant.KEY_USER_STATUS, ""));
        chatMessage.setStatusChannel(mSharedPref.getInt(Constant.KEY_STATUS_CHANNEL,-1));
        if(GlobalDataHolder.getInstance().getProfilePicBitmap() != null) {
            chatMessage.setBase64Image(Utils.bitmapToBase64String(GlobalDataHolder.getInstance().getProfilePicBitmap()));
        } else {
            chatMessage.setBase64Image("");
        }
        return chatMessage;
    }


    private void sendBroadcastRequestInfo() {
        try {
            ChatMessage chatMessage = generateChatMessageBasics();
            chatMessage.setType(ChatMessage.TYPE_REQUEST_INFO);
            Utils.sendBroadCastMessage(chatMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
