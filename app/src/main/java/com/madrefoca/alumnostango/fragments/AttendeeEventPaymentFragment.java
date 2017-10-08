package com.madrefoca.alumnostango.fragments;


import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeEventPayment;
import com.madrefoca.alumnostango.model.Event;
import com.madrefoca.alumnostango.model.Payment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

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

    @Nullable
    @BindView(R.id.text_attendee_selected)
    EditText textAttendeeSelected;

    @Nullable
    @BindView(R.id.text_view_amount)
    EditText textViewAmount;

    @Nullable
    @BindView(R.id.fabSavePayment)
    FloatingActionButton fabSavePayment;

    private ArrayAdapter<String> adapter;
    private List attendeesArrayList= new ArrayList();

    private Dao<Attendee, Integer> attendeesDao;
    private Dao<AttendeeEventPayment, Integer> attendeeEventPaymentDao;
    private Dao<Payment, Integer> paymentsDao;
    private Dao<Event, Integer> eventsDao;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private DatabaseHelper databaseHelper = null;
    private Bundle bundle;

    public AttendeeEventPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_attendee_event_payment, container, false);

        bundle = new Bundle();
        this.bundle = this.getArguments();

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            attendeesDao = databaseHelper.getAttendeeDao();
            attendeeEventPaymentDao = databaseHelper.getAttendeeEventPaymentDao();
            paymentsDao = databaseHelper.getPaymentsDao();
            eventsDao = databaseHelper.getEventsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.initViews(thisFragment);

        return thisFragment;
    }

    private void initViews(final View thisFragment) {

        textAttendeeSelected.setText("");

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
            Log.d("attEvPayFragment: ", "Name: " + attendee.getName() + " ,Lastname: " + attendee.getLastName());
            attendeesArrayList.add(attendee.getAlias());
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
                textAttendeeSelected.setText(name);
                textViewAmount.setText("150");

                /*Snackbar snackbar = Snackbar.make(getView(), "Alumno: " + name + " clickeado!",
                        Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.GREEN);
                snackbar.show();*/
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
        /*FloatingActionButton fabMicrophone = (FloatingActionButton) thisFragment.findViewById(R.id.fab_microphone);
        fabMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                //Intent intent = new Intent(MainActivity.this, NewMessageActivity.class);
                //startActivity(intent);
                promptSpeechInput();
            }
        });*/

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

    @Optional
    @OnClick(R.id.fabSavePayment)
    public void onClickFabSavePayment() {
        if(textAttendeeSelected.getText().toString().equals("")) {
            Log.d("attEvPayFragment: ", "No se eligió alumno de la lista.");
            // TODO: 08/10/17 achicar la forma de mostrar un snakbar.
            Snackbar snackbar = Snackbar.make(getView(), "Por favor elija un alumno primero!",
                    Snackbar.LENGTH_LONG);
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        } else {
            try {
                Attendee attendee = attendeesDao.queryForEq("alias", textAttendeeSelected.getText().toString()).get(0);
                Event event = eventsDao.queryForId(bundle.getInt("eventId"));
                Payment payment = new Payment();
                payment.setAmount(150.00);
                paymentsDao.create(payment);

                AttendeeEventPayment attendeeEventPayment = new AttendeeEventPayment();
                attendeeEventPayment.setAttendee(attendee);
                attendeeEventPayment.setEvent(event);
                attendeeEventPayment.setPayment(payment);

                Map matchingFields = new HashMap();
                matchingFields.put("idEvent", event);
                matchingFields.put("dni", attendee);

                if(attendeeEventPaymentDao.queryForFieldValues(matchingFields).size() == 0) {

                    attendeeEventPaymentDao.create(attendeeEventPayment);
                    Log.d("attEvPayFragment: ", "Se guardo correctamente el pago: " + event.getName() + " - "+ attendee.getAlias());
                    Snackbar snackbar = Snackbar.make(getView(), "Se guardo correctamente el pago!",
                            Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.GREEN);
                    snackbar.show();

                } else {

                    Log.d("attEvPayFragment: ", "Ya se registró el pago de: " + textAttendeeSelected.getText().toString() + " " +
                                    event.getName());

                    Snackbar snackbar = Snackbar.make(getView(), "Ya se registró el pago de: " +
                                    textAttendeeSelected.getText().toString() + ". " +
                            "Por favor elija otro alumno",
                            Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }

            } catch (SQLException e) {
                Log.d("attEvPayFragment: ", "No se pudo guardar el pago!");

                Snackbar snackbar = Snackbar.make(getView(), "No se pudo guardar el pago!",
                        Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                snackbar.show();
                e.printStackTrace();
            }
        }
        textAttendeeSelected.setText("");
        textViewAmount.setText("");
    }
}
