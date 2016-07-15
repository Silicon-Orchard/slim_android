package com.siliconorchard.notification.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.dialog.PopupSelectPictureOption;
import com.siliconorchard.notification.model.ChatMessage;
import com.siliconorchard.notification.singleton.GlobalDataHolder;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;
import com.siliconorchard.notification.widget.CircularImageView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/**
 * Created by adminsiriconorchard on 7/14/16.
 */
public class SettingsActivity extends ActivitySelectFileAndPhotoBase {

    private SharedPreferences mSharedPref;
    private TextView mTvTitle;
    private LinearLayout mLayoutBack;
    private ImageView mIvLeft;
    private ImageView mIvRight;
    private LinearLayout mLayoutSettings;

    private EditText mEtUserName;
    private EditText mEtStatus;
    private Button mBtnSelectPic;
    private Button mBtnPost;
    private CircularImageView mIvPic;
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initView();
        initListener();
    }

    private void initView() {
        mSharedPref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        ipAddress = Utils.getDeviceIpAddress();
        mTvTitle = (TextView) findViewById(R.id.tv_title_name);
        mLayoutBack = (LinearLayout) findViewById(R.id.layout_back);
        mTvTitle.setText(R.string.settings);

        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        mLayoutSettings = (LinearLayout) findViewById(R.id.layout_right);
        mIvLeft.setImageResource(R.drawable.ic_back);
        mLayoutSettings.setVisibility(View.INVISIBLE);

        mEtUserName = (EditText) findViewById(R.id.et_user_name);
        mEtStatus = (EditText) findViewById(R.id.et_user_comment);

        mBtnSelectPic = (Button) findViewById(R.id.btn_select_pic);
        mBtnPost = (Button) findViewById(R.id.btn_post);
        mIvPic = (CircularImageView) findViewById(R.id.iv_selected_pic);

        initTextAndImage();
    }

    private void initTextAndImage() {
        mEtUserName.setText(mSharedPref.getString(Constant.KEY_MY_DEVICE_NAME, ""));
        mEtStatus.setText(mSharedPref.getString(Constant.KEY_USER_STATUS, ""));
        if(GlobalDataHolder.getInstance().getProfilePicBitmap() != null) {
            mIvPic.setImageBitmap(GlobalDataHolder.getInstance().getProfilePicBitmap());
            mIvPic.setVisibility(View.VISIBLE);
        } else {
            mIvPic.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        mLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });

        mBtnSelectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPictureOption();
            }
        });

        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postProfileUpdate();
            }
        });

    }

    private void postProfileUpdate() {
        mSharedPref.edit().putString(Constant.KEY_MY_DEVICE_NAME, mEtUserName.getText().toString()).commit();
        mSharedPref.edit().putString(Constant.KEY_USER_STATUS, mEtStatus.getText().toString()).commit();
        if(mSelectedBitmap != null) {
            Utils.saveSaveImageBitmap(Constant.BASE_PATH+Constant.MY_PP, Constant.PROFILE_PIC_NAME, mSelectedBitmap);
            GlobalDataHolder.getInstance().setProfilePicBitmap(mSelectedBitmap);
        }
        sendBroadcastRequestInfo();
        Toast.makeText(this, "Saved Successfully!!!", Toast.LENGTH_LONG).show();
        this.finish();
    }

    private void processActivityResult(Intent data) {
        try {
            initUriAndFile(data, true);
            mSelectedBitmap = Utils.decodeImageFile(mSelectedFile, 250);
            if(mSelectedBitmap != null) {
                mIvPic.setImageBitmap(mSelectedBitmap);
                mIvPic.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ChatMessage generateChatMessageBasics() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setDeviceId(Constant.DEVICE_ID_UNKNOWN);
        chatMessage.setIpAddress(ipAddress);
        chatMessage.setDeviceName(Utils.getDeviceName(mSharedPref));
        chatMessage.setStatus(mSharedPref.getString(Constant.KEY_USER_STATUS, ""));
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
            chatMessage.setType(ChatMessage.TYPE_UPDATED_INFO);
            Utils.sendBroadCastMessage(chatMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constant.REQUEST_CODE_SELECT_SINGLE_PICTURE && resultCode == Activity.RESULT_OK) {
            processActivityResult(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
