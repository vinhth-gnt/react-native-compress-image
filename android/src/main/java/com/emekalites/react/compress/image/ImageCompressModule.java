package com.emekalites.react.compress.image;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.IOException;
import com.facebook.react.bridge.ReadableArray;

public class ImageCompressModule extends ReactContextBaseJavaModule {
    private final static String TAG = ImageCompressModule.class.getCanonicalName();
    private UriPath uriPath;
    private Context mContext;

    public ImageCompressModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext.getApplicationContext();
        uriPath = new UriPath((Application) mContext);
    }

    @Override
    public String getName() {
        return "ImageCompressAndroid";
    }

    @ReactMethod
    public void createCompressedImage(String imagePath, String directoryPath, final Callback successCb, final Callback failureCb) {
        try {
            createCompressedImageWithExceptions(imagePath, directoryPath, successCb, failureCb);
        } catch (IOException e) {
            failureCb.invoke(e.getMessage());
        }
    }

    private void createCompressedImageWithExceptions(String image, String directoryPath, final Callback successCb, final Callback failureCb) throws IOException {
        File imageFile = new ImageCompress(mContext)
                .setDestinationDirectoryPath(Environment.getExternalStorageDirectory().getPath())
                .compressToFile(new File(uriPath.getRealPathFromURI(Uri.parse(image))), directoryPath);

        if (imageFile != null) {
            WritableMap response = Arguments.createMap();
            response.putString("path", imageFile.getAbsolutePath());
            response.putString("uri", Uri.fromFile(imageFile).toString());
            response.putString("name", imageFile.getName());
            response.putDouble("size", imageFile.length());

            // Invoke success
            successCb.invoke(response);
        } else {
            failureCb.invoke("Error getting compressed image path");
        }
    }

    @ReactMethod
    public void createCustomCompressedImage(String imagePath, String directoryPath, String outputName, int maxWidth, int maxHeight, int quality, final Callback successCb, final Callback failureCb) {
        try {
            createCustomCompressedImageWithExceptions(imagePath, directoryPath, outputName, maxWidth, maxHeight, quality, successCb, failureCb);
        } catch (IOException e) {
            failureCb.invoke(e.getMessage());
        }
    }

    private void createCustomCompressedImageWithExceptions(String image, String directoryPath, String outputName, int maxWidth, int maxHeight, int quality, final Callback successCb, final Callback failureCb) throws IOException {
        File imageFile = new ImageCompress(mContext)
                .setMaxWidth(maxWidth)
                .setMaxHeight(maxHeight)
                .setQuality(quality)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(directoryPath)
                .compressToFile(new File(uriPath.getRealPathFromURI(Uri.parse(image))), outputName, "");

        if (imageFile != null) {
            WritableMap response = Arguments.createMap();
            response.putString("path", imageFile.getAbsolutePath());
            response.putString("uri", Uri.fromFile(imageFile).toString());
            response.putString("name", imageFile.getName());
            response.putDouble("size", imageFile.length());

            // Invoke success
            successCb.invoke(response);
        } else {
            failureCb.invoke("Error getting compressed image path");
        }
    }

    @ReactMethod
    public void caculateSize(ReadableArray paths, final Callback successCb, final Callback failureCb) {
        calculateSizeNative(paths, successCb, failureCb);
    }

    private void calculateSizeNative(ReadableArray paths, final Callback successCb, final Callback failureCb){
        WritableMap response = Arguments.createMap();
        response.putDouble("ratio30", ImageUtil.calculateSize(paths, 0.3f));
        response.putDouble("ratio50", ImageUtil.calculateSize(paths, 0.5f));
        response.putDouble("ratio70", ImageUtil.calculateSize(paths, 0.7f));
        response.putDouble("ratio100", ImageUtil.calculateSize(paths, 1f));

        successCb.invoke(response);
    }

    @ReactMethod
    public void resizeImage(String imagePath, String directoryPath, String outputName, float ratio, final Callback successCb, final Callback failureCb) {
        try {
            resizeImageNative(imagePath, directoryPath, outputName, ratio, successCb, failureCb);
        } catch (IOException e) {
            failureCb.invoke(e.getMessage());
        }
    }

    private void resizeImageNative(String imagePath, String directoryPath, String outputName, float ratio, final Callback successCb, final Callback failureCb) throws IOException {
       File imageFile = ImageUtil.compressImageV2(imagePath, ratio, new File(directoryPath).getAbsolutePath() + File.separator + outputName);
       if (imageFile != null) {
            WritableMap response = Arguments.createMap();
            response.putString("path", imageFile.getAbsolutePath());
            response.putString("uri", Uri.fromFile(imageFile).toString());
            response.putString("name", imageFile.getName());
            response.putDouble("size", imageFile.length());

            // Invoke success
            successCb.invoke(response);
        } else {
            failureCb.invoke("Error getting compressed image path");
        }
    }

}