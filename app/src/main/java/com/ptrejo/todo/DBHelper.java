package com.ptrejo.todo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static final String LOGTAG = "DBHelper";

    private static final String DATABASE_NAME    = "todoitems.db";
    private static final int    DATABASE_VERSION = 1;
    private static final String TABLE_NAME       = "tododata";

    public static final String KEY_ID           = "_id";
    public static final String KEY_NAME     	= "name";
    public static final String KEY_DESCRIPTION  = "description";
    public static final String KEY_DATE         = "date";
    public static final String KEY_INFO         = "info";

    public static final int COLUMN_ID         	= 0;
    public static final int COLUMN_NAME  	  	= 1;
    public static final int COLUMN_DESCRIPTION  = 2;
    public static final int COLUMN_DATE       	= 3;
    public static final int COLUMN_INFO       	= 4;

    private Context context;
    private SQLiteDatabase db;
    private SQLiteStatement insertStmt;

    private static final String INSERT =
            "INSERT INTO " + TABLE_NAME + "(" +
                    KEY_NAME + ", " +
                    KEY_DESCRIPTION + ", " +
                    KEY_DATE + ", " +
                    KEY_INFO + ") values (?, ?, ?, ?)";

    public DBHelper(Context context) throws Exception {
        this.context = context;

        try {
            OpenHelper openHelper = new OpenHelper(this.context);
            db = openHelper.getWritableDatabase();
            insertStmt = db.compileStatement(INSERT);
        } catch (Exception e) {
            Log.e(LOGTAG, " DBHelper constructor:  could not get database " + e);
            throw(e);
        }
    }

    public long insert(TodoItem itemInfo) {
        insertStmt.bindString(COLUMN_NAME, itemInfo.getName());
        insertStmt.bindString(COLUMN_DESCRIPTION, itemInfo.getDescription());
        insertStmt.bindString(COLUMN_DATE, itemInfo.getDate());
        insertStmt.bindString(COLUMN_INFO, itemInfo.getAddInfo());

        long value = -1;
        try {
            value = insertStmt.executeInsert();
        } catch (Exception e) {
            Log.e(LOGTAG, " executeInsert problem: " + e);
        }
        Log.d(LOGTAG, "value=" + value);
        return value;
    }

    public void deleteAll() {
        db.delete(TABLE_NAME, null, null);
    }

    public boolean deleteRecord(long rowId) {
        return db.delete(TABLE_NAME, KEY_ID + "=" + rowId, null) > 0;
    }

    public List<TodoItem> selectAll() {
        List<TodoItem> list = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME,
                new String[] {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_DATE, KEY_INFO},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                TodoItem itemInfo = new TodoItem(cursor.getString(COLUMN_NAME),
                                                    cursor.getString(COLUMN_DATE) ,
                                                    cursor.getString(COLUMN_DESCRIPTION),
                                                    cursor.getString(COLUMN_INFO),
                                                    cursor.getLong(COLUMN_ID));
                list.add(itemInfo);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed())
            cursor.close();

        return list;
    }

    private static class OpenHelper extends SQLiteOpenHelper {
        private static final String LOGTAG = "OpenHelper";

        private static final String CREATE_TABLE =
                "CREATE TABLE " +
                        TABLE_NAME +
                        "(" + KEY_ID + " INTEGER PRIMARY KEY, " +
                        KEY_NAME + " TEXT, " +
                        KEY_DESCRIPTION + " TEXT, " +
                        KEY_DATE + " TEXT, " +
                        KEY_INFO + " TEXT);";

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOGTAG, " onCreate");
            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Log.e(LOGTAG, " onCreate:  Could not create SQL database: " + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(LOGTAG, "Upgrading database, this will drop tables and recreate.");
            try {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            } catch (Exception e) {
                Log.e(LOGTAG, " onUpgrade:  Could not update SQL database: " + e);
            }
        }
    }
}
