package com.siliconorchard.notification.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;


/**
 * Created by ASUS on 7/31/2015.
 */
public abstract class FragmentBase extends Fragment {
    protected SharedPreferences mSharedPref;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, container, savedInstanceState);
        Utils.hideSoftKeyboard(getActivity());
        mSharedPref = getActivity().getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        initView(view);
        initListeners();
        return view;
    }

    @Nullable
    protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract void initView(View view);
    protected abstract void initListeners();
}
