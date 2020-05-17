package seventh.bupt.time;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
    private static final String DB_NAME = "TimeManagement.db";
    private static final String DB_TABLE = "normalTransaction";
    private static final int DB_VERSION = 1;

    public static final String KEY_ID = "_id";
    public static final String KEY_Date = "date";
    public static final String KEY_IsNotify = "isNotify";
    public static final String KEY_Description = "description";
    public static final String KEY_StartTime = "startTime";
    public static final String KEY_EndTime = "endTime";

    private SQLiteDatabase db;
    private final Context context;
    private DBOpenHelper dbOpenHelper;

    private static class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private static final String DB_CREATE = "create table " +
                DB_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_Date + " not null, " + KEY_StartTime +
                " text not null," + KEY_EndTime + " text not null," + KEY_IsNotify +
                " text not null,"  + KEY_Description + " text not null );";

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(_db);
        }
    }

    public DBAdapter(Context _context) {
        context = _context;
    }

    public void open() throws SQLiteException {
        dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    //数据库操作
    public long insert(NormalTransaction transaction) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_Date, String.valueOf(transaction.transactionDate_));
        newValues.put(KEY_IsNotify, transaction.isNotify_);
        newValues.put(KEY_Description, transaction.description_);
        newValues.put(KEY_StartTime, transaction.startTime_);
        newValues.put(KEY_EndTime, transaction.endTime_);
        return db.insert(DB_TABLE, null, newValues);
    }

    public long deleteAllData() {
        return db.delete(DB_TABLE, null, null);
    }

    public long deleteOneData(String date){
        return db.delete(DB_TABLE, KEY_Date + "=" + date, null);
    }

    public NormalTransaction[] queryAllData() {
        Cursor results = db.query(DB_TABLE, new String[] {KEY_Date, KEY_IsNotify, KEY_Description,KEY_StartTime,KEY_EndTime}, null, null, null, null, null);
     	return ConvertToTransaction(results);
    }

    public NormalTransaction[] queryDateData(String date) {
        Cursor results = db.rawQuery("select * from normalTransaction where date=?",new String[]{date});
        return ConvertToTransaction(results);
    }

    public long updateOneData(String date, NormalTransaction transaction) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_Date, transaction.transactionDate_);
        updateValues.put(KEY_IsNotify, transaction.isNotify_);
        updateValues.put(KEY_Description, transaction.description_);
        updateValues.put(KEY_StartTime, transaction.startTime_);
        updateValues.put(KEY_Description, transaction.endTime_);
        return db.update(DB_TABLE, updateValues, KEY_Date + "=" + date, null);
    }

    private NormalTransaction[] ConvertToTransaction(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        NormalTransaction[] transactions = new NormalTransaction[resultCounts];
        for (int i = 0; i < resultCounts; i++) {
            transactions[i] = new NormalTransaction(cursor.getString(cursor.getColumnIndex(KEY_Date)),
                    cursor.getString(cursor.getColumnIndex(KEY_IsNotify)),cursor.getString(cursor.getColumnIndex(KEY_Description)),
                    cursor.getString(cursor.getColumnIndex(KEY_StartTime)),cursor.getString(cursor.getColumnIndex(KEY_EndTime)));
            cursor.moveToNext();
        }
        return transactions;
    }

}
