package com.madrefoca.alumnostango.utils;

import com.madrefoca.alumnostango.model.Attendee;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by developer on 1/25/18.
 */

public class JsonUtil {
    public static String toJSon(List<Attendee> attendees) {
        JSONObject jsonObj = new JSONObject();

        for(Attendee attendee : attendees) {

            try {
                // Here we convert Java Object to JSON
                JSONObject jsonAttendee = new JSONObject();
                jsonAttendee.put("attendeeId", attendee.getAttendeeId());

                JSONObject jsonAttendeeType = new JSONObject();
                jsonAttendeeType.put("idAttendeeType", attendee.getAttendeeType().getIdAttendeeType());
                jsonAttendeeType.put("name", attendee.getAttendeeType().getName());

                jsonAttendee.put("attendeeType", jsonAttendeeType);
                jsonAttendee.put("name", attendee.getName());
                jsonAttendee.put("lastName", attendee.getLastName());
                jsonAttendee.put("age", attendee.getAge());
                jsonAttendee.put("cellphoneNumber", attendee.getCellphoneNumber());
                jsonAttendee.put("facebookProfile", attendee.getFacebookProfile());
                jsonAttendee.put("email", attendee.getFacebookProfile());
                jsonAttendee.put("alias", attendee.getAlias());
                jsonAttendee.put("state", attendee.getState());

                jsonObj.put(attendee.getAttendeeId().toString(), jsonAttendee);
           /* JSONObject jsonAdd = new JSONObject(); // we need another object to store the address
            jsonAdd.put("address", person.getAddress().getAddress());
            jsonAdd.put("city", person.getAddress().getCity());
            jsonAdd.put("state", person.getAddress().getState());*/

                // We add the object to the main object
                //jsonObj.put("address", jsonAdd);

                // and finally we add the phone number
                // In this case we need a json array to hold the java list
            /*JSONArray jsonArr = new JSONArray();

            for (PhoneNumber pn : person.getPhoneList() ) {
                JSONObject pnObj = new JSONObject();
                pnObj.put("num", pn.getNumber());
                pnObj.put("type", pn.getType());
                jsonArr.put(pnObj);
            }

            jsonObj.put("phoneNumber", jsonArr);*/



            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

        }
        return jsonObj.toString();
    }
}
