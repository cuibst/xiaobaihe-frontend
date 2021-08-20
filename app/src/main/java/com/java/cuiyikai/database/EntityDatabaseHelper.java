package com.java.cuiyikai.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * The main class to operate local entity database. <br>
 * This class use single instance mode, please use {@code getInstance()} to get the only instance to operate the database
 */
public class EntityDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "entity.db";
    private static final int DATABASE_VERSION = 1;
    private static EntityDatabaseHelper helper = null;
    private SQLiteDatabase database = null;
    public static final String TABLE_NAME = "entity_detail";

    private EntityDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private EntityDatabaseHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    /**
     * Use this function to get the only instance of the database helper.
     * @param context the app's context.
     * @param version required database version.
     * @return the instance of the database helper.
     */
    public static EntityDatabaseHelper getInstance(Context context, int version) {
        if(version > 0 && helper == null)
            helper = new EntityDatabaseHelper(context, version);
        else if(helper == null)
            helper = new EntityDatabaseHelper(context);
        return helper;
    }

    /**
     * This function opens the read link for the database. <br>
     * Remember to call this before any read operations.
     * @return the readable database.
     */
    public SQLiteDatabase openReadLink() {
        if(database == null || !database.isOpen())
            database = helper.getReadableDatabase();
        return database;
    }

    /**
     * This function opens the write link for the database. <br>
     * Remember to call this before any write operations.
     * @return the writable database.
     */
    public SQLiteDatabase openWriteLink() {
        if(database == null || !database.isOpen())
            database = helper.getWritableDatabase();
        return database;
    }

    /**
     * This function closes the link for the database. <br>
     * Remember to call this after all the operations.
     */
    public void closeLink() {
        if(database != null && database.isOpen()) {
            database.close();
            database = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(dropTable);
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "name VARCHAR NOT NULL," +
                "uri VARCHAR NOT NULL," +
                "jsonContent VARCHAR NOT NULL," +
                "problemsJson VARCHAR NOT NULL);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    /**
     * Insert the given entity to the database. <br>
     * Remember to <strong>open the write link</strong> before the operation.
     * @param databaseEntity the Entity you want to add.
     * @return the number of entities being updated.
     */
    public long insert(DatabaseEntity databaseEntity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", databaseEntity.getName());
        contentValues.put("uri", databaseEntity.getUri());
        contentValues.put("jsonContent", databaseEntity.getJsonContent());
        contentValues.put("problemsJson", databaseEntity.getProblemsJson());
        return database.insert(TABLE_NAME, null, contentValues);
    }

    /**
     * Get entities in the database by entity name. <br>
     * Remember to <strong>open the read link</strong> before the operation.
     * @param entityName the entity name you wants to get.
     * @return a list of entity with the name given.
     */
    public List<DatabaseEntity> queryEntityByName(String entityName) {
        String query = String.format("SELECT name, uri, jsonContent, problemsJson FROM %s WHERE name = ?", TABLE_NAME);
        ArrayList<DatabaseEntity> results = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, new String[]{entityName});
        while(cursor.moveToNext()) {
            DatabaseEntity databaseEntity = new DatabaseEntity();
            databaseEntity.setName(cursor.getString(0));
            databaseEntity.setUri(cursor.getString(1));
            databaseEntity.setJsonContent(cursor.getString(2));
            databaseEntity.setProblemsJson(cursor.getString(3));
            results.add(databaseEntity);
        }
        cursor.close();
        return results;
    }
}
