package com.madrefoca.alumnostango.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.fragments.attendeesTypes.AddAttendeesTypesFragment;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.AttendeeType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendeeTypesFragment extends Fragment {

    private DatabaseHelper databaseHelper = null;
    private FloatingActionButton fabAddNewAttendeeType;
    private ListView attendeeTypesListView;
    ArrayAdapter<String> adapter;


    public AttendeeTypesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_attendee_types, container, false);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        fabAddNewAttendeeType = (FloatingActionButton) thisFragment.findViewById(R.id.fabAddNewAteendeeType);

        fabAddNewAttendeeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAttendeesTypesFragment addAttendeesTypesFragment= new AddAttendeesTypesFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, addAttendeesTypesFragment, "nav_addAttendeesTypes");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //populate list
        this.populateAttendeeTypesList(thisFragment);

        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void populateAttendeeTypesList(View thisFragment){
        attendeeTypesListView = (ListView) thisFragment.findViewById(R.id.attendeesTypeslist);

        Log.d("AttendeeTypesFragment: ", "put the AttendeeTypes in the view...");
        List attendeeTypesArrayList= new ArrayList();
        for (AttendeeType attendeeType : getAllAttendeeTypesFromDatabase()) {
            // Writing AttendeeType to log
            Log.d("AttendeeTypesFrag: ", "Name: " + attendeeType.getName());
            //Writing Illnesses to the view
            attendeeTypesArrayList.add(attendeeType.getName());
        }

        // Adding items to listview
        adapter = new ArrayAdapter<String>(thisFragment.getContext(), R.layout.list_attendee_types_item,
                R.id.attendeeType_name, attendeeTypesArrayList);

        attendeeTypesListView.setAdapter(adapter);
    }

    private List<AttendeeType> getAllAttendeeTypesFromDatabase() {
        // Reading all AttendeeTypes
        Log.d("AttendeeTypesFragment: ", "Reading all AttendeeTypes from database...");
        List<AttendeeType> attendeeTypeList = null;
        try {
            // This is how, a reference of DAO object can be done
            attendeeTypeList = databaseHelper.getAttendeeTypeDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendeeTypeList;
    }
}
