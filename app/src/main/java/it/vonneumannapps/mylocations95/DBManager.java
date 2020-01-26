package it.vonneumannapps.mylocations95;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "db";
    public static final int DATABASE_VERSION = 1;

    public static final String SELECTED_FIELD_NAME = "selected";
    public static final String LOCATIONS_TABLE_NAME = "luoghi";

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query = "CREATE TABLE " + LOCATIONS_TABLE_NAME
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, descrizione TEXT, indirizzo TEXT, immagine BLOB)";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<Bundle> getLocations() {

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Bundle> locations = new ArrayList<>();

        String query = "SELECT * FROM " + LOCATIONS_TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            Bundle location = new Bundle();

            location.putInt("id", cursor.getInt(cursor.getColumnIndex("id")));
            location.putString("descrizione", cursor.getString(cursor.getColumnIndex("descrizione")));
            location.putString("indirizzo", cursor.getString(cursor.getColumnIndex("indirizzo")));
            location.putByteArray("immagine", cursor.getBlob(cursor.getColumnIndex("immagine")));

            locations.add(location);

            cursor.moveToNext();
        }

        db.close();

        return locations;
    }

    void updateLocation(Bundle location) {

        try(SQLiteDatabase db = getWritableDatabase()) {

            ContentValues cv = new ContentValues();

            cv.put("id", location.getInt("id"));
            cv.put("descrizione", location.getString("descrizione"));
            cv.put("indirizzo", location.getString("indirizzo"));
            cv.put("immagine", location.getByteArray("immagine"));

            db.update(LOCATIONS_TABLE_NAME, cv, "id=?", new String[]{String.valueOf(location.getInt("id"))});
        }
    }

    void insertLocation(Bundle location) {

        // recuperiamo il db
        try(SQLiteDatabase db = getWritableDatabase()) {
            /*
            String query = "INSERT INTO " + LOCATIONS_TABLE_NAME
            + " (descrizione , indirizzo, immagine) VALUES (' "
                    + location.get("descrizione") + "' "
                    + ", '" + location.getString("indirizzo") + "' "
                    + ", " + location.getString("immagine")
                    + ")";

            db.execSQL(query);
            */

            ContentValues cv = new ContentValues();

            cv.put("descrizione", location.getString("descrizione"));
            cv.put("indirizzo", location.getString("indirizzo"));
            cv.put("immagine", location.getByteArray("immagine"));

            //db.execSQL(query);
            db.insert(LOCATIONS_TABLE_NAME, null, cv);
        }
    }

    public void deleteSelectedLocations(ArrayList<Bundle> locations) {

        String query = getDeletionQuerySelectedLocations(locations);

        try(SQLiteDatabase db = getWritableDatabase()) {

            db.execSQL(query);
        }

        // TODO we need unit tests
    }

    public static String getDeletionQuerySelectedLocations(ArrayList<Bundle> locations) {

        String query = "DELETE FROM " + LOCATIONS_TABLE_NAME
                + "  WHERE id IN (";

        boolean isFirstIdToDelete = true;

        for(Bundle location : locations) {

            if(location.getBoolean(SELECTED_FIELD_NAME))
            {
                int idToAdd = location.getInt("id");

                if(isFirstIdToDelete) {

                    query = query + idToAdd;
                    isFirstIdToDelete = false; // abbiamo appena aggiunto il primo
                }
                else {
                    query = query + ", " + idToAdd;
                }
            }
        }

        query = query + ")";

        return query;
    }
}

