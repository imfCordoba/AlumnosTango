package com.madrefoca.alumnostango.fragments.attendeesTypes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.fragments.AttendeeTypesFragment;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.AttendeeType;

import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAttendeesTypesFragment extends Fragment {

    private DatabaseHelper databaseHelper = null;
    private EditText attendeeTypeName;
    private Button btnAdd;


    public AddAttendeesTypesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_add_attendees_types, container, false);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        btnAdd = (Button) thisFragment.findViewById(R.id.attendeeTypeBtnSave);
        attendeeTypeName = (EditText) thisFragment.findViewById(R.id.attendeeTypeName);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //guardar nuevo tipo
                try {
                    AttendeeType attendeeType = new AttendeeType();
                    attendeeType.setName(attendeeTypeName.getText().toString());
                    Dao<AttendeeType, Integer> attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
                    attendeeTypeDao.create(attendeeType);
                    // Writing Illness to log
                    Log.d("AddAttendeesTypesFrag: ", "AttendeeType " + attendeeType.getName() +
                            ", saved in database. ");

                    //back to attendee types list
                    AttendeeTypesFragment attendeeTypesFragment= new AttendeeTypesFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, attendeeTypesFragment, "nav_listAttendeeTypes");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });
        // Inflate the layout for this fragment
        return thisFragment;
    }
}
