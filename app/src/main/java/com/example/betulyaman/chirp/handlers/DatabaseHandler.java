package com.example.betulyaman.chirp.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    public ArrayList<String> getOntologies() {

    }

    public ArrayList<VectorElement> getEntries(String ontologyName) {

    }

    public void dropOntology(String ontologyName) {

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_ONTOLOGY
                + " (" + ONTOLOGY_COL_NAME + " VARCHAR(20) UNIQUE NOT NULL);");
        db.execSQL("CREATE TABLE " + TABLE_NAME_ENTRY
                + " (" + ENTRY_COL_ONTOLOGY_ID + " INTEGER PRIMARY KEY, "
                + ENTRY_COL_WORD + " INTEGER PRIMARY KEY, "
                + ENTRY_COL_WEIGHT + " INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ONTOLOGY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ENTRY);

        onCreate(db);
    }
}
