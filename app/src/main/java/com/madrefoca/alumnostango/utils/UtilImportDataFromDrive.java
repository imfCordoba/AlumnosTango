package com.madrefoca.alumnostango.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeEventPayment;
import com.madrefoca.alumnostango.model.AttendeeType;
import com.madrefoca.alumnostango.model.Coupon;
import com.madrefoca.alumnostango.model.Event;
import com.madrefoca.alumnostango.model.EventType;
import com.madrefoca.alumnostango.model.Payment;
import com.madrefoca.alumnostango.model.PaymentType;
import com.madrefoca.alumnostango.model.Place;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernando on 18/02/18.
 */

public class UtilImportDataFromDrive extends AsyncTask<String, Integer, String> {
    private ContentResolver contentResolver;
    private DatabaseHelper databaseHelper;

    //daos
    Dao<Place, Integer> placeDao;
    Dao<EventType, Integer> eventTypeDao;
    Dao<PaymentType, Integer> paymentTypeDao;
    Dao<AttendeeType, Integer> attendeeTypeDao;
    Dao<Attendee, Integer> attendeeDao;
    Dao<Event, Integer> eventDao;
    Dao<Coupon, Integer> couponDao;
    Dao<Payment, Integer> paymentDao;
    Dao<AttendeeEventPayment, Integer> attendeeEventPaymentDao;

    List<Place> places = null;
    List<EventType> eventTypes = null;
    List<PaymentType> paymentTypes = null;
    List<AttendeeType> attendeeTypes = null;
    List<Attendee> attendees = null;
    List<Event> events = null;
    List<Coupon> coupons = null;
    List<Payment> payments = null;
    List<AttendeeEventPayment> attendeeEventPayments = null;

    ProgressBar progressBar;
    private Context context;

    private String jsonTables;

    public UtilImportDataFromDrive(Context context, ProgressBar progressBar, String jsonTables) {
        this.context = context;
        this.progressBar = progressBar;
        this.jsonTables = jsonTables;
    }

    @Override
    protected String doInBackground(String... origin) {
        this.importAllTables();

        return "Import all tables task completed.";
    }

    @Override
    protected void onPostExecute(String result) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPreExecute() {
        //progressBar.setMax(this.countFields());
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }

    private void importAllTables() {
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        databaseHelper.clearTables();

        this.saveImportedPlaces(UtilImportJson.placesJsonToMap(this.jsonTables, context));

    }

    private void saveImportedPlaces(ArrayList<Place> placesList) {
        try {
            placeDao = databaseHelper.getPlacesDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Place place: placesList) {
            try {
                placeDao.create(place);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.d("UtilImpDataFromDrive: ", "Saved imported place from drive : " + place.getName() + " in database");
        }
    }
}
