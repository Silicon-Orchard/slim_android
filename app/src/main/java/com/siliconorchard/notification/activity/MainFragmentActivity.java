package com.siliconorchard.notification.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.dialog.DialogChatRequest;
import com.siliconorchard.notification.fragments.FragmentMain;
import com.siliconorchard.notification.fragments.FragmentProfile;
import com.siliconorchard.notification.fragments.container.FragmentContainerBase;
import com.siliconorchard.notification.fragments.container.FragmentContainerMain;
import com.siliconorchard.notification.model.ChatMessage;
import com.siliconorchard.notification.service.ServiceServer;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;

import org.json.JSONException;

/**
 * Created by adminsiriconorchard on 7/22/16.
 */
public class MainFragmentActivity extends FragmentActivity{

    //private TabLayout tabLayout;
    private RadioGroup mRgTab;

    private RadioButton mRbContact;
    private RadioButton mRbProfile;
    private boolean isChatNotificationShown;

    private String ipAddress;
    private SharedPreferences mSharedPref;

    private static Boolean isSimilarStatusFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        initView();
        initListener();
    }

    private void initView() {
        //tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //initTabLayout();
        mSharedPref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        mRgTab = (RadioGroup) findViewById(R.id.rg_tab);
        mRbContact = (RadioButton) findViewById(R.id.rb_contact_list);
        mRbProfile = (RadioButton) findViewById(R.id.rb_profile);
        mRbContact.setChecked(true);
        ipAddress = Utils.getDeviceIpAddress();
        initFragment();
    }

    protected void initFragment() {
        Fragment fragment = new FragmentContainerMain();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, fragment).commit();
    }

    private void initListener() {
        /*tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment fragment = null;
                if(position == 0) {
                    fragment = new FragmentMain();
                } else {
                    fragment = new FragmentProfile();
                }
                FragmentContainerBase fragmentContainerBase = (FragmentContainerBase) getSupportFragmentManager().getFragments().get(0);
                fragmentContainerBase.replaceFragment(fragment, false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
        mRgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = null;
                if (checkedId == R.id.rb_contact_list) {
                    fragment = new FragmentMain();
                } else {
                    fragment = new FragmentProfile();
                }
                FragmentContainerBase fragmentContainerBase = (FragmentContainerBase) getSupportFragmentManager().getFragments().get(0);
                fragmentContainerBase.replaceFragment(fragment, false);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiverChatReq, new IntentFilter(Constant.RECEIVER_NOTIFICATION_SIMILAR_STATUS_CHAT));
        if(isSimilarStatusFound != null && isSimilarStatusFound) {
            showChatRequestDialog();
        }
        isSimilarStatusFound = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverChatReq);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadCastLeftMessage();
        ServiceServer.closeSocket();
        isSimilarStatusFound = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendBroadCastLeftMessage() {
        try {
            ChatMessage chatMessage = generateChatMessageBasics();
            chatMessage.setType(ChatMessage.TYPE_LEFT_APPLICATION);
            Utils.sendBroadCastMessageToRegisteredClients(chatMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ChatMessage generateChatMessageBasics() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setDeviceId(Utils.getDeviceId(this, mSharedPref));
        chatMessage.setIpAddress(ipAddress);
        chatMessage.setDeviceName(Utils.getDeviceName(mSharedPref));
        return chatMessage;
    }

    public static void setIsSimilarStatusFound(Boolean isSimilarFound) {
        if(isSimilarStatusFound == null) {
            MainFragmentActivity.isSimilarStatusFound = isSimilarFound;
        }
    }

    private void showChatRequestDialog() {
        if(!isChatNotificationShown) {
            isChatNotificationShown = true;
            DialogChatRequest dialogChatRequest = new DialogChatRequest();
            dialogChatRequest.setOnDismissListener(new DialogChatRequest.OnDismissListener() {
                @Override
                public void onDismiss() {
                    isChatNotificationShown = false;
                }
            });
            dialogChatRequest.show(getFragmentManager(), "showChatRequestDialog");
        }
    }

    private BroadcastReceiver receiverChatReq = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAG_LOG","Similar status found");
            showChatRequestDialog();
        }
    };
}
