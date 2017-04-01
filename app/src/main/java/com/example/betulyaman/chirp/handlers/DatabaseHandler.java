package com.example.betulyaman.chirp.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.betulyaman.chirp.containers.Ontology;
import com.example.betulyaman.chirp.containers.Primitive;
import com.example.betulyaman.chirp.containers.VectorElement;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "ONTOLOGIES";
    private final static Integer DATABASE_VERSION = 1;
    private final static String LOG_TAG = "Database Handler";

    private final static String TABLE_NAME_ONTOLOGY = "ONTOLOGY";
    private final static String TABLE_NAME_ENTRY = "ENTRY";

    private final static String ONTOLOGY_COL_NAME = "NAME";

    private final static String ENTRY_COL_ONTOLOGY_ID = "ONTOLOGY_ID";
    private final static String ENTRY_COL_WORD = "WORD";
    private final static String ENTRY_COL_WEIGHT = "WEIGHT";

    private final static String TABLE_NAME_VECTORIZE="VECTORS";
    private final static String VECTORIZE_COL_NAME = "ADJACENTS";
    private final static String VECTORIZE_STRENGHT = "STRENGHT";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Long putOntology(String name) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ONTOLOGY_COL_NAME, name);

        Long insertedRowid = database.insert(TABLE_NAME_ONTOLOGY, null, values);
        Log.i(LOG_TAG, "Inserted ontology \"" + name + "\" with rowid " + insertedRowid + ".");

        return insertedRowid;
    }

    public void putEntry(Long ontologyID, String word, Integer weight) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ENTRY_COL_ONTOLOGY_ID, ontologyID);
        values.put(ENTRY_COL_WORD, word);
        values.put(ENTRY_COL_WEIGHT, weight);

        Long insertedRowid = database.insert(TABLE_NAME_ENTRY, null, values);
        Log.i(LOG_TAG, "Inserted entry \"" + word + "\" with weight " + weight + " to ontology with ID " + ontologyID + " with rowid " + insertedRowid + ".");
    }

    public void putWholeOntology(String ontologyName, ArrayList<VectorElement> elements) {
        Long ontologyID = putOntology(ontologyName);

        for (VectorElement element : elements) {
            putEntry(ontologyID, element.getWord(), element.getFrequency());
        }
    }


    //Ontoloji isimleri bir arraylist olarak döndürülüyor.
    public ArrayList<String> getOntologyNames() {
        ArrayList<String> ontologies = new ArrayList<>();
        String query = "SELECT " + ONTOLOGY_COL_NAME + " FROM " + TABLE_NAME_ONTOLOGY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                ontologies.add(c.getString(c.getColumnIndex(ONTOLOGY_COL_NAME)));
            } while (c.moveToNext());
        }

        c.close();
        return ontologies;
    }

    public ArrayList<VectorElement> getEntries(String ontologyName) {
        ArrayList<VectorElement> entries = new ArrayList<>();
        String query = "SELECT " + ENTRY_COL_WORD + "," + ENTRY_COL_WEIGHT + " FROM " + TABLE_NAME_ONTOLOGY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                entries.add(new VectorElement(c.getString(c.getColumnIndex(ENTRY_COL_WORD)), c.getInt(c.getColumnIndex(ENTRY_COL_WEIGHT))));
            } while (c.moveToNext());
        }
        c.close();
        return entries;
    }

    //Verilen isimdeki ontolojiyi siliyor
    public void deleteOntology(String ontologyName) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT rowid FROM " + TABLE_NAME_ONTOLOGY + " WHERE " + ONTOLOGY_COL_NAME + " = " + ontologyName;
        Cursor c = db.rawQuery(query, null);

        db.delete(TABLE_NAME_ENTRY, ENTRY_COL_ONTOLOGY_ID + "= ?", new String[]{String.valueOf(c.getInt(c.getColumnIndex("rowid")))});
        db.delete(TABLE_NAME_ONTOLOGY, "rowid = ?", new String[]{String.valueOf(c.getInt(c.getColumnIndex("rowid")))});
        c.close();
    }

    //Ontoloji içinden bir entry siliyor
    public void deleteEntries(String entry, String ontologyName) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT rowid FROM " + TABLE_NAME_ONTOLOGY + " WHERE " + ONTOLOGY_COL_NAME + " = " + ontologyName;
        Cursor c = db.rawQuery(query, null);

        db.delete(TABLE_NAME_ENTRY, ENTRY_COL_ONTOLOGY_ID + " = ? AND " + ENTRY_COL_WORD + " = ?", new String[]{String.valueOf(c.getString(c.getColumnIndex("rowid"))), entry});
        c.close();
    }

    public void editEntryName(String ontologyName, String entryName, String newName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(ENTRY_COL_WORD, newName);

        String query = "SELECT rowid FROM " + TABLE_NAME_ONTOLOGY + " WHERE " + ONTOLOGY_COL_NAME + " = " + ontologyName;
        Cursor c = db.rawQuery(query, null);

        db.update(TABLE_NAME_ENTRY, newValues, ENTRY_COL_ONTOLOGY_ID + " = ? ," + ENTRY_COL_WORD + " = ? ", new String[]{String.valueOf(c.getString(c.getColumnIndex("rowid"))), entryName});
        c.close();
    }

    public void editEntryWeight(String ontologyName, String entryName, String newWeight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ENTRY_COL_WEIGHT, newWeight);

        String query = "SELECT rowid FROM " + TABLE_NAME_ONTOLOGY + " WHERE " + ONTOLOGY_COL_NAME + " = " + ontologyName;
        Cursor c = db.rawQuery(query, null);

        db.update(TABLE_NAME_ENTRY, values, ENTRY_COL_ONTOLOGY_ID + " = ? ," + ENTRY_COL_WORD + " = ? ", new String[]{String.valueOf(c.getString(c.getColumnIndex("rowid"))), entryName});
        c.close();
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_ONTOLOGY
                + " (" + ONTOLOGY_COL_NAME + " VARCHAR(20) UNIQUE NOT NULL);");
        db.execSQL("CREATE TABLE " + TABLE_NAME_ENTRY
                + " (" + ENTRY_COL_ONTOLOGY_ID + " INTEGER PRIMARY KEY, "
                + ENTRY_COL_WORD + " INTEGER PRIMARY KEY, "
                + ENTRY_COL_WEIGHT + " INTEGER NOT NULL) WITHOUT ROWID;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ONTOLOGY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ENTRY);

        onCreate(db);
    }
}
