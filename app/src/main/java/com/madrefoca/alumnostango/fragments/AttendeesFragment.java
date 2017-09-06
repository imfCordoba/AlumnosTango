package com.madrefoca.alumnostango.fragments;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.adapters.AttendeesListAdapter;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeType;
import com.madrefoca.alumnostango.utils.DatabasePopulatorUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendeesFragment extends Fragment {

    private DatabaseHelper databaseHelper = null;
    private FloatingActionButton fabAttendeeType;
    private AttendeesListAdapter attendeesListAdapter;
    private RecyclerView attendeesRecyclerView;
    private LinearLayoutManager linearLayoutManager;


    public AttendeesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_attendees, container, false);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);
        databaseHelper.clearTables();

        //Insert some students
        DatabasePopulatorUtil databasePopulatorUtil = new DatabasePopulatorUtil(databaseHelper);
        databasePopulatorUtil.populate();

        fabAttendeeType = (FloatingActionButton) thisFragment.findViewById(R.id.fabAteendeeTypes);

        attendeesRecyclerView = (RecyclerView) thisFragment.findViewById(R.id.listAttendees);
        linearLayoutManager = new LinearLayoutManager(thisFragment.getContext());
        attendeesListAdapter = new AttendeesListAdapter(thisFragment.getContext());

        attendeesRecyclerView.setLayoutManager(linearLayoutManager);
        attendeesRecyclerView.setAdapter(attendeesListAdapter);

        fabAttendeeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttendeeTypesFragment attendeeTypesFragment= new AttendeeTypesFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, attendeeTypesFragment, "nav_listAttendeeTypes");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        this.populateAttendeesList(thisFragment);
        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void populateAttendeesList(View thisFragment) {

        attendeesRecyclerView = (RecyclerView) thisFragment.findViewById(R.id.listAttendees);

        Log.d("AttendeeFragment: ", "put the Attendees in the view...");
        attendeesListAdapter.clear();
        attendeesListAdapter.addAll(getAllAttendeeFromDatabase());

    }

    private List<Attendee> getAllAttendeeFromDatabase() {
        // Reading all Attendee
        Log.d("AttendeeFragment: ", "Reading all attendees from database...");
        List<Attendee> attendeesList = null;
        try {
            // This is how, a reference of DAO object can be done
            attendeesList = databaseHelper.getAttendeeDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendeesList;
    }

}
