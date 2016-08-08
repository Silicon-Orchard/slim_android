package com.siliconorchard.notification.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.siliconorchard.notification.R;

/**
 * Created by adminsiriconorchard on 8/8/16.
 */
public class PopupWifiNotFound  extends PopupBase {
    private Button mBtnOk;

    @Nullable
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setCancelable(false);
        return inflater.inflate(R.layout.popup_wifi_not_found, container, false);
    }

    @Override
    protected void initView(View view) {
        mBtnOk = (Button) view.findViewById(R.id.btn_ok);
    }

    @Override
    protected void initListeners() {
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnDismissListener != null) {
                    mOnDismissListener.onDismiss();
                }
                dismiss();
            }
        });
    }

    private OnDismissListener mOnDismissListener;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public static interface OnDismissListener{
        public void onDismiss();
    }
}
