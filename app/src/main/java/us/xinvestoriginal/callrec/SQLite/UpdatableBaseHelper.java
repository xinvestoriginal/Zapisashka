package us.xinvestoriginal.callrec.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by x-invest on 11.02.2016.
 */
public abstract class UpdatableBaseHelper extends SQLiteOpenHelper {

    protected static class UpData{

        private static final String IGNORED_FIELD = "serialVersionUID";

        public String[] columns;
        public String[]   types;
        public String[]  values;

        public UpData(){

        }

        public UpData(Object source){
            fill(source.getClass().getFields(),source);
        }

        public UpData(Field[] fields){
            fill(fields,null);
        }

        private void fill(Field[] fields, Object instance){
            ArrayList<String> columns = new ArrayList<>();
            ArrayList<String> types   = new ArrayList<>();
            ArrayList<String> values  = new ArrayList<>();
            for (int i = 0; i < fields.length; i++){
                Field f = fields[i];
                if (validField(f)) {
                    f.setAccessible(true);
                    String cName = f.getName();
                    String cType = toSqliteType(f.getType().toString());
                    if (instance != null){
                        try {
                            String cValue = String.valueOf(f.get(instance));
                            values.add(toSqliteValue(f.getType().toString(),cValue));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    columns.add(cName);
                    types.add(cType);
                }
            }
            this.values  = instance != null ? listToArr(values) : null;
            this.columns = listToArr(columns);
            this.types   =   listToArr(types);
        }

        private boolean validField(Field f){
            return !IGNORED_FIELD.equals(f.getName()) &&
                    !f.isSynthetic() && !java.lang.reflect.Modifier.isStatic(f.getModifiers());
        }

        private String[] listToArr(List<String> source){
            String[] res = new String[source.size()];
            return source.toArray(res);
        }
    }

    public  static final String  ID = "_id";


    protected abstract  Class[]  getClasses();

    protected static String tableName(Class c){
        return c.getSimpleName()+"sTable";
    }

    private static String createTableString(Class c){
        UpData upData = new UpData(c.getFields());
        return createTableString(tableName(c),upData.columns,upData.types);
    }

    private static String createTableString(String TABLE_NAME, String[]  COLUMNS, String[] TYPES) {
        String res = "create table IF NOT EXISTS " + TABLE_NAME + "  (" + ID + " integer primary key autoincrement";
        for (int i = 0; i < COLUMNS.length; i++) {
            if (!ID.equals(COLUMNS[i])) res += ", " + COLUMNS[i] + " " + TYPES[i];
        }
        res += ");";
        return res;
    }

    private static String toSqliteType(String source){
        return "int".equals(source) || "long".equals(source) ||
               "boolean".equals(source) ? "integer" : "text";
    }

    private static String toSqliteValue(String type, String source){
        //Log.e("toSqliteValue",type + " " + source );
        return !"boolean".equals(type) ? source : String.valueOf(true).equals(source) ? "1" : "0";
    }

    private static boolean arrContainsValue(String[] arr, String value){
        boolean res = false;
        for (int i = 0; !res && i < arr.length; i++){
            res = value.equals(arr[i]);
        }
        return res;
    }

    private static String[] classToFields(Class c){
        UpData upData = new UpData(c.getFields());
        return upData.columns;
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
            Log.e(UpdatableBaseHelper.class.getName(),e.toString());
        }
    }



    private UpData getUpData(Class c, final SQLiteDatabase db){
        String table = tableName(c);
        final Cursor cursor = db.query(table, null, null, null, null, null, null, null);
        UpData res;
        if (cursor != null) {

            ArrayList<String> newCols  = new ArrayList<>();
            ArrayList<String> newTypes = new ArrayList<>();
            String[] cols = cursor.getColumnNames();
            UpData upData = new UpData(c.getFields());

            for (int i = 0; i < upData.columns.length; i++){
                if ( !arrContainsValue(cols,upData.columns[i])){
                    newCols.add(upData.columns[i]);
                    newTypes.add(upData.types[i]);
                }
            }

            cursor.close();
            if (newCols.size() == 0 && newTypes.size() == 0){
                res = null;
            }else{
                res = new UpData();
                res.columns = res.listToArr(newCols);
                res.types  = res.listToArr(newTypes);
            }
        }else{
            res = null;
        }

        return res;
    }






    protected String toColumnsStr(Class c, SQLiteDatabase db){

        String table = tableName(c);
        final Cursor cursor = db.query(table, null, null, null, null, null, null, null);
        String res = "table: " + table + ", columns: ";
        if (cursor != null){
            String[] cols = cursor.getColumnNames();
            for (int i = 0; i < cols.length; i++){
                res += i == 0 ? cols[i] : ", " + cols[i];
            }
            cursor.close();
        }

        return res;
    }




    // typecal constructor with throw if cols != types array
    public UpdatableBaseHelper(Context context, String baseName, int BASE_VERSION) {
        super(context,baseName, null, BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Class[] classes = getClasses();
        for (Class c : classes){
            db.execSQL(createTableString(c));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Class[] classes = getClasses();
        for (Class c : classes){
            db.execSQL(createTableString(c));
        }
        for (Class c : classes){
            UpData upData = getUpData(c,db);
            if (upData != null){
                //LogHelper.print(this,upData.toStr());
                for (int i = 0; i < upData.columns.length; i++){
                    String upgradeSql = "ALTER TABLE " + tableName(c) +
                            " ADD COLUMN " + upData.columns[i] + " " + upData.types[i];
                    db.execSQL(upgradeSql);
                }
            }
        }
    }
}