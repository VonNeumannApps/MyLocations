package it.vonneumannapps.mylocations95;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utils {

    public static Bitmap convertByteArrayToBitmap(byte[] bytes) {

        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return bmp;

    }
}
