package com.madrefoca.alumnostango.utils;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeType;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by fernando on 27/08/17.
 */

public class DatabasePopulatorUtil {
    private DatabaseHelper databaseHelper = null;

    public DatabasePopulatorUtil() {
    }

    public DatabasePopulatorUtil(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void populate(){
        this.insertSomeStudents();
    }

    private void insertSomeStudents() {
        Log.d("DatabasePopulatorUtil: ", "insertSomeIllnessesAndSchools()");

        try {
            // This is how, a reference of DAO object can be done
            final Dao<Attendee, Integer> attendeeDao = databaseHelper.getAttendeeDao();

            ArrayList<Attendee> attendeeArrayList = new ArrayList<Attendee>();
            attendeeArrayList.add(new Attendee(30242306, "Fernando", 153251291));
            attendeeArrayList.add(new Attendee(30242303, "Daniel", 153251291));
            attendeeArrayList.add(new Attendee(30244306, "Pepe", 153251291));
            attendeeArrayList.add(new Attendee(30142306, "Eduardo", 153251291));
            attendeeArrayList.add(new Attendee(30202306, "Jeremy", 153251291));
            attendeeArrayList.add(new Attendee(30249306, "Jannett", 153251291));
            attendeeArrayList.add(new Attendee(30242316, "Ezequiel", 153251291));


            for(Attendee attendee: attendeeArrayList){
                //This is the way to insert data into a database table
                attendeeDao.create(attendee);
                String log = "Attendee " + attendee.getName() + ", saved in database. " ;
                // Writing Illness to log
                Log.d("DatabasePopulatorUtil: ", log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
