package com.siliconorchard.notification.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.fragments.container.FragmentContainerMain;
import com.siliconorchard.notification.model.HostInfo;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.widget.CircularImageView;

/**
 * Created by adminsiriconorchard on 7/25/16.
 */
public class FragmentViewProfile extends FragmentBase {
    private LinearLayout mLayoutBack;
    private CircularImageView mCivProfile;
    private TextView mTvName;
    private TextView mTvStatus;


    @Nullable
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_profile, container, false);
    }

    @Override
    protected void initView(View view) {
        Bundle bundle = getArguments();
        HostInfo hostInfo = (HostInfo) bundle.getParcelable(Constant.KEY_HOST_INFO);
        Bitmap bitmap = (Bitmap) bundle.getParcelable(Constant.KEY_IMAGE_BITMAP);
        mLayoutBack = (LinearLayout) view.findViewById(R.id.layout_back);
        mCivProfile = (CircularImageView) view.findViewById(R.id.civ_profile);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mTvStatus = (TextView) view.findViewById(R.id.tv_status);
        mCivProfile.setImageBitmap(bitmap);
        mTvName.setText(hostInfo.getDeviceName());
        mTvStatus.setText(hostInfo.getStatus());

    }

    @Override
    protected void initListeners() {
        mLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentContainerMain) getParentFragment()).popFragment();
            }
        });
    }
}
