package it.vonneumannapps.mylocations95;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationDetailActivity extends AppCompatActivity {

    EditText descET;
    EditText addressET;

    ImageView imageIV;

    DBManager dbManager;

    Bundle location;

    String[] actions = {getString(R.string.CAMERA_ACTION_TEXT), getString(R.string.GALLERY_ACTION_TEXT)};

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

    void openActionSelectionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getString(R.string.ACTION_SELECTION_TEXT));

        builder.setAdapter(actionsAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO
            }
        });

        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        initializeActionsAdapter();

        dbManager = new DBManager(this, DBManager.DATABASE_NAME, null, DBManager.DATABASE_VERSION);

        descET = findViewById(R.id.descriptionEditText);
        addressET = findViewById(R.id.addressEditText);

        imageIV = findViewById(R.id.locationImageImageView);

        location = getIntent().getExtras();

        if(!location.isEmpty()) {

            // caricare i dati
        }

        ImageView saveBtn = findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        ImageView addImageButton = findViewById(R.id.addImageButton);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

}
