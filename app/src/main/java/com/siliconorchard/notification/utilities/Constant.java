package com.siliconorchard.notification.utilities;

import android.os.Environment;

import java.io.File;

/**
 * Created by adminsiriconorchard on 7/13/16.
 */
public class Constant {
    public static final int SDK_VERSION = android.os.Build.VERSION.SDK_INT;
    public static final String SHARED_PREF_NAME = "com.siliconorchard.notification";
    public static final String SERVER_SERVICE_NAME = "com.siliconorchard.notification.service";

    public static final int UDP_SERVER_PORT = 27823;
    public static final String KEY_CLIENT_MESSAGE = "key_client_message";
    public static final String KEY_MY_IP_ADDRESS = "key_my_ip_address";

    public static final String SELF_PACKAGE_NAME = "com.siliconorchard.notification";
    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final String KEY_SERVER_IP_ADDRESS = "key_server_ip_address";

    public static final String KEY_MY_DEVICE_NAME = "key_my_device_name";
    public static final String KEY_HOST_INFO = "key_host_info";
    public static final String KEY_HOST_INFO_LIST = "key_host_info_list";
    public static final String KEY_CHANNEL_NUMBER = "key_channel_number";
    public static final String KEY_IMAGE_BITMAP = "key_image_bitmap";

    public static final String DEVICE_ID_UNKNOWN = "UNKNOWN";


    public static final String KEY_IS_CONTACT_MODIFIED = "key_is_contact_added";

    public static final String RECEIVER_NOTIFICATION_CONTACT_LIST_MODIFIED = "com.siliconorchard.notification.receiver.contact_modified";
    public static final String RECEIVER_NOTIFICATION_CHAT_REQUEST = "com.siliconorchard.notification.receiver.chat_request";
    public static final String RECEIVER_NOTIFICATION_CHAT_ACCEPT = "com.siliconorchard.notification.receiver.chat_accept";
    public static final String SERVICE_NOTIFICATION_STRING_CHAT_FOREGROUND = "com.siliconorchard.notification.service.receiver.foreground";
    public static final String SERVICE_NOTIFICATION_STRING_CHAT_BACKGROUND = "com.siliconorchard.notification.service.receiver.background";

    public static final int REQUEST_CODE_SELECT_SINGLE_PICTURE = 4551;
    public static final int REQUEST_CODE_SELECT_ANY_FILE = 4552;

    public static final int READ_PHONE_STATE_PERMISSION = 111;


    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator +
            "WalkieTalkie"+ File.separator + "photo"+ File.separator;
    //public static final String FOLDER_NAME_PHOTO = "photo";
    public static final String MY_PP = "mypp";
    public static final String OTHERS_PIC = "others";
    public static final String PROFILE_PIC_NAME = "profile.png";
    public static final String KEY_USER_STATUS = "user_status";
    public static final String KEY_STATUS_CHANNEL = "status_channel";


    public static String[] STATUSES = {
            "Ping Pong",
            "Chess",
            "Lunch",
            "Coffee",
            "Pool",
            "Foosball",
            "Board Games",
            "Hangout",
            "Walk",
            "Run"
    };
}
