package com.emekalites.react.compress.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

/**
 * Created by emnity on 10/7/17.
 */

public class ImageUtil {
    private ImageUtil() {
    }

    public static File compressImageV2(String imagePath, float ratio, String destinationPath) throws IOException {
        FileOutputStream fileOutputStream = null;
        File file = new File(destinationPath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            fileOutputStream = new FileOutputStream(destinationPath);
            // write the compressed bitmap at the destination specified by destinationPath.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
            int reqHeight = (int) (options.outHeight * ratio);
            int reqWidth = (int) (options.outWidth * ratio);
            Bitmap resizedBm = Bitmap.createScaledBitmap(bm, reqWidth, reqHeight, false);
            resizedBm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        ExifInterface exif = new ExifInterface(imagePath);
        String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

        ExifInterface newExif = new ExifInterface(file.getAbsolutePath());
        newExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat);
        newExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat);
        newExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lng);
        newExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lng);
        newExif.saveAttributes();

        return new File(destinationPath);
    }


    public static File compressImage(File imageFile, int reqWidth, int reqHeight, Bitmap.CompressFormat compressFormat,
                                     int quality, String destinationPath) throws IOException {
        FileOutputStream fileOutputStream = null;
        File file = new File(destinationPath).getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            fileOutputStream = new FileOutputStream(destinationPath);
            // write the compressed bitmap at the destination specified by destinationPath.
            Bitmap bm = decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight);
            bm.compress(compressFormat, quality, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return new File(destinationPath);
    }

    private static Bitmap decodeSampledBitmapFromFile(File imageFile, int reqWidth, int reqHeight) throws IOException {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            Bitmap scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            //check the rotation of the image and display it properly
            ExifInterface exif;
            exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            return scaledBitmap;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static long calculateSize(ReadableArray paths, float ratio){
        long size = 0;
        ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
        for (int keyIndex = 0; keyIndex < paths.size(); keyIndex++) {
            String path = paths.getString(keyIndex);

            File file = new File(path);
            if (ratio == 1){
                size += file.length();
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                Bitmap bm = BitmapFactory.decodeFile(file.getPath(), options);
                int reqHeight = (int) (options.outHeight * ratio);
                int reqWidth = (int) (options.outWidth * ratio);
                Bitmap resizedBm = Bitmap.createScaledBitmap(bm, reqWidth, reqHeight, false);
                try {
                    bmpStream.flush(); //to avoid out of memory error
                    bmpStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resizedBm.compress(Bitmap.CompressFormat.JPEG, 100, bmpStream);
                size += (long) bmpStream.size();
            }
        }

        return size;
    }
}
