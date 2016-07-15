package com.siliconorchard.notification.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.adapter.AdapterContactList;
import com.siliconorchard.notification.model.HostInfo;
import com.siliconorchard.notification.singleton.GlobalDataHolder;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;

import java.util.List;

public class MainActivity extends Activity {

    private TextView mTvTitle;
    private LinearLayout mLayoutBack;

    private List<HostInfo> mListHostInfo;

    private SharedPreferences mSharedPref;
    private String myIpAddress;
    private ListView mLvRecipientList;

    private AdapterContactList adapterContactList;

    private TextView mTvNoOnline;

    private ImageView mIvLeft;
    private ImageView mIvRight;
    private LinearLayout mLayoutSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListeners();
    }

    private void initView() {
        mSharedPref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        myIpAddress = Utils.getDeviceIpAddress();


        mTvTitle = (TextView) findViewById(R.id.tv_title_name);
        mLayoutBack = (LinearLayout) findViewById(R.id.layout_back);
        mTvTitle.setText(R.string.contact_list);

        mLvRecipientList = (ListView) findViewById(R.id.lv_recipient_list);
        mTvNoOnline = (TextView) findViewById(R.id.tv_no_online);

        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        mLayoutSettings = (LinearLayout) findViewById(R.id.layout_right);
        mIvRight.setImageResource(R.drawable.ic_settings);
        initContactList();
    }

    private void initListeners() {
        mLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityContactList.this.finish();
            }
        });
        /*mLvRecipientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HostInfo hostInfo = mListHostInfo.get(position);
                sendChatRequestMessage(hostInfo);
                hostInfo.setIsOnline(false);
                Intent intent = new Intent(ActivityContactList.this, ActivityChatOne2One.class);
                intent.putExtra(Constant.KEY_HOST_INFO, hostInfo);
                intent.putExtra(Constant.KEY_CHANNEL_NUMBER, 0);
                intent.putExtra(Constant.KEY_MY_IP_ADDRESS, myIpAddress);
                startActivity(intent);
            }
        });*/
        mLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        mLvRecipientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showUserDetails(position);
            }
        });
    }

    private void showUserDetails(int position) {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra(Constant.KEY_HOST_INFO, mListHostInfo.get(position));
        intent.putExtra(Constant.KEY_IMAGE_BITMAP, GlobalDataHolder.getInstance().getListProfilePic().get(position));
        startActivity(intent);
    }

    private void initContactList() {
        mListHostInfo = GlobalDataHolder.getInstance().getListHostInfo();
        adapterContactList = new AdapterContactList(this, mListHostInfo, GlobalDataHolder.getInstance().getListProfilePic());
        mLvRecipientList.setAdapter(adapterContactList);
        if(mListHostInfo == null || mListHostInfo.size()<1) {
            mTvNoOnline.setVisibility(View.VISIBLE);
            mLvRecipientList.setVisibility(View.GONE);
        } else {
            mTvNoOnline.setVisibility(View.GONE);
            mLvRecipientList.setVisibility(View.VISIBLE);
        }
    }

    private void updateContactList() {
        if(mListHostInfo == null) {
            initContactList();
            return;
        }
        mListHostInfo = GlobalDataHolder.getInstance().getListHostInfo();
        adapterContactList.notifyDataSetChanged();
        if(mListHostInfo == null || mListHostInfo.size()<1) {
            mTvNoOnline.setVisibility(View.VISIBLE);
            mLvRecipientList.setVisibility(View.GONE);
        } else {
            mTvNoOnline.setVisibility(View.GONE);
            mLvRecipientList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(Constant.RECEIVER_NOTIFICATION_CONTACT_LIST_MODIFIED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                boolean isModified = bundle.getBoolean(Constant.KEY_IS_CONTACT_MODIFIED);
                if(isModified) {
                    updateContactList();
                }
            }
        }
    };
}
