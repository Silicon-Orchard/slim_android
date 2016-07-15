package com.siliconorchard.notification.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adminsiriconorchard on 7/13/16.
 */
public class Notification {

    private String name;
    private String base64Image;
    private String status;

    private static final String JSON_KEY_NAME = "name";
    private static final String JSON_BASE_64_IMAGE = "base64_image";
    private static final String JSON_KEY_STATUS = "status";

    public Notification() {

    }

    public Notification(String jsonString) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonString);
        this.name = jsonObject.getString(JSON_KEY_NAME);
        this.base64Image = jsonObject.getString(JSON_BASE_64_IMAGE);
        this.status = jsonObject.getString(JSON_KEY_STATUS);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
