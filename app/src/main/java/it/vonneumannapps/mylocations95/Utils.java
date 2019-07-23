package it.vonneumannapps.mylocations95;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utils {

    public static final int ADD_OR_EDIT_CODE = 100;

    public static Bitmap convertByteArrayToBitmap(byte[] bytes) {

        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return bmp;

    }
}
