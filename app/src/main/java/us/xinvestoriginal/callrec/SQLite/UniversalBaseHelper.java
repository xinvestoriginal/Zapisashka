package us.xinvestoriginal.callrec.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import us.xinvestoriginal.callrec.Helpers.LogHelper;
import us.xinvestoriginal.callrec.Models.ProtoEntity;

/**
 * Created by x-inv on 22.04.2017.
 */

public abstract class UniversalBaseHelper extends UpdatableBaseHelper {

    protected static final String SERVER_ID = "id";

    public UniversalBaseHelper(Context context, String baseName, int BASE_VERSION) {
        super(context, baseName, BASE_VERSION);
    }

    public int getMaxId(Class c){
        String sql = "select MAX(" + ID + ") from " + tableName(c);
        return raw(sql);
    }

    public boolean hasId(Class c, int id){
        return hasId(c,ID,String.valueOf(id));
    }

    public boolean hasId(Class c, String selectColumn, String selectValue){
        String sql = "select count(*) from " + tableName(c) +
                     " where " + selectColumn + " = " + selectValue;
        return raw(sql) > 0;
    }

    protected int raw(String sql) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount= db.rawQuery(sql, null);
        mCount.moveToFirst();
        int count= mCount.getColumnCount() > 0 ? mCount.getInt(0) : 0;
        mCount.close();
        db.close();
        return count;
    }

    public synchronized boolean put(ProtoEntity source){
        boolean res;
        if (hasId(source.getClass(),SERVER_ID,String.valueOf(source.id))){
            Update(ID,String.valueOf(source._id),source);
            res = false;
        }else{
            Insert(source);
            res = true;
        }
        return res;
    }

    public boolean delete(ProtoEntity item)
    {
        String tableName = tableName(item.getClass());
        String colName = ID;
        String colVal = String.valueOf(item._id);
        return delete(tableName,colName,colVal);
    }

    protected boolean delete(String tableName, String colName, String colVal)
    {
        final SQLiteDatabase db = getWritableDatabase();
        boolean res = db.delete(tableName, colName + "=" + colVal, null) > 0;
        db.close();
        return res;
    }

    private synchronized long Insert(Object source) {
        String tableName = tableName(source.getClass());
        ContentValues cValues = new ContentValues();
        UpData upData = new UpData(source);
        for (int i = 0; i < upData.columns.length; i++) {
            if (!ID.equals(upData.columns[i])) {
                cValues.put(upData.columns[i], upData.values[i]);
                //LogHelper.print(this,String.valueOf(upData.columns[i]) + " " + String.valueOf(upData.values[i]));
            }
        }
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(tableName, null, cValues);
        db.close();

        return rowID;
    }

    private synchronized int Update(String selectColumn, String selectValue, Object source) {
        String tableName = tableName(source.getClass());
        String selectedString = selectColumn + " = ?";
        ContentValues cValues = new ContentValues();
        UpData upData = new UpData(source);
        for (int i = 0; i < upData.columns.length; i++) {
            cValues.put(upData.columns[i], upData.values[i]);
        }
        SQLiteDatabase db = getWritableDatabase();
        int updCount = db.update(tableName, cValues, selectedString, new String[]{selectValue});
        db.close();
        return updCount;
    }

    public synchronized ArrayList<Object> select(Class c, String[] keys,
                                                 String[] operations, String[] values,
                                                 String limit) {
        String  tableName = tableName(c);
        final SQLiteDatabase db = getWritableDatabase();
        String selection = null;
        String[] args = null;
        if (keys != null && values != null) {
            for (int i = 0; i < keys.length; i++ ){
                if (selection == null){
                    selection = keys[i] + operations[i] + " ?";
                }else{
                    selection += " AND " + keys[i] + operations[i] + " ?";
                }
            }
            args = values;
        }

        //LogHelper.print(this,selection);

        String sort = "id DESC";
        final Cursor cursor =  db.query(tableName, null, selection, args, null, null, sort, limit);
        //final Cursor cursor =  db.query(tableName, null, null, null, null, null, null);
        ArrayList<Object> res = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                res = new ArrayList<>();
                do {
                    String[] item = new String[cursor.getColumnNames().length];
                    for (String column : cursor.getColumnNames()) {
                        int i = cursor.getColumnIndex(column);
                        item[i] = cursor.getString(i);

                    }
                    try {
                        ProtoEntity e = (ProtoEntity)c.newInstance();
                        e.Fill(cursor.getColumnNames(),item);
                        res.add(e);
                    } catch (InstantiationException e1) {
                        LogHelper.print(this,e1.toString());
                    } catch (IllegalAccessException e1) {
                        LogHelper.print(this,e1.toString());
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return res;
    }

    public String toStr(){
        final SQLiteDatabase db = getWritableDatabase();
        Class[] classes = getClasses();
        String res = null;
        for (Class c : classes){
            String temp = toColumnsStr(c,db);
            res = res == null ? temp : "\n" + temp;
        }
        db.close();
        return res;
    }
}
