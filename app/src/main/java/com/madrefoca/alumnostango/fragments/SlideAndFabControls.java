package com.madrefoca.alumnostango.fragments;

import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.madrefoca.alumnostango.helpers.DatabaseHelper;

/**
 * Created by fernando on 17/09/17.
 */

public class SlideAndFabControls extends Fragment{
    private DatabaseHelper databaseHelper = null;
    private View view;
    private AlertDialog.Builder addEditAttendeeTypeDialog;
    private boolean add = false;
    private int edit_position;
    private Paint p = new Paint();

    public SlideAndFabControls() {

    }
}
