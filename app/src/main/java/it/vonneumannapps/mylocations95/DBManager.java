package it.vonneumannapps.mylocations95;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "db";
    public static final int DATABASE_VERSION = 1;

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query = "CREATE TABLE luoghi (id PRIMARY KEY AUTOINCREMENT, descrizione TEXT, indirizzo TEXT, immagine BLOB)";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<Bundle> getLocations() {

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Bundle> locations = new ArrayList<>();

        String query = "SELECT * FROM luoghi";

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

    void insertLocation(Bundle location) {

        // recuperiamo il db
        try(SQLiteDatabase db = getWritableDatabase()) {
            String query = "INSERT INTO luoghi (descrizione , indirizzo, immagine) VALUES (' "
                    + location.get("descrizione") + "' "
                    + ", '" + location.getString("indirizzo") + "' "
                    + ", " + location.getString("immagine")
                    + ")";

            db.execSQL(query);
        }
    }
}

