package it.vonneumannapps.mylocations95;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class LocationDetailActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    EditText descET;
    EditText addressET;

    ImageView locationPictureIV;

    DBManager dbManager;

    Bundle location;

    String[] actions;
    BaseAdapter actionsAdapter;

    void initializeActionsAdapter() {
        actionsAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return actions.length;
            }

            @Override
            public String getItem(int i) {
                return actions[i];
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                if(view == null) {
                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                    view = layoutInflater.inflate(R.layout.action_item_layout, viewGroup, false);

                }

                String action = getItem(i);
                TextView titleTV = view.findViewById(R.id.titleTextView);
                titleTV.setText(action);

                return view;
            }
        };
    }

    void checkForCameraPermission() {

        int myCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(myCameraPermission != PackageManager.PERMISSION_GRANTED){

            String[] permissionsToRequest = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissionsToRequest, Utils.CAMERA_PERMISSION_CODE);
        }
        else {
            takePicture();
        }
    }

    void loadData() {

        descET.setText(location.getString("descrizione"));
        addressET.setText(location.getString("indirizzo"));

        Bitmap bitmap = Utils.convertByteArrayToBitmap(location.getByteArray("immagine"));

        locationPictureIV.setImageBitmap(bitmap);
    }

    void takePicture() {

        // SCATTA FOTO

        // DOUBT: difference btw this and LocationDetailActivity.this
        Uri outputFileUri = Utils.getOutputFileUri(this);

        //crete intent to take a picture and return control
        // to the calling application
        // apre la fotocamera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create a file to save the  image
        // extra con il percorso del file
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        //start the image capture intent
        startActivityForResult(intent, Utils.TAKE_PICTURE_CODE);

        /*
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Do something for Oreo 26 and above versions
        } else{
            // do something for phones running an SDK before lollipop
        }*/
    }

    void saveLocation() {

        // salviamo in bundle e passiamo al dbmanager

        if(descET.getText().toString().equals("") || addressET.getText().toString().equals("")) {
            // error msg
            Toast.makeText(this, getString(R.string.REQUIRED_FIELDS_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();

            return;
        }

        // save location
        // compress image 100% for DB limit image size 2MB
        // DBManagher.insertLocation(Bundle location)
        //get image?

        boolean isEditMode = !location.isEmpty();

        location.putString("descrizione", descET.getText().toString());
        location.putString("indirizzo", addressET.getText().toString());

        BitmapDrawable bitmapDrawable = (BitmapDrawable) locationPictureIV.getDrawable();

        Bitmap bitmap = bitmapDrawable.getBitmap();

        location.putByteArray("immagine", Utils.convertBitmapToByteArray(bitmap));

        if (!isEditMode) {

            dbManager.insertLocation(location);
        }
        else{
            dbManager.updateLocation(location);
        }

        Toast.makeText(this, getString(R.string.SUCCESSFULLY_SAVED_MESSAGE), Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);

        finish();
    }

    void openActionSelectionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getString(R.string.ACTION_SELECTION_TEXT));

        builder.setAdapter(actionsAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final int FOTOCAMERA = 0;
                //final int GALLERIA = 1;

                if(i==FOTOCAMERA) {

                    //TODO
                    checkForCameraPermission();// controlla permessi e poi apre fotocamera
                }
                else { //GALLERIA
                    //TODO
                }
            }
        });

        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Utils.CAMERA_PERMISSION_CODE: {

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                }
                else {
                    //TODO messaggio all'utente: servono i permessi
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            case Utils.TAKE_PICTURE_CODE : {

                if(resultCode == RESULT_OK) {

                    String pathName = Utils.getMainDirectory(this).getPath() + Utils.TMP_FILENAME;
                    Bitmap bitmap;

                    // TODO Autoclose anche per i file?
                    File tempFile = new File(pathName);
                    bitmap = BitmapFactory.decodeFile(tempFile.getPath());

                    locationPictureIV.setImageBitmap(bitmap);

                    // TODO saveLocation()

                    // int jpegQuality = 5;
                    /*
                    //File tempFile2 = new File(pathName + "");
                    try(OutputStream fOut = new FileOutputStream(tempFile)) {

                        bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, fOut);
                    }*/

                    /*
                    final FileOutputStream ostream;

                    try {

                        Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getPath());

                        ostream = new FileOutputStream(tempFile);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, ostream);

                        locationPictureIV.setImageBitmap(bitmap);

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();
                    }
                    */
                }

                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        this.actions = new String[] {getString(R.string.CAMERA_ACTION_TEXT), getString(R.string.GALLERY_ACTION_TEXT)};

        initializeActionsAdapter();

        dbManager = new DBManager(this, DBManager.DATABASE_NAME,
                null, DBManager.DATABASE_VERSION);

        descET = findViewById(R.id.descriptionEditText);
        addressET = findViewById(R.id.addressEditText);

        locationPictureIV = findViewById(R.id.locationImageImageView);

        location = getIntent().getExtras();

        if(!location.isEmpty()) {

            // caricare i dati
            loadData();
        }

        ImageView saveBtn = findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveLocation();
            }
        });

        ImageView addImageButton = findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActionSelectionDialog();//scelta se fotocamera o galleria
            }
        });

    }
}
