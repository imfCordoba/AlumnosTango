package com.madrefoca.alumnostango.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeType;
import com.madrefoca.alumnostango.model.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
}
