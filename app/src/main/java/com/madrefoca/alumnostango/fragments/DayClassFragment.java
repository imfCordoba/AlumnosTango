package com.madrefoca.alumnostango.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madrefoca.alumnostango.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayClassFragment extends Fragment {


    public DayClassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day_class, container, false);
    }

}
