package us.xinvestoriginal.callrec.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by x-invest on 11.02.2016.
 */
public abstract class DefaultBaseHelper extends SQLiteOpenHelper {


    private static final int    BASE_VERSION = 1;

    public  static final String           ID = "id";
    public  static final String        STIME = "sqltime";

    protected abstract String[] GetDefaultTypes(String[] columns);


    public int GetCount(String tableName,String key,String value){
        return GetCount(tableName,new String[]{key}, null, new String[]{value});
    }

    public int GetCount(String tableName,String[] keys, String[] oprs, String[] values){
        return GetCount(tableName,keys, oprs, values, true);
    }

    public synchronized int GetCount(String tableName,String[] keys, String[] oprs,
                                     String[] values, boolean needQuotes) {
        String whereStr = "";
        for (int i = 0; i < keys.length; i++){
            if (whereStr.length() > 0) whereStr += " AND ";
            String opr = oprs == null || i > oprs.length - 1 ? " = " : oprs[i];
            whereStr += keys[i] + opr;
            whereStr += needQuotes ? " '" + values[i] + "'" : " " + values[i];
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select count(*) from " + tableName + " where " + whereStr;

        Cursor mCount= db.rawQuery(sql, null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        db.close();

        //if (!needQuotes) Log.e("@@@ ",sql + " " + String.valueOf(count));
        return count;
    }
    // see down
    public int Update(String tableName, String id, String[] columns, String[] values) {
        return Update(tableName, ID, id, columns, values);
    }

    // update row from col and vals arrays wher column = selectValue
    public synchronized int Update(String tableName, String selectColumn, String selectValue, String[] columns, String[] values) {
        if (columns == null || values == null) throw new RuntimeException("columns or values is null");
        if (columns.length  != values.length)  throw new RuntimeException("columns and values length not equils");
        if (columns.length == 0) throw new RuntimeException("columns length is zero");

        String selectedString = selectColumn + " = ?";
        ContentValues cValues = new ContentValues();
        //-------------------------------------------
        cValues.put(STIME,getDateTime());
        //-------------------------------------------
        for (int i = 0; i < columns.length; i++) cValues.put(columns[i], values[i]);
        SQLiteDatabase db = getWritableDatabase();
        int updCount = db.update(tableName, cValues, selectedString, new String[]{selectValue});
        db.close();
        return updCount;
    }

    //insert new row data from col and vals arrays
    public long Insert(String tableName, String[] columns, String[] values) {
        if (columns == null || values == null) throw new RuntimeException("columns or values is null");
        if (columns.length != values.length) throw new RuntimeException("columns and values length not equils");
        if (columns.length == 0) throw new RuntimeException("columns length is zero");
        ContentValues cValues = new ContentValues();
        //-------------------------------------------
        cValues.put(STIME,getDateTime());
        //-------------------------------------------
        for (int i = 0; i < columns.length; i++) cValues.put(columns[i], values[i]);
        return Insert(tableName, cValues);
    }

    //insert new row data from ContentValues
    public synchronized long Insert(String tableName, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(tableName, null, values);
        db.close();
        return rowID;
    }

    // clear table
    public int Clear(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        int clearCount = db.delete(tableName, null, null);
        db.close();
        return clearCount;
    }

    // select all rows from base
    public ArrayList<String[]> Select(String tableName) {
        return Select(tableName,null, null, null, null, null);
    }

    // select from base on mask
    public ArrayList<String[]> Select(String tableName, String key, String value) {
        return Select(tableName, key, "=", value);
    }

    public ArrayList<String[]> Select(String tableName, String key, String operation, String value) {
        return SelectLim(tableName,key,operation,value, null);
    }

    public ArrayList<String[]> SelectLim(String tableName, String key, String operation, String value, String limit) {
        return  Select(tableName, null, new String[]{key},new String[]{operation},new String[]{value},limit);
    }

    public  ArrayList<String[]> Select(String    tableName, String[] columns, String[] keys,
                                                   String[] operations, String[] values,
                                                   String limit){
       return Select(tableName,columns,keys,operations,values,limit,"id DESC");
    }
        // select from base on mask with operation
    public synchronized ArrayList<String[]> Select(String    tableName, String[] columns, String[] keys,
                                                   String[] operations, String[] values,
                                                   String limit, String sort) {
        ArrayList<String[]> res = new ArrayList<String[]>();

        final SQLiteDatabase db = getWritableDatabase();
        String selection = null;
        String[] selectionArgs = null;
        if (keys != null && values != null) {
            for (int i = 0; i < keys.length; i++ ){
                if (selection == null){
                    selection = keys[i] + operations[i] + " ?";
                }else{
                    selection += " AND " + keys[i] + operations[i] + " ?";
                }
            }
            selectionArgs = values;

        }

        final Cursor cursor = db.query(tableName, columns, selection, selectionArgs, null, null, sort, limit);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String[] item = new String[cursor.getColumnNames().length];
                    for (String column : cursor.getColumnNames()) {
                        int i = cursor.getColumnIndex(column);
                        item[i] = cursor.getString(i);
                    }
                    res.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }
        db.close();
        return res;
    }

    protected boolean Delete(String tableName, String colName, String colVal)
    {
        final SQLiteDatabase db = getWritableDatabase();
        boolean res = db.delete(tableName, colName + "=" + colVal, null) > 0;
        db.close();
        return res;
    }

    protected String GetCreateTableString(String TABLE_NAME, String[] COLUMNS, String[] TYPES) {
        String res = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement";
        res += ", " + STIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        for (int i = 0; i < COLUMNS.length; i++) res += ", " + COLUMNS[i] + " " + TYPES[i];
        res += ");";
        return res;
    }

    protected String GetCreateTableString(String TABLE_NAME, String[] COLUMNS) {
        return GetCreateTableString(TABLE_NAME,COLUMNS,GetDefaultTypes(COLUMNS));
    }

    // typecal constructor with throw if cols != types array
    public DefaultBaseHelper(Context context, String baseName) {
        super(context,baseName, null, BASE_VERSION);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void ExportDatabase(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+context.getPackageName()+"//databases//"+databaseName+"";
                String backupDBPath = "backup_"+databaseName;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }
}