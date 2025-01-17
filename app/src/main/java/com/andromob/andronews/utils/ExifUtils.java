package com.andromob.andronews.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.exifinterface.media.ExifInterface;

public class ExifUtils {

    public static Bitmap getRotatedBitmap(Context context, Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            float width = (float) bitmap.getWidth();
            float height = (float) bitmap.getHeight();
            float max = Math.max(width / 1280.0f, height / 1280.0f);
            if (max > 1.0f) {
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width / max), (int) (height / max), false);
            }
            Bitmap rotateBitmap = rotateBitmap(bitmap,
                    new ExifInterface(context.getContentResolver().openInputStream(uri))
                            .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1));
            if (rotateBitmap != bitmap) {
                bitmap.recycle();
            }
            return rotateBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int i) {
        Matrix matrix = new Matrix();
        switch (i) {
            case 2:
                matrix.setScale(-1.0f, 1.0f);
                break;
            case 3:
                matrix.setRotate(180.0f);
                break;
            case 4:
                matrix.setRotate(180.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case 5:
                matrix.setRotate(90.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case 6:
                matrix.setRotate(90.0f);
                break;
            case 7:
                matrix.setRotate(-90.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case 8:
                matrix.setRotate(-90.0f);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            bitmap.recycle();
            return createBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}