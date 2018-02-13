package com.madrefoca.alumnostango.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeType;
import com.madrefoca.alumnostango.model.Event;
import com.madrefoca.alumnostango.model.EventType;
import com.madrefoca.alumnostango.model.PaymentType;
import com.madrefoca.alumnostango.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando Gomez de Paz on 1/25/18.
 */

public class JsonUtil {

    public static String attendeesToJSon(List<Attendee> attendees) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonAttendeesArray = new JSONArray();

        for(Attendee attendee : attendees) {

            try {
                // Here we convert Java Object to JSON
                JSONObject jsonAttendee = new JSONObject();
                jsonAttendee.put("attendeeId", attendee.getAttendeeId());
                jsonAttendee.put("attendeeType", attendee.getAttendeeType().getName());
                jsonAttendee.put("name", attendee.getName());
                jsonAttendee.put("lastName", attendee.getLastName());
                jsonAttendee.put("age", attendee.getAge());
                jsonAttendee.put("cellphoneNumber", attendee.getCellphoneNumber());
                jsonAttendee.put("facebookProfile", attendee.getFacebookProfile());
                jsonAttendee.put("email", attendee.getFacebookProfile());
                jsonAttendee.put("alias", attendee.getAlias());
                jsonAttendee.put("state", attendee.getState());

                //jsonObj.put(attendee.getAttendeeId().toString(), jsonAttendee);
                jsonAttendeesArray.put(jsonAttendee);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

            try {
                jsonObj.put("attendees",jsonAttendeesArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jsonObj.toString();
    }

    public static String eventsToJSon(List<Event> events) {
        JSONObject jsonEvents = new JSONObject();
        JSONArray jsonEventsArray = new JSONArray();

        for(Event event : events) {

            try {
                // Here we convert Java Object to JSON
                JSONObject jsonEvent = new JSONObject();
                jsonEvent.put("idEvent", event.getIdEvent());
                jsonEvent.put("idPlace", event.getPlace().getIdplace());
                jsonEvent.put("name", event.getName());
                jsonEvent.put("day", event.getDay());
                jsonEvent.put("month", event.getMonth());
                jsonEvent.put("year", event.getYear());
                jsonEvent.put("hour", event.getHour());
                jsonEvent.put("minutes", event.getMinutes());
                jsonEvent.put("paymentAmount", event.getPaymentAmount());
                jsonEvent.put("state", event.getState());

                jsonEventsArray.put(jsonEvent);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

            try {
                jsonEvents.put("events",jsonEventsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jsonEvents.toString();
    }

    public static ArrayList<Attendee> jsonToMap(String jsonContacts, final Context context) {
        Log.i("JsonUtil", "jsonContacts string: " + jsonContacts);
        ArrayList<Attendee> attendeeContacts = new ArrayList<Attendee>();


        try {
            JSONObject jsonObj = new JSONObject(jsonContacts);

            // Getting JSON Array node
            JSONArray contacts = jsonObj.getJSONArray("attendees");

            // looping through All Contacts
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                Attendee attendee = new Attendee();
                attendee.setName(c.getString("name"));
                attendee.setCellphoneNumber(c.getString("cellphoneNumber"));
                attendee.setEmail(c.isNull("email") ? "" : c.getString("email"));
                attendee.setState(c.getString("state"));

                // adding contact to contact list
                attendeeContacts.add(attendee);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return attendeeContacts;
    }

    public static String allTablesToJSon(DatabaseHelper databaseHelper) {
        Dao<Place, Integer> placeDao;
        Dao<EventType, Integer> eventTypeDao;
        Dao<PaymentType, Integer> paymentTypeDao;

        List<Place> places = null;
        List<EventType> eventTypes = null;
        List<PaymentType> paymentTypes = null;

        try {
            placeDao = databaseHelper.getPlacesDao();
            places = placeDao.queryForAll();

            eventTypeDao = databaseHelper.getEventTypesDao();
            eventTypes = eventTypeDao.queryForAll();

            paymentTypeDao = databaseHelper.getPaymentTypesDao();
            paymentTypes = paymentTypeDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("places", placesToJson(places));
            jsonObj.put("eventTypes", eventTypesToJson(eventTypes));
            jsonObj.put("paymentTypes", paymentTypesToJson(paymentTypes));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    public static JSONArray placesToJson (List<Place> places) {
        JSONArray jsonPlacesArray = new JSONArray();

        for(Place place : places) {
            try {
                // Here we convert Java Object to JSON
                JSONObject jsonPlace = new JSONObject();
                jsonPlace.put("name", place.getName());
                jsonPlace.put("address", place.getAddress());
                jsonPlace.put("gpsLocation", place.getGpsLocation());
                jsonPlace.put("phone", place.getPhone());
                jsonPlace.put("email", place.getEmail());
                jsonPlace.put("facebookLink", place.getFacebookLink());

                jsonPlacesArray.put(jsonPlace);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

        }
        return jsonPlacesArray;
    }

    public static JSONArray eventTypesToJson (List<EventType> eventTypes) {
        JSONArray jsonEventTypesArray = new JSONArray();

        for(EventType eventType : eventTypes) {
            try {
                JSONObject jsonEventType = new JSONObject();
                jsonEventType.put("idEventType", eventType.getIdEventType());
                jsonEventType.put("name", eventType.getName());
                jsonEventType.put("description", eventType.getDescription());

                jsonEventTypesArray.put(jsonEventType);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

        }
        return jsonEventTypesArray;
    }

    public static JSONArray paymentTypesToJson (List<PaymentType> paymentTypes) {
        JSONArray jsonPaymentTypesArray = new JSONArray();

        for(PaymentType paymentType : paymentTypes) {
            try {
                JSONObject jsonPaymentType = new JSONObject();
                jsonPaymentType.put("idPaymentType", paymentType.getIdPaymentType());
                jsonPaymentType.put("name", paymentType.getName());
                jsonPaymentType.put("description", paymentType.getDescription());

                jsonPaymentTypesArray.put(jsonPaymentType);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

        }
        return jsonPaymentTypesArray;
    }
}
