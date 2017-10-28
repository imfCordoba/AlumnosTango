package com.madrefoca.alumnostango.fragments;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.adapters.EventAccountsAdapter;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeEventPayment;
import com.madrefoca.alumnostango.model.Event;
import com.madrefoca.alumnostango.model.Payment;
import com.madrefoca.alumnostango.utils.AttendeePaymentRow;
import com.madrefoca.alumnostango.utils.AttendeesSimpleCallback;
import com.madrefoca.alumnostango.utils.EventAccountsSimpleCallback;
import com.madrefoca.alumnostango.utils.ManageFragmentsNavigation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventAccountsFragment extends Fragment {

    private Bundle bundle;

    @Nullable
    @BindView(R.id.fabAddPaymentView)
    FloatingActionButton fabAddPaymentView;

    @Nullable
    @BindView(R.id.event_accounts_recyclerView)
    RecyclerView eventAccountsRecyclerView;

    @Nullable
    @BindView(R.id.payment_input_Search)
    EditText paymentInputSearch;

    @Nullable
    @BindView(R.id.total_cash)
    EditText totalCash;

    EventAccountsAdapter eventAccountsAdapter;


    private DatabaseHelper databaseHelper = null;

    private Dao<Attendee, Integer> attendeesDao;
    private Dao<AttendeeEventPayment, Integer> attendeeEventPaymentDao;
    private Dao<Payment, Integer> paymentsDao;
    private Dao<Event, Integer> eventsDao;

    ArrayList<AttendeePaymentRow> attendeePaymentRowArrayList = new ArrayList<>();


    public EventAccountsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragment = inflater.inflate(R.layout.fragment_event_accounts, container, false);

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

        this.initView(thisFragment);
        this.initSwipe(thisFragment);

        return thisFragment;
    }

    private void initView(View thisFragment) {

        try {
            List<AttendeeEventPayment> attendeeEventPaymentArrayList = attendeeEventPaymentDao.queryForEq("idEvent", bundle.getInt("eventId"));
            for(AttendeeEventPayment attendeeEventPayment : attendeeEventPaymentArrayList){
                AttendeePaymentRow attendeePaymentRow = new AttendeePaymentRow();
                attendeePaymentRow.setAttendeeEventPayment(attendeeEventPayment);
                attendeePaymentRow.setAttendee(attendeesDao.queryForId(attendeeEventPayment.getAttendee().getAttendeeId()));
                attendeePaymentRow.setPayment(paymentsDao.queryForId(attendeeEventPayment.getPayment().getIdPayment()));

                attendeePaymentRowArrayList.add(attendeePaymentRow);

                Log.d("EventAccountFrag: ", "---->Attendee: " +
                        attendeesDao.queryForId(attendeeEventPayment.getAttendee().getAttendeeId()).getAlias() +
                        " Id: " + attendeeEventPayment.getAttendee().getAttendeeId() +
                        " Pago: " + paymentsDao.queryForId(attendeeEventPayment.getPayment().getIdPayment()).getAmount());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        eventAccountsRecyclerView.setHasFixedSize(true);
        eventAccountsRecyclerView.setLayoutManager(new LinearLayoutManager(thisFragment.getContext()));
        eventAccountsAdapter = new EventAccountsAdapter(attendeePaymentRowArrayList);
        eventAccountsRecyclerView.setAdapter(eventAccountsAdapter);

        this.calculateTotalCash();
    }

    private void initSwipe(View thisFragment) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new EventAccountsSimpleCallback(0,
                ItemTouchHelper.LEFT, thisFragment.getContext(), attendeePaymentRowArrayList,
                eventAccountsAdapter, thisFragment);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(eventAccountsRecyclerView);
    }

    private void calculateTotalCash() {
        Double partialTotalCash = 0.0;
        for(AttendeePaymentRow attendeePaymentRow : attendeePaymentRowArrayList) {
            partialTotalCash+= attendeePaymentRow.getPayment().getAmount();
        }
        totalCash.setText(partialTotalCash.toString());
    }

    @Optional
    @OnClick(R.id.fabAddPaymentView)
    public void onClickFabAddPaymentView() {
        ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_ATTENDEE_EVENT_PAYMENT);
        // update the main content by replacing fragments
        Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.navItemTag);
        fragmentTransaction.commitAllowingStateLoss();

    }

    @OnTextChanged(value = R.id.payment_input_Search,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterPaymentInputSearch(Editable editable) {
        //after the change calling the method and passing the search input
        filter(editable.toString());
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<AttendeePaymentRow> filterdAttendeePaymentRow = new ArrayList<>();

        if(text.isEmpty() || text == "") {
            filterdAttendeePaymentRow.addAll(attendeePaymentRowArrayList);
        } else {
            //looping through existing elements
            for (AttendeePaymentRow attendeePaymentRow : attendeePaymentRowArrayList) {
                //if the existing elements contains the search input
                if (attendeePaymentRow.getAttendee().getAlias().contains(text)) {
                    //adding the element to filtered list
                    filterdAttendeePaymentRow.add(attendeePaymentRow);
                }
            }
        }

        //calling a method of the adapter class and passing the filtered list
        eventAccountsAdapter.filterList(filterdAttendeePaymentRow);
    }
}
