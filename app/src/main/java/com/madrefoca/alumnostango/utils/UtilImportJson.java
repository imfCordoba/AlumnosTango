package com.madrefoca.alumnostango.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fernando on 18/02/18.
 */

public class UtilImportJson {

    public static ArrayList<Place> placesJsonToMap(String jsonPlaces, final Context context) {
        Log.i("JsonUtil", "jsonPlaces string: " + jsonPlaces);
        ArrayList<Place> placesList = new ArrayList<Place>();

        try {
            JSONObject jsonObj = new JSONObject(jsonPlaces);

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
            Toast.makeText(context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return placesList;
    }
}
