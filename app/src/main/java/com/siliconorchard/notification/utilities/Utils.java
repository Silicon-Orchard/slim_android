package com.siliconorchard.notification.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.asynctask.SendMessageAsync;
import com.siliconorchard.notification.model.ChatMessage;
import com.siliconorchard.notification.model.HostInfo;
import com.siliconorchard.notification.service.ServiceServer;
import com.siliconorchard.notification.singleton.GlobalDataHolder;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by adminsiriconorchard on 7/13/16.
 */
public class Utils {
    private static String DEVICE_NAME;
    private static String DEVICE_ID;



    public static boolean findDeviceID(Activity activity, SharedPreferences sharedPreferences) {
        boolean isPermissionRequested = false;
        DEVICE_ID = sharedPreferences.getString(Constant.KEY_DEVICE_ID, null);
        if(DEVICE_ID == null) {
            try {
                getDeviceIdFromTelephonyManager(activity, sharedPreferences);
            } catch (SecurityException e) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.READ_PHONE_STATE)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                Constant.READ_PHONE_STATE_PERMISSION);

                        isPermissionRequested = true;
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
            }
        }

        return isPermissionRequested;
    }

    public static String getDeviceId(Activity activity, SharedPreferences sharedPreferences) {
        if(DEVICE_ID == null) {
            findDeviceID(activity, sharedPreferences);
        }
        return DEVICE_ID;
    }

    public static void getDeviceIdFromTelephonyManager(Activity activity, SharedPreferences sharedPreferences) {
        TelephonyManager telephonyManager = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
        DEVICE_ID = telephonyManager.getDeviceId();
        sharedPreferences.edit().putString(Constant.KEY_DEVICE_ID, DEVICE_ID).commit();
    }

    public static void setDeviceId(String deviceId) {
        DEVICE_ID = deviceId;
    }

    public static void startServerService(Context context) {
        String ipAddress = getDeviceIpAddress();
        if(ipAddress == null) {
            Log.e("TAG_LOG", "Ip address not found");
            return;
        }
        Intent serviceIntent = new Intent(context, ServiceServer.class);
        serviceIntent.putExtra(Constant.KEY_MY_IP_ADDRESS, ipAddress);
        context.startService(serviceIntent);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        sharedPreferences.edit().putString(Constant.KEY_MY_IP_ADDRESS, ipAddress).commit();
    }

    /**
     * Get ip address of the device
     */
    public static String getDeviceIpAddress() {
        String ipAddress = null;
        try {
            //Loop through all the network interface devices
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces(); enumeration.hasMoreElements(); ) {
                NetworkInterface networkInterface = enumeration.nextElement();
                //Loop through all the ip addresses of the network interface devices
                for (Enumeration<InetAddress> enumerationIpAddr = networkInterface.getInetAddresses(); enumerationIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumerationIpAddr.nextElement();
                    //Filter out loopback address and other irrelevant ip addresses
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        //Print the device ip address in to the text view
                        ipAddress = inetAddress.getHostAddress();

                    }
                }
            }
        } catch (SocketException e) {
            Log.e("ERROR:", e.toString());
        }
        return ipAddress;
    }

    public static HostInfo getHostInfoFromChatMessage(ChatMessage chatMessage) {
        HostInfo hostInfo = new HostInfo();
        hostInfo.setIpAddress(chatMessage.getIpAddress());
        hostInfo.setDeviceId(chatMessage.getDeviceId());
        hostInfo.setDeviceName(chatMessage.getDeviceName());
        hostInfo.setStatus(chatMessage.getStatus());
        hostInfo.setStatusId(chatMessage.getStatusChannel());
        return hostInfo;
    }



    public static void findDeviceName(SharedPreferences sharedPreferences) {
        DEVICE_NAME = sharedPreferences.getString(Constant.KEY_MY_DEVICE_NAME, null);
        if(DEVICE_NAME == null) {
            DEVICE_NAME = android.os.Build.MANUFACTURER + android.os.Build.PRODUCT + android.os.Build.MODEL;
            sharedPreferences.edit().putString(Constant.KEY_MY_DEVICE_NAME, DEVICE_NAME).commit();
        }
    }

    public static String getDeviceName(SharedPreferences sharedPreferences) {
        if(DEVICE_NAME == null) {
            findDeviceName(sharedPreferences);
        }
        return DEVICE_NAME;
    }

    public static void setDeviceName(SharedPreferences sharedPreferences, String deviceName) {
        if(deviceName == null) {
            return;
        }
        DEVICE_NAME = deviceName;
        sharedPreferences.edit().putString(Constant.KEY_MY_DEVICE_NAME, deviceName).commit();
    }



    public static boolean isServerServiceRunning(Context context, String serviceName) {
        boolean isRunning = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getPackageName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static AlertDialog.Builder createAlertDialog(Activity activity, String title, String message) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_info);
        return builder;
    }

    public static AlertDialog.Builder createAlertDialog(Activity activity, int title, int message) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_info);
        return builder;
    }

    public static void sendBroadCastMessage(ChatMessage chatMessage) throws JSONException {
        String ipAddress = chatMessage.getIpAddress();
        String ipAddressBase = ipAddress.substring(0, ipAddress.lastIndexOf('.') + 1);
        String message = chatMessage.getJsonString();
        for(int i = 2; i<255; i++) {
            String clientAddress = ipAddressBase+i;
            if(!ipAddress.equals(clientAddress)) {
                HostInfo hostInfo = new HostInfo();
                hostInfo.setIpAddress(clientAddress);
                SendMessageAsync sendMessageAsync = new SendMessageAsync();
                sendMessageAsync.execute(hostInfo, message);
            }
        }
    }


    public static void sendBroadCastMessageToRegisteredClients(ChatMessage chatMessage) throws JSONException{
        String message = chatMessage.getJsonString();
        List<HostInfo> list = GlobalDataHolder.getInstance().getListHostInfo();
        if(list == null || list.size() < 1) {
            return;
        }
        for(int i = 0; i<list.size(); i++) {
            HostInfo hostInfo = list.get(i);
            SendMessageAsync sendMessageAsync = new SendMessageAsync();
            sendMessageAsync.execute(hostInfo, message);
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static Bitmap decodeImageFile(String filePath, int desiredSize) throws FileNotFoundException, IOException {
        File file = new File(filePath);
        if(desiredSize <= 0) {
            return decodeImageFile(file);
        } else {
            return decodeImageFile(file, desiredSize);
        }
    }

    /**
     * Decodes bitmap from file to a desired scale
     * @param file {Image file}
     * @param desiredSize {Desired size of the image}
     * @return {Bitmap}
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Bitmap decodeImageFile(File file, int desiredSize) throws FileNotFoundException, IOException{
        Bitmap bitmap = null;

        //Decode image size
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;

        FileInputStream fis = new FileInputStream(file);
        BitmapFactory.decodeStream(fis, null, options1);
        fis.close();

        int scale = 1;
        if (options1.outHeight > desiredSize || options1.outWidth > desiredSize) {
            scale = (int)Math.pow(2, (int) Math.ceil(Math.log(desiredSize /
                    (double) Math.max(options1.outHeight, options1.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inSampleSize = scale;
        fis = new FileInputStream(file);
        bitmap = BitmapFactory.decodeStream(fis, null, options2);
        fis.close();

        return bitmap;
    }


    /**
     * Decodes bitmap from file to a desired scale
     * @param file {Image file}
     * @return {Bitmap}
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Bitmap decodeImageFile(File file) throws FileNotFoundException, IOException{
        Bitmap bitmap = null;


        FileInputStream fis = new FileInputStream(file);
        bitmap = BitmapFactory.decodeStream(fis);
        fis.close();

        return bitmap;
    }

    public static File createFile(String folderPath, String fileName) {
        //final File dir = new File(AppController.getInstance().getFilesDir() + "/WalkiTalkie");
        final File dir = new File(folderPath);
        dir.mkdirs();
        folderPath = dir.getAbsolutePath();

        if(fileName == null) {
            return null;
        }
        File mFile = null;
        try {
            File folder = new File(folderPath);
            Log.e("TAG_LOG","Folder Path: "+folderPath);
            if(!folder.exists()) {
                if(folder.mkdirs()) {
                    Log.e("TGA_LOG","Directory created");
                } else {
                    Log.e("TGA_LOG", "Directory not created");
                }
            }
            mFile = new File(folderPath, fileName);
            if(!mFile.exists()) {
                mFile.createNewFile();
            }
        } catch (IOException e) {
            mFile = null;
            e.printStackTrace();
        }
        return mFile;
    }


    public static boolean saveSaveImageBitmap(String filePath, String fileName, Bitmap bitmap) {
        boolean ret = false;
        File file = createFile(filePath, fileName);
        if(file == null) {
            return false;
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static String bitmapToBase64String(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedString;
    }

    public static Bitmap base64StringToBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedBitmap;
    }


    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(Activity activity) {
        if(activity.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public static void showSoftKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    private static Integer SCREEN_HEIGHT;
    private static Integer SCREEN_WIDTH;



    public static int getScreenHeight(Context context) {
        if(SCREEN_HEIGHT == null) {
            findScreenResolution(context);
        }
        return SCREEN_HEIGHT;
    }

    public static int getScreenWidth(Context context) {
        if(SCREEN_WIDTH == null) {
            findScreenResolution(context);
        }
        return SCREEN_WIDTH;
    }

    /**
     * Get display screen height and width
     * @param {context} activity context
     */
    private static void findScreenResolution(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        SCREEN_WIDTH= metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;
    }

    /*
    * Converts density-independent pixels to actual pixel
    * @param: {int} density-independent pixel
    * @return: {int} pixel
    * */
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /*
    * Converts actual pixel to density-independent pixels
    * @param: {int} pixel
    * @return: {int} density-independent pixel
    * */
    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
