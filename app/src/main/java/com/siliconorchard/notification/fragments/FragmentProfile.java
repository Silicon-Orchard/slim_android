package com.siliconorchard.notification.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.dialog.PopupSelectStatus;
import com.siliconorchard.notification.model.ChatMessage;
import com.siliconorchard.notification.model.HostInfo;
import com.siliconorchard.notification.singleton.GlobalDataHolder;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;
import com.siliconorchard.notification.widget.CircularImageView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by adminsiriconorchard on 7/22/16.
 */
public class FragmentProfile extends FragmentSelectPictureBase {

    private CircularImageView mCivProfile;
    private TextView mTvTapChange;
    private TextView mTvName;
    private EditText mEtName;
    private ImageView mIvEditName;
    private TextView mTvStatus;
    private ImageView mIvEditStatus;
    private Button mBtnSave;

    private String ipAddress;
    private String mName;
    private String mStatus;
    private int statusChannel;

    @Nullable
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    protected void initView(View view) {
        mCivProfile = (CircularImageView) view.findViewById(R.id.civ_profile);
        mTvTapChange = (TextView) view.findViewById(R.id.tv_tap_change);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mEtName = (EditText) view.findViewById(R.id.et_name);
        mIvEditName = (ImageView) view.findViewById(R.id.iv_edit_name);
        mTvStatus = (TextView) view.findViewById(R.id.tv_status);
        mIvEditStatus = (ImageView) view.findViewById(R.id.iv_edit_status);
        mBtnSave = (Button) view.findViewById(R.id.btn_save);
        initTextAndImage();
    }

    private void initTextAndImage() {
        ipAddress = Utils.getDeviceIpAddress();
        mName = mSharedPref.getString(Constant.KEY_MY_DEVICE_NAME, "");
        mStatus = mSharedPref.getString(Constant.KEY_USER_STATUS, "");
        mTvName.setText(mName);
        mTvStatus.setText(mStatus);
        mEtName.setText(mName);
        statusChannel = mSharedPref.getInt(Constant.KEY_STATUS_CHANNEL, -1);
        if(GlobalDataHolder.getInstance().getProfilePicBitmap() != null) {
            mCivProfile.setImageBitmap(GlobalDataHolder.getInstance().getProfilePicBitmap());
        }
    }

    @Override
    protected void initListeners() {

        mCivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChange();
            }
        });

        mTvTapChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChange();
            }
        });

        mIvEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameEdit();
            }
        });
        mTvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameEdit();
            }
        });

        mIvEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus();
            }
        });
        mTvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus();
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postProfileUpdate();
            }
        });

        mTvStatus.setMovementMethod(new ScrollingMovementMethod());
    }

    private void showNameEdit() {
        if (mTvName.getVisibility() == View.GONE) {
            mName = mEtName.getText().toString();
            mTvName.setText(mName);
            mTvName.setVisibility(View.VISIBLE);
            mEtName.setVisibility(View.GONE);
        } else {
            mTvName.setVisibility(View.GONE);
            mEtName.setVisibility(View.VISIBLE);
        }
    }

    private void setStatus() {
        PopupSelectStatus popupSelectStatus = new PopupSelectStatus();
        popupSelectStatus.setOnStatusSelect(new PopupSelectStatus.OnStatusSelect() {
            @Override
            public void onStatusSelect(int position, String status) {
                mTvStatus.setText(status);
                statusChannel = position;
                Log.e("TAG_LOG", "Status channel: " + statusChannel);
            }
        });
        popupSelectStatus.show(getChildFragmentManager(), "");
    }

    private void imageChange() {
        selectPictureOption();
    }



    private void postProfileUpdate() {
        mSharedPref.edit().putString(Constant.KEY_MY_DEVICE_NAME, mEtName.getText().toString()).commit();
        mSharedPref.edit().putString(Constant.KEY_USER_STATUS, mTvStatus.getText().toString()).commit();
        mSharedPref.edit().putInt(Constant.KEY_STATUS_CHANNEL, statusChannel).commit();
        Log.e("TAG_LOG", "Status channel: " + statusChannel);
        if(mSelectedBitmap != null) {
            Utils.saveSaveImageBitmap(Constant.BASE_PATH + Constant.MY_PP, Constant.PROFILE_PIC_NAME, mSelectedBitmap);
            GlobalDataHolder.getInstance().setProfilePicBitmap(mSelectedBitmap);
        }
        sendBroadcastRequestInfo();
        Toast.makeText(getActivity(), "Saved Successfully!!!", Toast.LENGTH_LONG).show();
        publishSimilarStatusPopup();
        //this.finish();
    }

    private void publishSimilarStatusPopup() {
        if(statusChannel<0) {
            return;
        }
        List<HostInfo> hostInfoList = GlobalDataHolder.getInstance().getListHostInfo();
        if(hostInfoList != null) {
            for(int i = 0; i<hostInfoList.size(); i++) {
                if(hostInfoList.get(i).getStatusId() == statusChannel) {
                    Intent intentContactModified = new Intent(Constant.RECEIVER_NOTIFICATION_SIMILAR_STATUS_CHAT);
                    getActivity().sendBroadcast(intentContactModified);
                    return;
                }
            }
        }
    }

    private void processActivityResult(Intent data) {
        try {
            initUriAndFile(data, true);
            mSelectedBitmap = Utils.decodeImageFile(mSelectedFile, Constant.MINIMUM_IMAGE_SIZE);
            if(mSelectedBitmap != null) {
                mCivProfile.setImageBitmap(mSelectedBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ChatMessage generateChatMessageBasics() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setDeviceId(Utils.getDeviceId(getActivity(), mSharedPref));
        chatMessage.setIpAddress(ipAddress);
        chatMessage.setDeviceName(Utils.getDeviceName(mSharedPref));
        chatMessage.setStatus(mSharedPref.getString(Constant.KEY_USER_STATUS, ""));
        statusChannel = mSharedPref.getInt(Constant.KEY_STATUS_CHANNEL, -1);
        chatMessage.setStatusChannel(statusChannel);
        Log.e("TAG_LOG", "Status channel: " + statusChannel);
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
            Utils.sendBroadCastMessageToRegisteredClients(chatMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            processActivityResult(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
