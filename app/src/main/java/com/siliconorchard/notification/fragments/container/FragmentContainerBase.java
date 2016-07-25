package com.siliconorchard.notification.fragments.container;

/**
 * Created by ASUS on 7/31/2015.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.siliconorchard.notification.R;

public abstract class FragmentContainerBase extends Fragment {

    protected boolean mIsViewInitiated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!mIsViewInitiated) {
            initialiseView();
        }
    }



    /**
     * Replace fragment and add to the back stack according to condition
     * @param fragment {Fragment to replace}
     * @param addToBackStack {Boolean value whether this fragment should be added in back stack or not}
     */
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        Log.e("TAG_LOG", "replaceFragment()");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container_framelayout, fragment);
        transaction.commit();
        getChildFragmentManager().executePendingTransactions();
    }

    /**
     * Pop fragment from the fragment back stack
     * @return
     */
    public boolean popFragment() {
        Log.e("TAG_LOG", "pop fragment: " + getChildFragmentManager().getBackStackEntryCount());
        boolean isPop = false;
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            isPop = true;
            getChildFragmentManager().popBackStack();
        }
        return isPop;
    }


    protected abstract void initialiseView();
}