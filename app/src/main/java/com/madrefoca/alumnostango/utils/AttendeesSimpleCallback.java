package com.madrefoca.alumnostango.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.adapters.AttendeesDataAdapter;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.AttendeeType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by fernando on 16/10/17.
 */

public class AttendeesSimpleCallback extends ItemTouchHelper.SimpleCallback {

    @Nullable
    @BindView(R.id.dialog_attendee_id)
    EditText attendeeId;

    @Nullable
    @BindView(R.id.dialog_attendee_name)
    EditText attendeeName;

    @Nullable
    @BindView(R.id.dialog_attendee_lastname)
    EditText attendeeLastName;

    @Nullable
    @BindView(R.id.dialog_attendee_age)
    EditText attendeeAge;

    @Nullable
    @BindView(R.id.dialog_attendee_phone)
    EditText attendeePhone;

    @Nullable
    @BindView(R.id.dialog_attendee_facebook)
    EditText attendeeFacebook;

    @Nullable
    @BindView(R.id.dialog_attendee_email)
    EditText attendeeEmail;

    @Nullable
    @BindView(R.id.dialog_attendee_type_spinner)
    Spinner attendeeTypesSpinner;

    @Nullable
    @BindView(R.id.fabAddAttendee)
    FloatingActionButton fabAddAttendee;


    private ArrayList<Attendee> attendeesList =  new ArrayList<>();
    private AttendeesDataAdapter attendeesListAdapter;
    private Context context;
    private int position;
    private boolean add = false;
    private Paint p = new Paint();
    private View thisFragment;
    private AlertDialog.Builder addEditAttendeeDialog;
    private ArrayAdapter<String> dataAdapter;
    private DatabaseHelper databaseHelper;

    //daos
    Dao<AttendeeType, Integer> attendeeTypeDao;
    Dao<Attendee, Integer> attendeeDao;

    public AttendeesSimpleCallback(int dragDirs, int swipeDirs, Context context,
                                   ArrayList<Attendee> attendeesList, AttendeesDataAdapter attendeesListAdapter,
                                   View thisFragment) {
        super(dragDirs, swipeDirs);

        this.context = context;
        this.attendeesList = attendeesList;
        this.attendeesListAdapter = attendeesListAdapter;
        this.thisFragment = thisFragment;
        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(), DatabaseHelper.class);

