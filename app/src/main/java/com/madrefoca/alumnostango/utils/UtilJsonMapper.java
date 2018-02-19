package com.madrefoca.alumnostango.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeType;
import com.madrefoca.alumnostango.model.EventType;
import com.madrefoca.alumnostango.model.PaymentType;
import com.madrefoca.alumnostango.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by fernando on 18/02/18.
 */

public class UtilJsonMapper {

    private JSONObject jsonObj;
    private Context context;
    private DatabaseHelper databaseHelper;
    private Dao<AttendeeType, Integer> attendeeTypeDao;

    public UtilJsonMapper(String jsonTables, Context context) {
        try {
            this.jsonObj = new JSONObject(jsonTables);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.context = context;
        databaseHelper = OpenHelperManager.getHelper(this.context, DatabaseHelper.class);
    }

    public ArrayList<Place> placesJsonMapper() {
        ArrayList<Place> placesList = new ArrayList<Place>();

        try {
            // Getting JSON Array node
            JSONArray placesJson = jsonObj.getJSONArray("places");

            // looping through All places
            for (int i = 0; i < placesJson.length(); i++) {
                JSONObject c = placesJson.getJSONObject(i);
                Place place = new Place();
                place.setId(Integer.getInteger(c.getString("idPlace")));
                place.setName(c.getString("name"));
                place.setAddress( c.getString("address"));

                // adding contact to contact list
                placesList.add(place);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return placesList;
    }

    public ArrayList<EventType> eventTypesJsonMapper() {
        ArrayList<EventType> eventTypeArrayList = new ArrayList<EventType>();

        try {
            // Getting JSON Array node
            JSONArray eventTypesJson = jsonObj.getJSONArray("eventTypes");

            // looping through All eventTypes
            for (int i = 0; i < eventTypesJson.length(); i++) {
                JSONObject c = eventTypesJson.getJSONObject(i);
                EventType eventType = new EventType();
                eventType.setIdEventType(Integer.getInteger(c.getString("idEventType")));
                eventType.setName(c.getString("name"));
                eventType.setDescription(c.getString("description"));

                eventTypeArrayList.add(eventType);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return eventTypeArrayList;
    }

    public ArrayList<PaymentType> paymentTypesMapper() {
        ArrayList<PaymentType> paymentTypeArrayList = new ArrayList<PaymentType>();

        try {
            // Getting JSON Array node
            JSONArray paymentTypesJson = jsonObj.getJSONArray("paymentTypes");

            // looping through All paymentTypesJson
            for (int i = 0; i < paymentTypesJson.length(); i++) {
                JSONObject c = paymentTypesJson.getJSONObject(i);
                PaymentType paymentType = new PaymentType();
                paymentType.setIdPaymentType(Integer.getInteger(c.getString("idPaymentType")));
                paymentType.setName(c.getString("name"));
                paymentType.setDescription(c.getString("description"));

                paymentTypeArrayList.add(paymentType);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return paymentTypeArrayList;
    }

    public ArrayList<AttendeeType> attendeeTypesMapper() {
        ArrayList<AttendeeType> attendeeTypeArrayList = new ArrayList<AttendeeType>();

        try {
            // Getting JSON Array node
            JSONArray attendeeTypesJson = jsonObj.getJSONArray("attendeeTypes");

            // looping through All attendeeTypesJson
            for (int i = 0; i < attendeeTypesJson.length(); i++) {
                JSONObject c = attendeeTypesJson.getJSONObject(i);
                AttendeeType attendeeType = new AttendeeType();
                attendeeType.setIdAttendeeType(Integer.getInteger(c.getString("idAttendeeType")));
                attendeeType.setName(c.getString("name"));
                attendeeType.setDescription(c.getString("description"));

                attendeeTypeArrayList.add(attendeeType);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return attendeeTypeArrayList;
    }

    public ArrayList<Attendee> attendeeMapper() {
        ArrayList<Attendee> attendeeArrayList = new ArrayList<Attendee>();

        try {
            // Getting JSON Array node
            JSONArray attendeeJson = jsonObj.getJSONArray("attendees");

            // looping through All attendeeTypesJson
            for (int i = 0; i < attendeeJson.length(); i++) {
                JSONObject c = attendeeJson.getJSONObject(i);
                Attendee attendee = new Attendee();
                attendee.setAttendeeId(Integer.getInteger(c.getString("attendeeId")));
                attendee.setName(c.getString("name"));

                AttendeeType attendeeType = attendeeTypeDao.queryForId(Integer.valueOf(c.getString("attendeeType")));
                attendee.setAttendeeType(attendeeType);
                attendee.setName(c.getString("name"));
                attendee.setLastName(c.getString("lastName"));
                attendee.setAge(Integer.valueOf(c.getString("age")));
                attendee.setCellphoneNumber(c.getString("cellphoneNumber"));
                attendee.setFacebookProfile(c.getString("facebookProfile"));
                attendee.setEmail(c.getString("email"));
                attendee.setAlias();
                attendee.setState(c.getString("state"));

                attendeeArrayList.add(attendee);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendeeArrayList;
    }
}
