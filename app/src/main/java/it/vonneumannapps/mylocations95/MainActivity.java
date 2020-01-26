package it.vonneumannapps.mylocations95;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBManager dbManager;
    ArrayList<Bundle> locations = new ArrayList<>();
    BaseAdapter baseAdapter;
    ImageView selectAllBtn;

    // questa immagine descrive i luoghi visitati, con indirizzo e immagine

    void initializaAdapter() {

        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return locations.size();
            }

            @Override
            public Bundle getItem(int i) {
                return locations.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                if(view == null) {
                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                    view = layoutInflater.inflate(R.layout.location_item_layout, viewGroup, false);
                }

                Bundle location = getItem(i);

                // NB findViewById lo chiamato sulla view, non sulla current activity
                TextView descTV = view.findViewById(R.id.descriptionTextView);
                TextView addressTV =view.findViewById(R.id.addressTextView);
                descTV.setText(location.getString("descrizione"));
                addressTV.setText(location.getString("indirizzo"));

                ImageView checkIV = view.findViewById(R.id.checkImageView);
                if(location.getBoolean(DBManager.SELECTED_FIELD_NAME)) {
                    checkIV.setVisibility(View.VISIBLE);
                } else {
                    checkIV.setVisibility(View.GONE);
                }

                ImageView locationImageIV = view.findViewById(R.id.locationImageImageView);
                locationImageIV.setImageBitmap(Utils.convertByteArrayToBitmap(location.getByteArray("immagine")));

                return view;
            }
        };
    }

    void loadLocations() {

        locations.clear();

        locations.addAll(dbManager.getLocations());

        baseAdapter.notifyDataSetChanged();
    }

    // lo splash
    void openSplashActivity() {

        Intent intent = new Intent(MainActivity.this, SplashActivity.class);

        startActivity(intent);
    }

    void openLocationDetailActivity(Bundle location){

        Intent intent = new Intent(this, LocationDetailActivity.class);

        intent.putExtras(location);

        startActivityForResult(intent, Utils.ADD_OR_EDIT_CODE);

        //startIntent(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            case Utils.ADD_OR_EDIT_CODE : {
                if (resultCode == RESULT_OK) {
                    loadLocations();
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
        setContentView(R.layout.activity_main);

        openSplashActivity();

        dbManager = new DBManager(this, DBManager.DATABASE_NAME, null, DBManager.DATABASE_VERSION);

        initializaAdapter();
        loadLocations();

        ListView locationsLV = findViewById(R.id.locationsListView);
        locationsLV.setAdapter(baseAdapter);

        locationsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Bundle location = locations.get(i);
                openLocationDetailActivity(location);
            }
        });

        ImageView addBtn = findViewById(R.id.addButton);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openLocationDetailActivity(new Bundle());
            }
        });

        selectAllBtn = findViewById(R.id.selectAllButton);
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //todo spiegare questo

                selectAllAccounts();
            }
        });

        ImageView deleteBtn = findViewById(R.id.deleteButton);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDeletionConfirmationDialog();
            }
        });
    }

    void selectAllAccounts() {

        for (Bundle location : locations) {

            location.putBoolean(DBManager.SELECTED_FIELD_NAME, true);
        }

        baseAdapter.notifyDataSetChanged();
    }

    void openDeletionConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.DELETE_CONFIRMATION_MESSAGE);

        builder.setCancelable(false);// utente deve scegliere o si o no
        builder.setPositiveButton(getString(R.string.YES), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                deleteLocations(); // procedi con cancellazione
                // TODO notificare dataset changed
            }
        });

        // la dialog viene chiusa automaticamente passando listener null
        builder.setNegativeButton(getString(R.string.NO), null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void deleteLocations() {

        dbManager.deleteSelectedLocations(getLocationsToBeDeleted());

        Utils.showShortToast(this, R.string.DELETE_SUCCESS_MESSAGE);

        //non facciamo "notify changed" perché in questo caso è cambiato il numero degli account
        // e dobbiamo ricaricarli tutti dal db
        loadLocations();
    }

    ArrayList<Bundle> getLocationsToBeDeleted() {

        ArrayList<Bundle> locationsToBeDeleted = new ArrayList<>();

        // questo for sarebbe superfluo perché ho già messo il controllo nel metodo del db manager
        for(Bundle location : locations) {
            if(location.getBoolean(DBManager.SELECTED_FIELD_NAME)) {

                locationsToBeDeleted.add(location);
            }
        }

        return locationsToBeDeleted;
    }
}
