package com.madrefoca.alumnostango.fragments;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.madrefoca.alumnostango.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendeesFragment extends Fragment {

    private FloatingActionButton fabNewAttendeeType;


    public AttendeesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_attendees, container, false);

        fabNewAttendeeType = (FloatingActionButton) thisFragment.findViewById(R.id.fabNewAteendeeType);

        fabNewAttendeeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttendeeTypesFragment attendeeTypesFragment= new AttendeeTypesFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, attendeeTypesFragment, "tag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        // Inflate the layout for this fragment
        return thisFragment;
    }

}
