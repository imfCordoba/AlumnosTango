package com.madrefoca.alumnostango.fragments;


import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.utils.UtilImportContacts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

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

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Integer count =1;

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

        int hasWritePermission = ActivityCompat.checkSelfPermission(thisFragment.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }

        ButterKnife.bind(this, thisFragment);

        progressBar.setMax(10);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        return thisFragment;
    }

    @Optional
    @OnClick(R.id.exportDbButton)
    public void onClickExportDatabase() {
        //save database
        //path: /data/data/com.madrefoca.alumnostango/databases/AlumnosTango.db

        //create folder in internal storage if does not exist.
        createJsonFolder();

        Log.d("Database path: ",databaseHelper.getReadableDatabase().getPath());
    }

    private void createJsonFolder() {
        String path = Environment.getDataDirectory().getAbsolutePath().toString() +
                "/com.madrefoca.alumnostango/jsonFolder";
        File file = new File(thisFragment.getContext().getFilesDir(), "example.json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos;
        DataOutputStream dos;
        String s = "";
        try {
            File f = thisFragment.getContext().getFilesDir();
            s = f.getCanonicalPath();
            File file2= new File(s + "/archivo3.txt");

            if(file.exists()){
                file.delete();
            }

            if(file2.exists()){
                file2.delete();
            }
            file2.createNewFile();
            fos = new FileOutputStream(file2);
            dos = new DataOutputStream(fos);
            dos.write("asdfadf".getBytes());
            dos.writeChars("\n");
            dos.write("ddddddddddddddd".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("app path: ",s);

    }

    @Optional
    @OnClick(R.id.importContactsButton)
    public void onClickImportContactsButton() {
        count =1;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        UtilImportContacts utilImportContacts = new UtilImportContacts(thisFragment.getContext(), progressBar);
        utilImportContacts.execute(481);

    }

}
