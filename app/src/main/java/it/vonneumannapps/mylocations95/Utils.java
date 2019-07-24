package it.vonneumannapps.mylocations95;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;// used for os version checks

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class Utils {

    public static final int ADD_OR_EDIT_CODE = 100;
    public static final int TAKE_PICTURE_CODE = 200;
    public static final int CAMERA_PERMISSION_CODE = 300;

    public static final String TMP_FILENAME = "/temp.jpg";

    public static Bitmap convertByteArrayToBitmap(byte[] bytes) {

        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return bmp;

                /*image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(),
                image.getHeight(), false));*/

    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);

        byte[] bytes = stream.toByteArray();

        return bytes;
    }

    public static File getMainDirectory(Context context) {


        return context.getExternalFilesDir(null);
    }

    public static Uri getOutputFileUri(Context context) {

        // Creo l'URI di destinazione, file (cartella_corrente)/temp.jpg
        File dir = Utils.getMainDirectory(context);
        File file = new File(dir + TMP_FILENAME );

        Uri outputFileUri;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ // Android Oreo 8.0 and avove

            String authority = BuildConfig.APPLICATION_ID + ".provider";
            outputFileUri = FileProvider.getUriForFile(context, authority, file);

        } else{
            // for phones running an SDK before Oreo 8.0
            outputFileUri = Uri.fromFile(file);
        }

        return outputFileUri;
    }
}
