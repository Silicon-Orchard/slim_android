package com.siliconorchard.notification.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.model.HostInfo;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.widget.CircularImageView;

/**
 * Created by adminsiriconorchard on 7/15/16.
 */
public class UserDetailsActivity extends Activity {

    private TextView mTvTitle;
    private LinearLayout mLayoutBack;
    private ImageView mIvLeft;
    private LinearLayout mLayoutSettings;

    private TextView mTvUserName;
    private TextView mTvStatus;
    private CircularImageView mIvPic;

    private HostInfo mHostInfo;
    private Bitmap mProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        initView();
        initListener();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        mHostInfo = (HostInfo) bundle.getParcelable(Constant.KEY_HOST_INFO);
        mProfilePic = (Bitmap)bundle.getParcelable(Constant.KEY_IMAGE_BITMAP);

        mTvTitle = (TextView) findViewById(R.id.tv_title_name);
        mLayoutBack = (LinearLayout) findViewById(R.id.layout_back);
        mTvTitle.setText(R.string.settings);

        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mLayoutSettings = (LinearLayout) findViewById(R.id.layout_right);
        mIvLeft.setImageResource(R.drawable.ic_back);
        mLayoutSettings.setVisibility(View.INVISIBLE);

        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mIvPic = (CircularImageView) findViewById(R.id.iv_profile_pic);

        fillViewWithData();

    }

    private void fillViewWithData() {
        mTvTitle.setText(mHostInfo.getDeviceName());
        mTvUserName.setText(mHostInfo.getDeviceName());
        if(mHostInfo.getStatus() == null || mHostInfo.getStatus().length()<1) {
            mTvStatus.setText("No status found");
        } else {
            mTvStatus.setText(mHostInfo.getStatus());
        }

        if(mProfilePic != null) {
            mIvPic.setImageBitmap(mProfilePic);
        } else {
            mIvPic.setImageResource(R.drawable.ic_pp);
        }
    }

    private void initListener() {
        mLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetailsActivity.this.finish();
            }
        });
    }
}