        try {
            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
            attendeeDao = databaseHelper.getAttendeeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ButterKnife.bind(this, thisFragment);

        this.initDialog();
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.LEFT){
            this.manageDelete();
        } else {
            this.manageEdit();
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
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_edit_white);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() +
                        width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);

                c.drawBitmap(icon,null,icon_dest,p);
            } else {
                p.setColor(Color.parseColor("#D32F2F"));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(),
                        (float) itemView.getBottom());
                c.drawRect(background,p);
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete_white);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,
                        (float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                c.drawBitmap(icon,null,icon_dest,p);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void manageDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Borrar alumno?");
        builder.setMessage("Esta por borrar un alumno de la base de datos! Desea continuar?");
        builder.setCancelable(false);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    //No se borra se pasa a un estado de inactivo para no romper los pagos en clases
                    Attendee attendee = attendeeDao.queryForId(attendeesList.get(position).getAttendeeId());
                    attendee.setState("inactive");
                    attendeeDao.update(attendee);
                    Log.d("AttendeeFragment: ", "Attendee: " + attendeesList.get(position).getName() +" "+
                            attendeesList.get(position).getLastName() + ", with id: " +
                            attendeesList.get(position).getAttendeeId() + " was deleted frm database.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                attendeesListAdapter.removeItem(position);
                Toast.makeText(context, "Se borro el alumno de la base de datos!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "No se borro el alumno de la base de datos!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void manageEdit() {
        removeView();
        addEditAttendeeDialog.setTitle("Editar alumno");
        attendeeId.setText(attendeesList.get(position).getAttendeeId().toString());
        attendeeName.setText(attendeesList.get(position).getName());
        attendeeLastName.setText(attendeesList.get(position).getLastName());
        if(attendeesList.get(position).getAge() != null)
            attendeeAge.setText(attendeesList.get(position).getAge().toString());
        if(attendeesList.get(position).getCellphoneNumber() != null)
            attendeePhone.setText(attendeesList.get(position).getCellphoneNumber().toString());
        attendeeFacebook.setText(attendeesList.get(position).getFacebookProfile());
        attendeeEmail.setText(attendeesList.get(position).getEmail());
        attendeeTypesSpinner.setSelection(dataAdapter.getPosition(attendeesList.get(position).getAttendeeType().getName()));

        addEditAttendeeDialog.show();
    }

    private void removeView(){
        if(thisFragment.getParent()!=null) {
            ((ViewGroup) thisFragment.getParent()).removeView(thisFragment);
        }
    }

    private void initDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_attendees, null);
        addEditAttendeeDialog = new AlertDialog.Builder(view.getContext());
        ButterKnife.bind(this, view);

        addEditAttendeeDialog.setView(view);

        this.populateAttendeeTypesSpinner(view);

        addEditAttendeeDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AttendeeType attendeeType = new AttendeeType();
                // TODO: 16/09/17 agregar una validacion por si tratan de crear un alumno y no tienen cargados los tipos de alumnos.
                String attendeTypeSelected = attendeeTypesSpinner.getSelectedItem().toString();
                try {
                    attendeeType = attendeeTypeDao.queryForEq("name", attendeTypeSelected).get(0);
                } catch (SQLException e) {
                    Log.d("AttendeeFragment: ", "Cannot find attendee type: " + attendeTypeSelected + "in database");
                    e.printStackTrace();
                }

                Attendee attendee = null;

                try {
                    if(add){
                        add =false;

                        //getting data from dialog
                        attendee = new Attendee();
                        attendee.setName(attendeeName.getText().toString());
                        attendee.setLastName(attendeeLastName.getText().toString());
                        attendee.setAlias();
                        attendee.setAge(Integer.parseInt(attendeeAge.getText().toString()));
                        attendee.setCellphoneNumber(Integer.parseInt(attendeePhone.getText().toString()));
                        attendee.setFacebookProfile(attendeeFacebook.getText().toString());
                        attendee.setEmail(attendeeEmail.getText().toString());
                        attendee.setAttendeeType(attendeeType);
                        attendee.setState("active");

                        attendeeDao.create(attendee);
                        Log.d("AttendeeFragment: ", "Saved attendee: " + attendee.getName() +" "+ attendee.getLastName());
                    }else{
                        attendee = attendeeDao.queryForId(Integer.parseInt(attendeeId.getText().toString()));
                        attendee.setName(attendeeName.getText().toString());
                        attendee.setLastName(attendeeLastName.getText().toString());
                        attendee.setAlias();
                        attendee.setAge(Integer.parseInt(attendeeAge.getText().toString()));
                        attendee.setCellphoneNumber(Integer.parseInt(attendeePhone.getText().toString()));
                        attendee.setFacebookProfile(attendeeFacebook.getText().toString());
                        attendee.setEmail(attendeeEmail.getText().toString());
                        attendee.setAttendeeType(attendeeType);

                        attendeeDao.update(attendee);
                        Log.d("AttendeeFragment: ", "Updated attendee: " + attendee.getName() +" "+ attendee.getLastName());
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                attendeesList.clear();
                attendeesList.addAll(getAllAttendeeFromDatabase());
                attendeesListAdapter.notifyDataSetChanged();
                dialog.dismiss();

                //return to the origin fragment
                ManageFragmentsNavigation.navItemIndex = 1;
                ManageFragmentsNavigation.CURRENT_TAG = ManageFragmentsNavigation.TAG_ATTENDEES;
                Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
                FragmentTransaction fragmentTransaction = ((Activity) context).getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();

            }
        });
    }

    private void populateAttendeeTypesSpinner(View view) {
        //get attendees types to put into spinner
        ArrayList<String> list = getAttendeeTypesFromDatabase();

        dataAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attendeeTypesSpinner.setAdapter(dataAdapter);
    }

    private ArrayList<String> getAttendeeTypesFromDatabase() {
        List<AttendeeType> attendeesTypesList = null;
        try {
            attendeesTypesList = attendeeTypeDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> list = new ArrayList<String>();
        for(AttendeeType attendeeType : attendeesTypesList){
            list.add(attendeeType.getName());
        }
        return list;
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
    @OnClick(R.id.fabAddAttendee)
    public void onClickAddNewAttendee() {
        removeView();
        add = true;
        addEditAttendeeDialog.setTitle("Nuevo alumno");
        attendeeName.setText("");
        attendeeLastName.setText("");
        attendeeAge.setText("");
        attendeePhone.setText("");
        attendeeFacebook.setText("");
        attendeeEmail.setText("");
        attendeeTypesSpinner.setSelection(0);
        addEditAttendeeDialog.show();
    }
}
