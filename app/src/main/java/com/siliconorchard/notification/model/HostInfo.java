package com.siliconorchard.notification.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adminsiriconorchard on 4/18/16.
 */
public class HostInfo implements Parcelable{
    private String ipAddress;
    private String deviceId;
    private String deviceName;
    private boolean isChecked;
    private boolean isOnline;
    private String status;
    private int statusId;

    public HostInfo() {

    }

    public HostInfo(Parcel in) {
        this.ipAddress = in.readString();
        this.deviceId = in.readString();
        this.deviceName = in.readString();
        this.isChecked = in.readInt() == 1 ? true : false;
        this.isOnline = in.readInt() == 1 ? true : false;
        this.status = in.readString();
        this.statusId = in.readInt();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ipAddress);
        dest.writeString(this.deviceId);
        dest.writeString(this.deviceName);
        dest.writeInt(this.isChecked ? 1 : 0);
        dest.writeInt(this.isOnline ? 1 : 0);
        dest.writeString(this.status);
        dest.writeInt(this.statusId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator CREATOR = new Creator() {
        public HostInfo createFromParcel(Parcel in) {
            return new HostInfo(in);
        }

        public HostInfo[] newArray(int size) {
            return new HostInfo[size];
        }
    };
}
