package com.madrefoca.alumnostango.fragments;


import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.utils.UtilImportContacts;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Nullable
    @BindView(R.id.exportDbButton)
    Button exportDbButton;

    @Nullable
    @BindView(R.id.importContactsButton)
    Button importContactsButton;

    private DatabaseHelper databaseHelper = null;

    View thisFragment = null;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragment = inflater.inflate(R.layout.fragment_settings, container, false);

        int hasReadContactsPermission = ActivityCompat.checkSelfPermission(thisFragment.getContext(),
                Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        return thisFragment;
    }

    @Optional
    @OnClick(R.id.exportDbButton)
    public void onClickExportDatabase() {
        //save database
        //path: /data/data/com.madrefoca.alumnostango/databases/AlumnosTango.db

        Log.d("Database path: ",databaseHelper.getReadableDatabase().getPath());
    }

    @Optional
    @OnClick(R.id.importContactsButton)
    public void onClickImportContactsButton() {
        UtilImportContacts utilImportContacts = new UtilImportContacts(thisFragment.getContext());
        String sleepTime = "0";
        utilImportContacts.execute(sleepTime);
        //utilImportContacts.importAllContactsFromPhone(thisFragment.getContext());

    }

}
