package com.madrefoca.alumnostango.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.adapters.AttendeesDataAdapter;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeType;
import com.madrefoca.alumnostango.utils.AttendeesSimpleCallback;
import com.madrefoca.alumnostango.utils.ManageFragmentsNavigation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendeesFragment extends Fragment {

    private ArrayList<Attendee> attendeesList =  new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private AttendeesDataAdapter attendeesListAdapter;
    private View view;

    @Nullable
    @BindView(R.id.fabAttendeeTypes)
    FloatingActionButton fabAttendeeType;

    @Nullable
    @BindView(R.id.attendeesRecyclerView)
    RecyclerView attendeesRecyclerView;

    //daos
    Dao<AttendeeType, Integer> attendeeTypeDao;
    Dao<Attendee, Integer> attendeeDao;

    public AttendeesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_attendees, container, false);

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
            attendeeDao = databaseHelper.getAttendeeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //databaseHelper.clearTables();

        //Insert some students
        //DatabasePopulatorUtil databasePopulatorUtil = new DatabasePopulatorUtil(databaseHelper);
        //databasePopulatorUtil.populate();

        this.initViews(thisFragment);

        this.populateAttendeesList(thisFragment);
        this.initSwipe(thisFragment);
        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initViews(View thisFragment) {
        attendeesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        attendeesRecyclerView.setLayoutManager(layoutManager);
        attendeesListAdapter = new AttendeesDataAdapter(attendeesList);
        attendeesRecyclerView.setAdapter(attendeesListAdapter);
    }

    private void populateAttendeesList(View thisFragment) {
        Log.d("AttendeeFragment: ", "put the Attendees in the view...");
        attendeesList.addAll(getAllAttendeeFromDatabase());
        attendeesListAdapter.notifyDataSetChanged();
    }

    private void initSwipe(View thisFragment) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new AttendeesSimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, thisFragment.getContext(), attendeesList,
                attendeesListAdapter, thisFragment);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(attendeesRecyclerView);
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

    @Optional
    @OnClick(R.id.fabAttendeeTypes)
    public void onClickDisplayAttendeeTypesFragment() {
        ManageFragmentsNavigation.navItemIndex = 8;
        ManageFragmentsNavigation.CURRENT_TAG = ManageFragmentsNavigation.TAG_ATTENDEE_TYPES;

        // update the main content by replacing fragments
        Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.CURRENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }
}
