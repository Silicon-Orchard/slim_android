package com.siliconorchard.notification.singleton;

import android.graphics.Bitmap;

import com.siliconorchard.notification.model.HostInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by adminsiriconorchard on 4/20/16.
 */
public class GlobalDataHolder {
    private static GlobalDataHolder mInstance;
    private List<HostInfo> mListHostInfo;
    private Bitmap mProfilePicBitmap;
    private List<Bitmap> mListProfilePic;

    private GlobalDataHolder() {

    }

    public static GlobalDataHolder getInstance() {
        if(mInstance == null) {
            mInstance = new GlobalDataHolder();
        }
        return mInstance;
    }


    public List<HostInfo> getListHostInfo() {
        return mListHostInfo;
    }

    public void setListHostInfo(List<HostInfo> listHostInfo) {
        this.mListHostInfo = listHostInfo;
    }

    public boolean addToHostList(HostInfo hostInfo, Bitmap bitmap) {
        if(mListHostInfo == null) {
            mListHostInfo = new ArrayList<>();
            mListProfilePic = new ArrayList<>();
        }
        for(ListIterator<HostInfo> listIterator = mListHostInfo.listIterator(); listIterator.hasNext();) {
            if(listIterator.next().getIpAddress().equals(hostInfo.getIpAddress())) {
                return false;
            }
        }
        mListHostInfo.add(hostInfo);
        mListProfilePic.add(bitmap);
        return true;
    }

    public void updateHostList(HostInfo hostInfo, Bitmap bitmap) {
        if(mListHostInfo == null) {
            mListHostInfo = new ArrayList<>();
            mListProfilePic = new ArrayList<>();
            mListHostInfo.add(hostInfo);
            mListProfilePic.add(bitmap);
            return;
        }
        int position = 0;
        for(ListIterator<HostInfo> listIterator = mListHostInfo.listIterator(); listIterator.hasNext();) {
            if(listIterator.next().getIpAddress().equals(hostInfo.getIpAddress())) {
                break;
            }
            position++;
        }
        if(position < mListHostInfo.size()) {
            mListHostInfo.set(position, hostInfo);
            mListProfilePic.set(position, bitmap);
        } else {
            mListHostInfo.add(hostInfo);
            mListProfilePic.add(bitmap);
        }
    }

    public boolean removeFromHostList(HostInfo hostInfo) {
        if(mListHostInfo == null) {
            return false;
        }
        int position = 0;
        for(ListIterator<HostInfo> listIterator = mListHostInfo.listIterator(); listIterator.hasNext();) {
            if(listIterator.next().getIpAddress().equals(hostInfo.getIpAddress())) {
                listIterator.remove();
                if(position<mListProfilePic.size()) {
                    mListProfilePic.remove(position);
                }
                return true;
            }
            position++;
        }
        return false;
    }


    public Bitmap getProfilePicBitmap() {
        return mProfilePicBitmap;
    }

    public void setProfilePicBitmap(Bitmap profilePicBitmap) {
        this.mProfilePicBitmap = profilePicBitmap;
    }

    public List<Bitmap> getListProfilePic() {
        return mListProfilePic;
    }

    public void setListProfilePic(List<Bitmap> mListProfilePic) {
        this.mListProfilePic = mListProfilePic;
    }
}
