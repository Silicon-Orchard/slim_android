package com.siliconorchard.notification.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.adapter.AdapterContactList;
import com.siliconorchard.notification.fragments.container.FragmentContainerBase;
import com.siliconorchard.notification.model.HostInfo;
import com.siliconorchard.notification.singleton.GlobalDataHolder;
import com.siliconorchard.notification.utilities.Constant;

import java.util.List;

/**
 * Created by adminsiriconorchard on 7/22/16.
 */
public class FragmentMain extends FragmentBase {

    private List<HostInfo> mListHostInfo;

    private SharedPreferences mSharedPref;
    private String myIpAddress;
    private ListView mLvRecipientList;

    private AdapterContactList adapterContactList;

    private TextView mTvNoOnline;

    @Nullable
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    protected void initView(View view) {
        mLvRecipientList = (ListView) view.findViewById(R.id.lv_recipient_list);
        mTvNoOnline = (TextView) view.findViewById(R.id.tv_no_online);
        initContactList();
    }

    @Override
    protected void initListeners() {
        mLvRecipientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showUserDetails(position);
            }
        });
    }

    private void showUserDetails(int position) {
        Fragment fragment = new FragmentViewProfile();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.KEY_HOST_INFO, mListHostInfo.get(position));
        bundle.putParcelable(Constant.KEY_IMAGE_BITMAP, GlobalDataHolder.getInstance().getListProfilePic().get(position));
        fragment.setArguments(bundle);
        ((FragmentContainerBase) getParentFragment()).replaceFragment(fragment, true);
    }

    private void initContactList() {
        mListHostInfo = GlobalDataHolder.getInstance().getListHostInfo();
        adapterContactList = new AdapterContactList(getActivity(), mListHostInfo, GlobalDataHolder.getInstance().getListProfilePic());
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
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(Constant.RECEIVER_NOTIFICATION_CONTACT_LIST_MODIFIED));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
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
