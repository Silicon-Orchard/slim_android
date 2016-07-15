package com.siliconorchard.notification.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.siliconorchard.notification.utilities.Constant;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Created by adminsiriconorchard on 4/13/16.
 */
public class ServiceServer extends IntentService {
    private static ServerSocket sServerSocket;

    public ServiceServer() {
        super(Constant.SERVER_SERVICE_NAME);
    }
    public ServiceServer(String name) {
        super(name);
    }

    private static DatagramSocket sDataGramSocket;
    public static final int DATA_PACKET_LENGTH = 65508;

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.e("TAG_LOG","Service Running");
            sDataGramSocket = new DatagramSocket(Constant.UDP_SERVER_PORT);
            while (true) {
                try {
                    byte[] buffer = new byte[DATA_PACKET_LENGTH];
                    if(sDataGramSocket.isClosed()) {
                        this.stopSelf();
                        break;
                    }
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    sDataGramSocket.receive(datagramPacket);
                    byte[] receivedData = datagramPacket.getData();
                    Log.e("TAG_LOG", "Ending message receiving from service");
                    publishResults(receivedData, datagramPacket.getLength());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void publishResults(byte[] data, int length) {
        String message = new String(data, 0, length);
        publishResults(message);
    }

    public static void closeSocket() {
        try {
            if(sServerSocket != null) {
                sServerSocket.close();
            }
            if(sDataGramSocket != null) {
                sDataGramSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void publishResults(String message) {
        Intent intentForeGround = new Intent(Constant.SERVICE_NOTIFICATION_STRING_CHAT_FOREGROUND);
        intentForeGround.putExtra(Constant.KEY_CLIENT_MESSAGE, message);
        sendBroadcast(intentForeGround);

        Intent intentBackGround = new Intent(Constant.SERVICE_NOTIFICATION_STRING_CHAT_BACKGROUND);
        intentBackGround.putExtra(Constant.KEY_CLIENT_MESSAGE, message);
        sendBroadcast(intentBackGround);
    }
}
