package com.madrefoca.alumnostango.fragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.adapters.AttendeesDataAdapter;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendeeEventPaymentFragment extends Fragment {

    @Nullable
    @BindView(R.id.attendee_list_view)
    ListView attendeeListView;

    @Nullable
    @BindView(R.id.attendee_input_Search)
    EditText attendeeInputSearch;

    private ArrayAdapter<String> adapter;
    private List attendeesArrayList= new ArrayList();

    private Dao<Attendee, Integer> attendeesDao;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private DatabaseHelper databaseHelper = null;

    public AttendeeEventPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_attendee_event_payment, container, false);

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            attendeesDao = databaseHelper.getAttendeeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.initViews(thisFragment);

        return thisFragment;
    }

    private void initViews(final View thisFragment) {
        // Reading all illnesses
        Log.d("AttendeeEventPaymentF: ", "Reading all attendees from database...");
        List<Attendee> attendeeList = null;
        try {
            attendeeList = databaseHelper.getAttendeeDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Log.d("AttendeeEventPaymentF: ", "put the attendees in the view...");
        for (Attendee attendee : attendeeList) {
            // Writing Illness to log
            Log.d("MainActivity: ", "Name: " + attendee.getName() + " ,Lastname: " + attendee.getLastName());
            //Writing Illnesses to the view
            attendeesArrayList.add(attendee.getName() + " " + attendee.getLastName());
        }

        // Adding items to listview
        adapter = new ArrayAdapter<String>(thisFragment.getContext(), R.layout.list_item,
                R.id.attendee_name_lastname, attendeesArrayList);
        attendeeListView.setAdapter(adapter);

        //set listener to the list for the clicks
        attendeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = attendeeListView.getItemAtPosition(i).toString();
                Snackbar snackbar = Snackbar.make(getView(), "Alumno: " + name + " clickeado!",
                        Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.GREEN);
                snackbar.show();
            }
        });

        /**
         * Enabling Search Filter
         * */
        attendeeInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                AttendeeEventPaymentFragment.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        //adding listener to the microphone button
        FloatingActionButton fabMicrophone = (FloatingActionButton) thisFragment.findViewById(R.id.fab_microphone);
        fabMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                //Intent intent = new Intent(MainActivity.this, NewMessageActivity.class);
                //startActivity(intent);
                promptSpeechInput();
            }
        });

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getView().getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    attendeeInputSearch.setText(result.get(0));
                }
                break;
            }
        }
    }
}
