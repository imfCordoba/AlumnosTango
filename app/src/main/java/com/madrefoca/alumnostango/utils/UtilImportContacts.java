package com.madrefoca.alumnostango.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ProgressBar;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fernando on 20/01/18.
 */

public class UtilImportContacts extends AsyncTask<String, String, String> {
    // TODO: 20/01/18  The application may be doing too much work on its main thread. investigate this message.

    private ContentResolver contentResolver;
    private DatabaseHelper databaseHelper;

    AttendeeType attendeeType;

    //daos
    Dao<AttendeeType, Integer> attendeeTypeDao;
    Dao<Attendee, Integer> attendeeDao;

    private int countOfContactsSaved = 0;

    private String resp;
    ProgressBar progressBar;
    private Context context;

    public UtilImportContacts(Context context) {
        this.context = context;
    }

    private void importAllContactsFromPhone() {

        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();

            attendeeType = new AttendeeType();
            attendeeType.setName("alumno");

            Map matchingFields = new HashMap();
            matchingFields.put("name", "alumno");

            try {
                //if the attendee type exist, is not created.
                if(attendeeTypeDao.queryForFieldValues(matchingFields).size() == 0) {
                    attendeeTypeDao.create(attendeeType);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            attendeeDao = databaseHelper.getAttendeeDao();
            databaseHelper.clearTable("attendees");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        contentResolver = context.getContentResolver();

        Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                    Map<String, String> contact = new HashMap<String, String>();
                    contact.put("nameContact", cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                    contact.put("phoneContact", retrieveContactNumber(id));
                    contact.put("emailContact", retrieveContactEmail(id));

                    this.saveImportedContact(contact);

                }
            }
            Log.i("UtilImportContacts: ", "Count of total contacts: " + cur.getCount());
            Log.i("UtilImportContacts: ", "Count of contacts saved: " + this.countOfContactsSaved);
        }
    }

    private String retrieveContactNumber(String idContact) {
        String phoneNumber = "";
        // get the phone number
        // TODO: 20/01/18 creo que esto te devuelve el ultimo numero. Revisar
        Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                new String[]{idContact}, null);
        if(!pCur.isAfterLast()) {
            pCur.moveToNext();
            phoneNumber = pCur.getString(
                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
        }

        pCur.close();

        return phoneNumber;
    }

    private String retrieveContactEmail(String idContact) {
        String email = "";
        Cursor emailCur = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{idContact}, null);

        if(!emailCur.isAfterLast()) {
            emailCur.moveToNext();
            email = emailCur.getString(
                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)).trim();
        }

        emailCur.close();

        return email;
    }

    private void saveImportedContact(Map<String, String> contact) {
        // TODO: 16/09/17 agregar una validacion por si tratan de crear un alumno y no tienen cargados los tipos de alumnos.
        String attendeTypeSelected = attendeeType.getName();
        try {
            attendeeType = attendeeTypeDao.queryForEq("name", attendeTypeSelected).get(0);
        } catch (SQLException e) {
            Log.d("UtilImportContacts: ", "Cannot find attendee type: " + attendeTypeSelected + "in database");
            e.printStackTrace();
        }

        Attendee attendee = new Attendee();
        attendee.setName(contact.get("nameContact"));
        //attendee.setLastName(attendeeLastName.getText().toString());
        attendee.setAlias();
        attendee.setCellphoneNumber(contact.get("phoneContact"));
        attendee.setEmail(contact.get("emailContact"));
        attendee.setAttendeeType(attendeeType);
        attendee.setState("active");

        try {
            attendeeDao.create(attendee);
        } catch (SQLException e) {
            Log.e("UtilImportContacts: ", "Cannot save imported contact: " + contact.get("nameContact") + " in database");
            e.printStackTrace();
        }
        this.countOfContactsSaved++;
        Log.d("UtilImportContacts: ", "Saved imported contact: " + contact.get("nameContact"));

    }

    @Override
    protected String doInBackground(String... params) {
        publishProgress("Working..."); // Calls onProgressUpdate()
        resp = "Working";
        this.importAllContactsFromPhone();

        return resp;
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(String... text) {
        //finalResult.setText(text[0]);

    }
}


