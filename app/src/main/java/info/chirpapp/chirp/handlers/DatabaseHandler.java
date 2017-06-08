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
            values.put(CATEGORY_COL_NAME, name);

            insertedRowid = database.insert(TABLE_NAME_CATEGORY, null, values);
            Log.i(LOG_TAG, "Inserted category \"" + name + "\" with rowid " + insertedRowid + '.');
        }
        return insertedRowid;
    }

    public void putEntry(Long categoryID, String word, Integer weight) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTRY_COL_CATEGORY_ID, categoryID);
        contentValues.put(ENTRY_COL_WORD, word);
        contentValues.put(ENTRY_COL_WEIGHT, weight);

        Long insertedRowid = database.insert(TABLE_NAME_ENTRY, null, contentValues);
        Log.i(LOG_TAG, "Inserted entry \"" + word + "\" with weight " + weight + " to category with ID " + categoryID + " to row " + insertedRowid);
    }

    public Integer getMaxWeight() {
        Integer maxWeight;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_NAME_CATEGORY, new String[]{"MAX(" + ENTRY_COL_WEIGHT + ')'}, null, null, null, null, null);

        if (c.moveToFirst()) {
            return c.getInt(0);
        } else {
            return -1;
        }
    }

    public void addEntry(Long categoryID, String word) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTRY_COL_CATEGORY_ID, categoryID);
        contentValues.put(ENTRY_COL_WORD, word);
        contentValues.put(ENTRY_COL_WEIGHT, getMaxWeight());

        Long insertedRowid = database.insert(TABLE_NAME_ENTRY, null, contentValues);
        Log.i(LOG_TAG, "Inserted entry \"" + word + "\" with weight " + getMaxWeight() + " to category with ID " + categoryID + " to row " + insertedRowid);

    }

    public void putWholeCategory(String categoryName, HashMap<String, Integer> elements) {
        Long categoryID = putCategory(categoryName);

        for (Entry<String, Integer> element : elements.entrySet()) {
            putEntry(categoryID, element.getKey(), element.getValue());
        }
    }


    //Kategori isimleri bir arraylist olarak döndürülüyor.
    public ArrayList<String> getCategoryNames() {
        ArrayList<String> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_NAME_CATEGORY, new String[]{CATEGORY_COL_NAME}, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                categories.add(c.getString(c.getColumnIndex(CATEGORY_COL_NAME)));
            } while (c.moveToNext());
        }

        return categories;
    }

    public HashMap<String, Integer> getEntries(String categoryName) {
        HashMap<String, Integer> entries = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c1 = db.query(TABLE_NAME_CATEGORY, new String[]{"rowid"}, CATEGORY_COL_NAME + "=?", new String[]{categoryName}, null, null, null);
        String categoryID;

        if (c1.moveToFirst()) {
            categoryID = c1.getString(c1.getColumnIndex("rowid"));


            Cursor c = db.query(TABLE_NAME_ENTRY, new String[]{ENTRY_COL_WORD, ENTRY_COL_WEIGHT}, ENTRY_COL_CATEGORY_ID + "=?", new String[]{categoryID}, null, null, null);

            if (c.moveToFirst()) {
                do {
                    entries.put(c.getString(c.getColumnIndex(ENTRY_COL_WORD)), c.getInt(c.getColumnIndex(ENTRY_COL_WEIGHT)));
                } while (c.moveToNext());
            }
            c.close();
        } else {
            Log.e("DB", "CATEGORY NOT IN DB");
        }

        return entries;
    }

    //Verilen isimdeki kategoriyi siliyor
    public void deleteCategory(String categoryName) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_NAME_CATEGORY, new String[]{"rowid"}, TABLE_NAME_CATEGORY + '.' + CATEGORY_COL_NAME + " = ?", new String[]{categoryName}, null, null, null);

        db.delete(TABLE_NAME_ENTRY, ENTRY_COL_CATEGORY_ID + "= ?", new String[]{String.valueOf(c.getInt(c.getColumnIndex("rowid")))});
        db.delete(TABLE_NAME_CATEGORY, "rowid = ?", new String[]{String.valueOf(c.getInt(c.getColumnIndex("rowid")))});
        c.close();
    }

    //Kategori içinden bir entry siliyor
    public void deleteEntries(String entry, String categoryName) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_NAME_CATEGORY, new String[]{"rowid"}, TABLE_NAME_CATEGORY + '.' + CATEGORY_COL_NAME + " = ?", new String[]{categoryName}, null, null, null);

        db.delete(TABLE_NAME_ENTRY, ENTRY_COL_CATEGORY_ID + " = ? AND " + ENTRY_COL_WORD + " = ?", new String[]{String.valueOf(c.getString(c.getColumnIndex("rowid"))), entry});
        c.close();
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
