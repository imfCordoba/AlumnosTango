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

    ArrayList<Place> places = null;
    ArrayList<EventType> eventTypes = null;
    ArrayList<PaymentType> paymentTypes = null;
    ArrayList<AttendeeType> attendeeTypes = null;
    ArrayList<Attendee> attendees = null;
    ArrayList<Event> events = null;
    ArrayList<Coupon> coupons = null;
    ArrayList<Payment> payments = null;
    ArrayList<AttendeeEventPayment> attendeeEventPayments = null;

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

        UtilJsonMapper utilJsonMapper = new UtilJsonMapper(this.jsonTables, context);

        this.saveImportedPlaces(utilJsonMapper.placesJsonMapper());
        this.saveImportedEventTypes(utilJsonMapper.eventTypesJsonMapper());
        this.savePaymentTypes(utilJsonMapper.paymentTypesMapper());
        this.saveAttendeeTypes(utilJsonMapper.attendeeTypesMapper());
        this.saveAttendees(utilJsonMapper.attendeeMapper());

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

    private void saveImportedEventTypes(ArrayList<EventType> eventTypeArrayList) {
        try {
            eventTypeDao = databaseHelper.getEventTypesDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(EventType eventType: eventTypeArrayList) {
            try {
                eventTypeDao.create(eventType);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.d("UtilImpDataFromDrive: ", "Saved imported event type from drive : " + eventType.getName() + " in database");
        }
    }

    private void savePaymentTypes(ArrayList<PaymentType> paymentTypeArrayList) {
        try {
            paymentTypeDao = databaseHelper.getPaymentTypesDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(PaymentType paymentType: paymentTypeArrayList) {
            try {
                paymentTypeDao.create(paymentType);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.d("UtilImpDataFromDrive: ", "Saved imported payment type from drive : " + paymentType.getName() + " in database");
        }
    }

    private void saveAttendeeTypes(ArrayList<AttendeeType> attendeeTypeArrayList) {
        try {
            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(AttendeeType attendeeType: attendeeTypeArrayList) {
            try {
                attendeeTypeDao.create(attendeeType);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.d("UtilImpDataFromDrive: ", "Saved imported atteendee type from drive : " + attendeeType.getName() + " in database");
        }
    }

    private void saveAttendees(ArrayList<Attendee> attendeeTypeArrayList) {
        try {
            attendeeDao = databaseHelper.getAttendeeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Attendee attendee: attendeeTypeArrayList) {
            try {
                attendeeDao.create(attendee);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.d("UtilImpDataFromDrive: ", "Saved imported atteendee from drive : " + attendee.getAlias() + " in database");
        }
    }
}
