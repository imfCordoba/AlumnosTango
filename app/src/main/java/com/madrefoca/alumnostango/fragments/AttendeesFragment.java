package com.madrefoca.alumnostango.fragments;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
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
import android.widget.Spinner;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.adapters.DataAdapter;
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
public class AttendeesFragment extends Fragment implements View.OnClickListener{

    private ArrayList<Attendee> attendeesList =  new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private FloatingActionButton fabAttendeeType;
    private FloatingActionButton fabAddAttendee;
    private DataAdapter attendeesListAdapter;
    private RecyclerView attendeesRecyclerView;
    private AlertDialog.Builder alertDialog;
    private EditText attendeeName;
    private Spinner attendeeTypesSpinner;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Paint p = new Paint();

    //daos
    Dao<AttendeeType, Integer> attendeeTypeDao;


    public AttendeesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_attendees, container, false);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);
        try {
            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //databaseHelper.clearTables();

        //Insert some students
        //DatabasePopulatorUtil databasePopulatorUtil = new DatabasePopulatorUtil(databaseHelper);
        //databasePopulatorUtil.populate();

        this.initViews(thisFragment);

        this.populateAttendeesList(thisFragment);
        this.initSwipe();
        this.initDialog(thisFragment);
        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initViews(View thisFragment) {

        fabAttendeeType = (FloatingActionButton) thisFragment.findViewById(R.id.fabAteendeeTypes);
        fabAddAttendee = (FloatingActionButton) thisFragment.findViewById(R.id.fabAddAttendee);

        fabAttendeeType.setOnClickListener(this);
        fabAddAttendee.setOnClickListener(this);

        attendeesRecyclerView = (RecyclerView) thisFragment.findViewById(R.id.attendeesRecyclerView);
        attendeesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        attendeesRecyclerView.setLayoutManager(layoutManager);
        attendeesListAdapter = new DataAdapter(attendeesList);
        attendeesRecyclerView.setAdapter(attendeesListAdapter);

    }

    private void populateAttendeesList(View thisFragment) {
        Log.d("AttendeeFragment: ", "put the Attendees in the view...");
        attendeesList.addAll(getAllAttendeeFromDatabase());
        attendeesListAdapter.notifyDataSetChanged();
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
                    attendeesListAdapter.removeItem(position);
                } else {
                    removeView();
                    edit_position = position;
                    alertDialog.setTitle("Editar alumno");
                    attendeeName.setText(attendeesList.get(position).getName());
                    alertDialog.show();
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
        itemTouchHelper.attachToRecyclerView(attendeesRecyclerView);
    }

    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void initDialog(View thisFragment){
        alertDialog = new AlertDialog.Builder(thisFragment.getContext());
        view = getLayoutInflater().inflate(R.layout.dialog_layout,null);
        alertDialog.setView(view);
        attendeeName = (EditText)view.findViewById(R.id.dialog_attendee_name);

        this.populateAttendeeTypesSpinner(view);

        alertDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(add){
                    add =false;
                    Attendee attende = new Attendee();
                    // TODO: 09/09/17 traer el verdadero type desde el dialogo
                    AttendeeType attendeeType;

                    try {
                        String attendeType = attendeeTypesSpinner.getSelectedView().toString();
                        attendeeType = attendeeTypeDao.queryForEq("name", attendeType).get(0);

                        attende.setName(attendeeName.getText().toString());
                        attende.setAttendeeType(attendeeType);
                        // TODO: 09/09/17 agregar el apellido desde el dialogo
                        attende.setLastName("popleta");
                        Dao<Attendee, Integer> attendeeDao = databaseHelper.getAttendeeDao();
                        attendeeDao.create(attende);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    attendeesList.clear();
                    attendeesList.addAll(getAllAttendeeFromDatabase());
                    attendeesListAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Attendee attendee = new Attendee();
                    attendee.setName(attendeeName.getText().toString());
                    // TODO: 09/09/17 agregar el apellido desde el dialogo
                    attendee.setLastName("popleta");
                    attendeesList.set(edit_position,attendee);
                    attendeesListAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

            }
        });
    }

    private void populateAttendeeTypesSpinner(View view) {
        attendeeTypesSpinner = (Spinner) view.findViewById(R.id.dialog_attendee_type_spinner);
        //get attendees types to put into spinner

        List<AttendeeType> attendeesTpesList = null;
        try {
            attendeesTpesList = attendeeTypeDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> list = new ArrayList<String>();
        for(AttendeeType attendeeType : attendeesTpesList){
            list.add(attendeeType.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attendeeTypesSpinner.setAdapter(dataAdapter);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabAddAttendee:
                removeView();
                add = true;
                alertDialog.setTitle("Nuevo alumno");
                attendeeName.setText("");
                alertDialog.show();
                break;
            case R.id.fabAteendeeTypes:
                AttendeeTypesFragment attendeeTypesFragment= new AttendeeTypesFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, attendeeTypesFragment, "nav_listAttendeeTypes");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }
}
