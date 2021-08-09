package com.emekalites.react.compress.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.ByteArrayOutputStream;

/**
 * Created by emnity on 10/7/17.
 */

public class ImageUtil {
    private ImageUtil() {
    }

    public static File compressImageV2(File imageFile, String destinationPath, int quality, int reqWidth, int reqHeight) {
        File outputFile = new File(destinationPath).getParentFile();
        if (!outputFile.exists()) {
            outputFile.mkdirs();
        }
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // Bitmap bmm = decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight);
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        long maxSize = (long) (imageFile.length() * (quality * 1.0 / 100));
        long streamLength = maxSize;
        int compressQuality = 88;
        ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
        while (streamLength >= maxSize && compressQuality > 4) {
            try {
                bmpStream.flush(); //to avoid out of memory error
                bmpStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            compressQuality -= 4;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            streamLength = (long) bmpStream.size();
        }
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(outputFile);
            fo.write(bmpStream.toByteArray());
            fo.flush();
            fo.close();
        } catch (IOException e) {
            return null;
        } 
        return outputFile;
    }

    public static File compressImage(File imageFile, int reqWidth, int reqHeight, Bitmap.CompressFormat compressFormat,
                                     int quality, String destinationPath) throws IOException {
        // FileOutputStream fileOutputStream = null;
        // File file = new File(destinationPath).getParentFile();
        // if (!file.exists()) {
        //     file.mkdirs();
        // }
        // try {
        //     fileOutputStream = new FileOutputStream(destinationPath);
        //     // write the compressed bitmap at the destination specified by destinationPath.
        //     Bitmap bm = decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight);
        //     bm.compress(compressFormat, quality, fileOutputStream);
        // } finally {
        //     if (fileOutputStream != null) {
        //         fileOutputStream.flush();
        //         fileOutputStream.close();
        //     }
        // }

        // return new File(destinationPath);

        File outputFile = new File(destinationPath).getParentFile();
        if (!outputFile.exists()) {
            outputFile.mkdirs();
        }
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight);
        // Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        long maxSize = (long) (imageFile.length() * (quality * 1.0 / 100));
        long streamLength = maxSize;
        int compressQuality = 98;
        ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
        while (streamLength >= maxSize && compressQuality > 2) {
            try {
                bmpStream.flush(); //to avoid out of memory error
                bmpStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            compressQuality -= 2;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            streamLength = (long) bmpStream.size();
        }
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(destinationPath);
            fo.write(bmpStream.toByteArray());
            fo.flush();
            fo.close();
        } catch (IOException e) {
            return null;
        } 
        return outputFile;
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
}
