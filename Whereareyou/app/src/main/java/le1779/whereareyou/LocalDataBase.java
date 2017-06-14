package le1779.whereareyou;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kevin on 2016/8/2.
 */
public class LocalDataBase extends SQLiteOpenHelper {

    // 資料庫名稱
    public static final String DATABASE_NAME = "localdata.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;
    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase database;
    private String TAG = "SQLite";

    public LocalDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new LocalDataBase(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //String CREATE_GROUP_TABLE = "CREATE TABLE group_table (g_id INTEGER PRIMARY KEY  NOT NULL ,g_name TEXT ,area_lat REAL ,area_long REAL ,area_meter INTEGER ,area_name TEXT ,area_remark TEXT ,area_deadline TEXT)" ;
        String CREATE_GROUP_TABLE = "CREATE TABLE group_table (g_id INTEGER PRIMARY KEY  NOT NULL ,g_name TEXT ,member_id TEXT ,member_NAME TEXT ,member_in INTEGER)" ;
        sqLiteDatabase.execSQL(CREATE_GROUP_TABLE);
        String CREATE_USER_TABLE = "CREATE TABLE user_table (u_id TEXT PRIMARY KEY  NOT NULL ,u_name TEXT ,u_email TEXT ,u_lat REAL ,u_lng REAL ,update_time TEXT)" ;
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        //String CREATE_USER_GROUP_TABLE = "CREATE TABLE user_group_table (_id INTEGER PRIMARY KEY AUTOINCREMENT ,g_id INTEGER ,u_id INTEGER ,in INTEGER)" ;
        //sqLiteDatabase.execSQL(CREATE_USER_GROUP_TABLE);
        String CREATE_AREA_TABLE = "CREATE TABLE area_table (a_id TEXT PRIMARY KEY ,g_id INTEGER ,area_lat REAL ,area_long REAL ,area_meter INTEGER ,area_name TEXT ,area_remark TEXT ,area_deadline TEXT)" ;
        sqLiteDatabase.execSQL(CREATE_AREA_TABLE);
        String CREATE_USER_AREA_TABLE = "CREATE TABLE area_user (_id INTEGER PRIMARY KEY AUTOINCREMENT ,a_id TEXT ,u_id TEXT ,is_enter TEXT)" ;
        sqLiteDatabase.execSQL(CREATE_USER_AREA_TABLE);
        Log.d(TAG, "Create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 刪除原有的表格
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "group_table");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "user_table");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "area_table");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "area_user");
        // 呼叫onCreate建立新版的表格
        onCreate(sqLiteDatabase);
        Log.d(TAG, "Upgrade");
    }

    //public void putGroup(int id, String name, double lat, double lng, int meter, String area_name, String area_remark, String area_deadline) {
    //    ContentValues contentValues = new ContentValues();
    //    contentValues.put("g_id", id);
    //    contentValues.put("g_name", name);
    //    contentValues.put("area_Lat", lat);
    //    contentValues.put("area_Long", lng);
    //    contentValues.put("area_meter", meter);
    //    contentValues.put("area_name", area_name);
    //    contentValues.put("area_remark", area_remark);
    //    contentValues.put("area_deadline", area_deadline);
    //    Cursor cursor = database.query(true,
    //            "group_table",//資料表名稱
    //            new String[]{"g_id", "g_name"},//欄位名稱
    //            "g_id=" + id,//WHERE
    //            null, // WHERE 的參數
    //            null, // GROUP BY
    //            null, // HAVING
    //            null, // ORDOR BY
    //            null  // 限制回傳的rows數量
    //    );
    //    String output = "";
    //    if(cursor != null) {
    //        cursor.moveToFirst();
    //        for (int i = 0; i < cursor.getCount(); i++) {
    //            int g_id = cursor.getInt(0);
    //            String g_name = cursor.getString(1);
    //            output = output + g_id + g_name;
    //            cursor.moveToNext();
    //        }
    //    }
    //    if(output==""){
    //        database.insert("group_table", null, contentValues);
    //        Log.d(TAG, "insert");
    //    }else {
    //        database.update("group_table",
    //                contentValues,
    //                "g_id=" + id,
    //                null);
    //        Log.d(TAG, "update");
    //    }
//
    //}
}
