package com.siliconorchard.notification.fragments.container;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.siliconorchard.notification.fragments.FragmentMain;

import java.util.List;

/**
 * Created by ASUS on 7/31/2015.
 */
public class FragmentContainerMain extends FragmentContainerBase {
    private Fragment mFragment;

    @Override
    protected void initialiseView() {
        mIsViewInitiated = true;
        replaceFragment(new FragmentMain(), false);
        //replaceFragment(new FragmentSignUp(), false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG_LOG", "onActivityResult Fragment Container");
        List<Fragment> list = getChildFragmentManager().getFragments();
        Log.e("TAG_LOG", "Number of fragments" + list == null ? "NULL" : "" + list.size());
        if(list != null && list.size() > 0) {
            try {
                int reduceIndex = 1;
                Fragment fragment = list.get(list.size() - reduceIndex);
                while(fragment == null) {
                    reduceIndex++;
                    fragment = list.get(list.size() - reduceIndex);
                }
                fragment.onActivityResult(requestCode, resultCode, data);
            } catch (Exception e) {
                Log.e("TAG_LOG", "Fragment on activity result error occurred.");
                e.printStackTrace();
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
