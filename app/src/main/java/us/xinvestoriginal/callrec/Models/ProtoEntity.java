package us.xinvestoriginal.callrec.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import us.xinvestoriginal.callrec.Helpers.LogHelper;

/**
 * Created by x-invest on 13.07.2016.
 */


public abstract class ProtoEntity {

    public long  id;
    public long _id;


    public void Fill(String json){
        try {
            Fill(new JSONObject(json));
        } catch (JSONException e) {
            LogHelper.print(this.getClass(),e.toString());
        }
    }

    public void Fill(JSONObject jsonObject) {
        Object source = this;
        Field[] fields = source.getClass().getFields();
        for (int i = 0; i < fields.length; i++){
            Field f = fields[i];
            if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                String valName = f.getName();
                if (jsonObject.has(valName)){
                    boolean accessible = f.isAccessible();
                    f.setAccessible(true);
                    try {
                        try {
                            f.set(source, cast(f.getType().toString(),jsonObject.getString(valName)));
                        } catch (IllegalAccessException e) {
                            LogHelper.print(this.getClass(),e.toString());
                        }
                    } catch (JSONException e) {
                        LogHelper.print(this.getClass(),e.toString());
                    }
                    f.setAccessible(accessible);
                }
            }
        }
    }

    public void Fill(String[] columns, String[] values) {
        Object source = this;
        for (int i = 0; i < columns.length; i++){
            String fieldName = columns[i];
            String fieldValue = values[i];
            //LogHelper.print(this,fieldName + " = " + fieldValue);
            try {
                Field f = source.getClass().getField(fieldName);
                boolean accessible = f.isAccessible();
                f.setAccessible(true);
                try {
                    f.set(source, cast(f.getType().toString(),fieldValue));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                f.setAccessible(accessible);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    private Object cast(String type,String source){
        if ("boolean".equals(type)){
            try{
                if ("0".equals(source)) return false; else if ("1".equals(source)) return true;
                return Boolean.parseBoolean(source);
            }catch (NumberFormatException ex){
                return false;
            }
        }
        if ("int".equals(type)){
            try{
                return Integer.parseInt(source);
            }catch (NumberFormatException ex){
                return 0;
            }
        }
        if ("long".equals(type)){
            try{
                return Long.parseLong(source);
            }catch (NumberFormatException ex){
                return 0;
            }
        }
        return String.valueOf(source);
    }
}
