package com.siliconorchard.notification.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.activity.ActivityChatPublicChannel;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;

/**
 * Created by adminsiriconorchard on 6/14/16.
 */
public class DialogChatRequest extends DialogFragment implements
        View.OnClickListener {

    private View rootView;
    private TextView mTvChatReq;
    private TextView mBtnAccept;
    private TextView mBtnDecline;

    private SharedPreferences mSharedPref;

    private OnDismissListener mOnDismissListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_chat_request, container, false);
        initView();
        initListener();
        return rootView;
    }

    private void initView() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        mSharedPref = getActivity().getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mTvChatReq = (TextView) rootView.findViewById(R.id.tv_chat_req);
        mBtnAccept = (TextView) rootView.findViewById(R.id.tv_yes);
        mBtnDecline = (TextView) rootView.findViewById(R.id.tv_no);
    }

    private void initListener() {
        mBtnAccept.setOnClickListener(this);
        mBtnDecline.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_yes:
                startChatActivity();
                dismiss();
                break;
            case R.id.tv_no:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void startChatActivity() {
        Intent chatActivity = new Intent(getActivity(), ActivityChatPublicChannel.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_CHANNEL_NUMBER, mSharedPref.getInt(Constant.KEY_STATUS_CHANNEL,-1));
        bundle.putString(Constant.KEY_MY_IP_ADDRESS, Utils.getDeviceIpAddress());
        chatActivity.putExtras(bundle);
        startActivity(chatActivity);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public interface OnDismissListener {
        public void onDismiss();
    }
}