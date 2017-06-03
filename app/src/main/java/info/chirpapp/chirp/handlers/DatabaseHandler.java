package info.chirpapp.chirp.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CATEGORIES.db";
    private static final Integer DATABASE_VERSION = 1;
    private static final String LOG_TAG = "Database Handler";

    private static final String TABLE_NAME_CATEGORY = "CATEGORY";
    private static final String TABLE_NAME_ENTRY = "ENTRY";

    private static final String CATEGORY_COL_NAME = "NAME";

    private static final String ENTRY_COL_CATEGORY_ID = "CATEGORY_ID";
    private static final String ENTRY_COL_WORD = "WORD";
    private static final String ENTRY_COL_WEIGHT = "WEIGHT";

    private final Object mutex;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mutex = new Object();
    }

    public Long putCategory(String name) {
        Long insertedRowid;
        SQLiteDatabase database = getWritableDatabase();

        synchronized (mutex) {
            ContentValues values = new ContentValues();
            values.put(CATEGORY_COL_NAME, '\'' + name + '\'');

            insertedRowid = database.insert(TABLE_NAME_CATEGORY, null, values);
            Log.i(LOG_TAG, "Inserted category \"" + name + "\" with rowid " + insertedRowid + '.');
        }

        return insertedRowid;
    }

    public void putEntry(Long categoryID, String word, Double weight) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTRY_COL_CATEGORY_ID, categoryID);
        contentValues.put(ENTRY_COL_WORD, '\'' + word + '\'');
        contentValues.put(ENTRY_COL_WEIGHT, weight);

        Long insertedRowid = database.insert(TABLE_NAME_ENTRY, null, contentValues);
        Log.i(LOG_TAG, "Inserted entry \"" + word + "\" with weight " + weight + " to category with ID " + categoryID + " to row " + insertedRowid);
    }

    public void putWholeCategory(String categoryName, HashMap<String, Double> elements) {
        Long categoryID = putCategory(categoryName);

        for (Entry<String, Double> element : elements.entrySet()) {
            putEntry(categoryID, element.getKey(), element.getValue());
        }
    }


    //Kategori isimleri bir arraylist olarak döndürülüyor.
    public ArrayList<String> getCategoryNames() {
        ArrayList<String> categories = new ArrayList<>();
        String query = "SELECT " + CATEGORY_COL_NAME + " FROM " + TABLE_NAME_CATEGORY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                categories.add(c.getString(c.getColumnIndex(CATEGORY_COL_NAME)));
            } while (c.moveToNext());
        }

        return categories;
    }

    public HashMap<String, Integer> getEntries(String categoryName) {
        HashMap<String, Integer> entries = new HashMap<>();
        String query = "SELECT " + ENTRY_COL_WORD + ',' + ENTRY_COL_WEIGHT + " FROM " + TABLE_NAME_CATEGORY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                entries.put(c.getString(c.getColumnIndex(ENTRY_COL_WORD)), c.getInt(c.getColumnIndex(ENTRY_COL_WEIGHT)));
            } while (c.moveToNext());
        }
        c.close();
        return entries;
    }

    public Integer getCategoryCount() {
        String query = "SELECT  count() FROM " + TABLE_NAME_CATEGORY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        c.close();
        return c.moveToFirst() ? c.getInt(0) : -1;
    }
    //Verilen isimdeki kategoriyi siliyor

    public void deleteCategory(String categoryName) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT rowid FROM " + TABLE_NAME_CATEGORY + " WHERE " + TABLE_NAME_CATEGORY + '.' + CATEGORY_COL_NAME + " = " + categoryName;
        Cursor c = db.rawQuery(query, null);

        db.delete(TABLE_NAME_ENTRY, ENTRY_COL_CATEGORY_ID + "= ?", new String[]{String.valueOf(c.getInt(c.getColumnIndex("rowid")))});
        db.delete(TABLE_NAME_CATEGORY, "rowid = ?", new String[]{String.valueOf(c.getInt(c.getColumnIndex("rowid")))});
        c.close();
    }

    //Kategori içinden bir entry siliyor
    public void deleteEntries(String entry, String categoryName) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT rowid FROM " + TABLE_NAME_CATEGORY + " WHERE " + TABLE_NAME_CATEGORY + '.' + CATEGORY_COL_NAME + " = " + categoryName;
        Cursor c = db.rawQuery(query, null);

        db.delete(TABLE_NAME_ENTRY, ENTRY_COL_CATEGORY_ID + " = ? AND " + ENTRY_COL_WORD + " = ?", new String[]{String.valueOf(c.getString(c.getColumnIndex("rowid"))), entry});
        c.close();
    }

    public void editEntryName(String categoryName, String entryName, String newName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(ENTRY_COL_WORD, newName);

        String query = "SELECT rowid FROM " + TABLE_NAME_CATEGORY + " WHERE " + CATEGORY_COL_NAME + " = " + categoryName;
        Cursor c = db.rawQuery(query, null);

        db.update(TABLE_NAME_ENTRY, newValues, ENTRY_COL_CATEGORY_ID + " = ? ," + ENTRY_COL_WORD + " = ? ", new String[]{String.valueOf(c.getString(c.getColumnIndex("rowid"))), entryName});
        c.close();
    }

    public void editEntryWeight(String categoryName, String entryName, String newWeight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ENTRY_COL_WEIGHT, newWeight);

        String query = "SELECT rowid FROM " + TABLE_NAME_CATEGORY + " WHERE " + CATEGORY_COL_NAME + " = " + categoryName;
        Cursor c = db.rawQuery(query, null);

        db.update(TABLE_NAME_ENTRY, values, ENTRY_COL_CATEGORY_ID + " = ? ," + ENTRY_COL_WORD + " = ? ", new String[]{String.valueOf(c.getString(c.getColumnIndex("rowid"))), entryName});
        c.close();
    }

    public void dropEntry(String categoryName, String entry) {

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_CATEGORY + " (" + CATEGORY_COL_NAME + " TEXT NOT NULL);");
        db.execSQL("CREATE TABLE " + TABLE_NAME_ENTRY + " (" + ENTRY_COL_CATEGORY_ID + " INTEGER, " + ENTRY_COL_WORD + " TEXT, " + ENTRY_COL_WEIGHT + " INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ENTRY);

        onCreate(db);
    }
}
