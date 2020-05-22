package us.xinvestoriginal.callrec.Helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.xinvestoriginal.callrec.Models.NumberEntity;

/**
 * Created by x-inv on 24.08.2017.
 */

public class PhoneBookHelper {

    public static String uniTel(String source){
        if (source == null) return null;
        String res = source.replaceAll("[^0-9]", "");
        if (res.length() <= 10) return res;
        return res.substring(res.length() - 10);
    }

    public static List<NumberEntity> ReadContactsFromPhone(Context context){



        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur.getCount() == 0) return null;
        Map<String,NumberEntity> contacts = new HashMap<>();
        while (cur.moveToNext()) {

            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
                while (pCur.moveToNext()) {
                    String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (phone != null && phone.length() > 0) {
                        NumberEntity phoneContact = new NumberEntity();
                        phoneContact.numName = name;
                        phoneContact.phone = phone;
                        phoneContact.enable = false;
                        if (!contacts.containsKey(phoneContact.phone)) {
                            contacts.put(phoneContact.phone,phoneContact);
                        }
                    }
                }
                pCur.close();
            }
        }

        List<NumberEntity> res = new ArrayList<>(contacts.values());

        Collections.sort(res, new Comparator<NumberEntity>() {
            @Override
            public int compare(NumberEntity item1, NumberEntity item2) {
                return item1.numName.compareToIgnoreCase(item2.numName);
            }
        });

        return res;
    }

    public static String getContactName(Context context, String number) {
        String name;
        // define the columns I want the query to return
        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};
        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            } else {
                //Contact Not Found
                name = null;
            }
            cursor.close();
        }else{
            name = null;
        }
        return name;
    }






}
