package com.siliconorchard.notification.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.siliconorchard.notification.dialog.PopupSelectPictureOption;
import com.siliconorchard.notification.utilities.Constant;
import com.siliconorchard.notification.utilities.Utils;

import java.io.File;

/**
 * Created by adminsiriconorchard on 6/20/16.
 */
public class ActivitySelectFileAndPhotoBase extends Activity {

    protected File mSelectedFile;
    protected static final String FILE_PATH = Constant.BASE_PATH+Constant.MY_PP;
    protected String uriPath;
    protected Bitmap mSelectedBitmap;

    protected void initUriAndFile(Intent data, boolean isImage) {
        if(uriPath == null) {
            Uri fileUri = data.getData();
            if(isImage){
                mSelectedFile = new File(Utils.getRealPathFromURI(this, fileUri));
            } else {
                mSelectedFile = new File(fileUri.getPath());
            }

        } else {
            mSelectedFile = new File(uriPath);
        }
    }

    protected void selectPictureOption() {
        FragmentManager fm = getFragmentManager();
        PopupSelectPictureOption popupSelectPictureOption = new PopupSelectPictureOption();
        popupSelectPictureOption.setOnClickOption(new PopupSelectPictureOption.OnClickOption() {
            @Override
            public void onClickOptions(PopupSelectPictureOption.Options option) {
                switch (option) {
                    case GALLERY:
                        selectPicture();
                        break;
                    case CAMERA:
                        capturePicture();
                        break;
                }
            }
        });
        popupSelectPictureOption.show(fm, "popup_select_picture_options");
    }

    private void selectPicture() {
        uriPath = null;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constant.REQUEST_CODE_SELECT_SINGLE_PICTURE);
    }

    private void capturePicture() {
        File tempFolder = new File(FILE_PATH);
        tempFolder.mkdir();
        if (! tempFolder.exists()){
            if (! tempFolder.mkdirs()){
                Log.e("TAG_LOG", "failed to create directory");
            }
        }
        mSelectedFile = new File(FILE_PATH,
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        uriPath = mSelectedFile.getAbsolutePath();
        Uri uriImage = Uri.fromFile(mSelectedFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        startActivityForResult(intent, Constant.REQUEST_CODE_SELECT_SINGLE_PICTURE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (uriPath != null) {
            outState.putString("cameraImageUri", uriPath);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            uriPath = savedInstanceState.getString("cameraImageUri");
        }
    }
}
