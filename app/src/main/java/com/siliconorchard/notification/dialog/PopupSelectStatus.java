package com.siliconorchard.notification.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.adapter.AdapterStatus;
import com.siliconorchard.notification.utilities.Constant;

/**
 * Created by adminsiriconorchard on 7/26/16.
 */
public class PopupSelectStatus extends PopupBase {

    private ListView mLvStatus;
    private EditText mEtStatus;
    private Button mBtnOk;
    private LinearLayout mLayoutStatus;

    private OnStatusSelect mOnStatusSelect;

    private boolean isSave;


    @Nullable
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.popup_select_status, container, false);
    }

    @Override
    protected void initView(View view) {
        mLvStatus = (ListView) view.findViewById(R.id.lv_status);
        mEtStatus = (EditText) view.findViewById(R.id.et_status);
        mBtnOk = (Button) view.findViewById(R.id.btn_ok);
        mLayoutStatus = (LinearLayout) view.findViewById(R.id.layout_status);
        mLayoutStatus.setVisibility(View.GONE);
        AdapterStatus adapterStatus = new AdapterStatus(getActivity());
        mLvStatus.setAdapter(adapterStatus);
    }

    @Override
    protected void initListeners() {
        mLvStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSelectStatus(position);
            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSave) {
                    mOnStatusSelect.onStatusSelect(-1, mEtStatus.getText().toString());
                    dismiss();
                } else {
                    isSave = true;
                    mLvStatus.setVisibility(View.GONE);
                    mLayoutStatus.setVisibility(View.VISIBLE);
                    mBtnOk.setText(R.string.save);
                }
            }
        });
    }

    private void onSelectStatus(int position) {
        if(mOnStatusSelect != null) {
            mOnStatusSelect.onStatusSelect(position, Constant.STATUSES[position]);
        }
        this.dismiss();
    }

    public void setOnStatusSelect(OnStatusSelect onStatusSelect) {
        this.mOnStatusSelect = onStatusSelect;
    }

    public interface OnStatusSelect {
        public void onStatusSelect(int position, String status);
    }
}
