package com.madrefoca.alumnostango.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.adapters.EventsDataAdapter;
import com.madrefoca.alumnostango.adapters.PickerAdapter;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Event;
import com.madrefoca.alumnostango.model.EventType;
import com.madrefoca.alumnostango.utils.ManageFragmentsNavigation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    private ArrayList<Event> eventsList =  new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private EventsDataAdapter eventsListAdapter;
    private AlertDialog.Builder addEditEventDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Paint p = new Paint();
    private ArrayAdapter<String> dataAdapter;


    @Nullable
    @BindView(R.id.fabAddPaymentsEvent)
    FloatingActionButton fabAddPaymentsEvent;

    @Nullable
    @BindView(R.id.eventsPaymentsRecyclerView)
    RecyclerView eventsPaymentsRecyclerView;

    @Nullable
    @BindView(R.id.dialog_event_id)
    EditText eventId;

    @Nullable
    @BindView(R.id.dialog_event_name)
    EditText eventName;

    //daos
    Dao<EventType, Integer> eventTypeDao;
    Dao<Event, Integer> eventDao;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragment = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            eventTypeDao = databaseHelper.getEventTypesDao();
            eventDao = databaseHelper.getEventsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //databaseHelper.clearTables();

        //Insert some students
        //DatabasePopulatorUtil databasePopulatorUtil = new DatabasePopulatorUtil(databaseHelper);
        //databasePopulatorUtil.populate();

        this.initViews(thisFragment);

        this.populateEventsList();
        this.initSwipe();
        this.initDialog(thisFragment, inflater);

        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initViews(View thisFragment) {
        eventsPaymentsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        eventsPaymentsRecyclerView.setLayoutManager(layoutManager);
        eventsListAdapter = new EventsDataAdapter(eventsList);
        eventsPaymentsRecyclerView.setAdapter(eventsListAdapter);
    }

    private void populateEventsList() {
        Log.d("EventFragment: ", "put the Events in the view...");
        eventsList.addAll(getAllEventFromDatabase());
        eventsListAdapter.notifyDataSetChanged();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    try {
                        eventDao.deleteById(eventsList.get(position).getIdEvent());
                        Log.d("EventFragment: ", "Event: " + eventsList.get(position).getName() +" "+
                                eventsList.get(position).getName() + ", with id: " +
                                eventsList.get(position).getIdEvent() + " was deleted frm database.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    eventsListAdapter.removeItem(position);
                } else {
                    removeView();
                    edit_position = position;
                    addEditEventDialog.setTitle("Editar clase");
                    eventId.setText(eventsList.get(position).getIdEvent().toString());
                    eventName.setText(eventsList.get(position).getName());

                    addEditEventDialog.show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() +
                                width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);

                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(),
                                (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,
                                (float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(eventsPaymentsRecyclerView);
    }

    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void initDialog(View thisFragment, LayoutInflater inflater) {
        addEditEventDialog = new AlertDialog.Builder(thisFragment.getContext());
        view = inflater.inflate(R.layout.dialog_events,null);

        ButterKnife.bind(this, view);

        addEditEventDialog.setView(view);

        this.populateEventTypesSpinner(view);

        addEditEventDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventType eventType = new EventType();
                // TODO: 16/09/17 agregar una validacion por si tratan de crear un alumno y no tienen cargados los tipos de alumnos.

                Event event = null;

                try {
                    if(add){
                        add =false;

                        //getting data from dialog
                        event = new Event();
                        event.setName(eventName.getText().toString());
                        event.setEventType(eventType);

                        eventDao.create(event);
                        Log.d("EventFragment: ", "Saved event: " + event.getName());
                    }else{
                        event = eventDao.queryForId(Integer.parseInt(eventId.getText().toString()));
                        event.setName(eventName.getText().toString());
                        event.setEventType(eventType);

                        eventDao.update(event);
                        Log.d("EventFragment: ", "Updated event: " + event.getName());
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                eventsList.clear();
                eventsList.addAll(getAllEventFromDatabase());
                eventsListAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
    }

    private void populateEventTypesSpinner(View view) {
        //get events types to put into spinner
        ArrayList<String> list = getEventTypesFromDatabase();

        dataAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //eventTypesSpinner.setAdapter(dataAdapter);
    }

    private ArrayList<String> getEventTypesFromDatabase() {
        List<EventType> eventsTpesList = null;
        try {
            eventsTpesList = eventTypeDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> list = new ArrayList<String>();
        for(EventType eventType : eventsTpesList){
            list.add(eventType.getName());
        }
        return list;
    }

    private List<Event> getAllEventFromDatabase() {
        // Reading all Event
        Log.d("EventFragment: ", "Reading all events from database...");
        List<Event> eventsList = null;
        try {
            // This is how, a reference of DAO object can be done
            eventsList = databaseHelper.getEventsDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventsList;
    }

    @Optional
    @OnClick(R.id.fabAddPaymentsEvent)
    public void onClickAddNewEvent() {
        // TODO: 25/09/17  https://github.com/wdullaer/MaterialDateTimePicker
        // TODO: 25/09/17  https://android--examples.blogspot.com.ar/2015/05/how-to-use-datepickerdialog-in-android.html
        removeView();

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                HomeFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setThemeDark(true);
        dpd.vibrate(true);
        dpd.dismissOnPause(true);
        dpd.showYearPickerFirst(true);
        dpd.setVersion(true ? DatePickerDialog.Version.VERSION_2 : DatePickerDialog.Version.VERSION_1);
        dpd.setAccentColor(Color.parseColor("#9C27B0"));
        dpd.setTitle("Fecha de la clase");

        if (true) {
            Calendar date1 = Calendar.getInstance();
            Calendar date2 = Calendar.getInstance();
            date2.add(Calendar.WEEK_OF_MONTH, -1);
            Calendar date3 = Calendar.getInstance();
            date3.add(Calendar.WEEK_OF_MONTH, 1);
            Calendar[] days = {date1, date2, date3};
            dpd.setHighlightedDays(days);
        }

        if (false) {
            Calendar[] days = new Calendar[13];
            for (int i = -6; i < 7; i++) {
                Calendar day = Calendar.getInstance();
                day.add(Calendar.DAY_OF_MONTH, i * 2);
                days[i + 6] = day;
            }
            dpd.setSelectableDays(days);
        }
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Optional
    @OnClick(R.id.fabPaymentsEventTypes)
    public void onClickDisplayEventTypesFragment() {
        ManageFragmentsNavigation.navItemIndex = 9;
        ManageFragmentsNavigation.CURRENT_TAG = ManageFragmentsNavigation.TAG_EVENT_TYPES;

        // update the main content by replacing fragments
        Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.CURRENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        //dateTextView.setText(date);
    }

}
