package com.madrefoca.alumnostango.activities;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.Payment;
import com.madrefoca.alumnostango.utils.JsonUtil;
import com.madrefoca.alumnostango.utils.UtilImportContacts;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class SettingsActivity extends AppCompatActivity {

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

    private static final String TAG = "alumnos tango";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_CREATOR = 2;
    private GoogleSignInClient mGoogleSignInClient;
    private Bitmap mBitmapToSave;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    private Dao<Attendee, Integer> attendeeDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        int hasReadContactsPermission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }

        int hasWritePermission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }

        ButterKnife.bind(this, this);

        databaseHelper = OpenHelperManager.getHelper(this.getApplicationContext(),DatabaseHelper.class);

        try {
            attendeeDao = databaseHelper.getAttendeeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.signIn();
    }

    /** Start sign in activity. */
    private void signIn() {
        Log.i(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /** Build a Google SignIn client. */
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(getApplicationContext(), signInOptions);
    }

    /** Create a new file and save it to Drive. */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");

        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                return createFileIntentSender(task.getResult());
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Failed to create new contents.", e);
                            }
                        });
    }

    /**
     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
     * CreateFileActivityOptions} for user to create a new photo in Drive.
     */
    private Task<Void> createFileIntentSender(DriveContents driveContents) {
        Log.i(TAG, "New contents created.");
        // Get an output stream for the contents.
        OutputStream outputStream = driveContents.getOutputStream();
        List<Attendee> attendees = null;
        try {
            attendees = attendeeDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String attendeesJson = JsonUtil.toJSon(attendees);
        Log.i("test", attendeesJson);
        try {
            outputStream.write(attendeesJson.getBytes());
        } catch (IOException e) {
            Log.w(TAG, "Unable to write file contents.", e);
        }

        // Create the initial metadata - MIME type and title.
        // Note that the user will be able to change the title later.
        MetadataChangeSet metadataChangeSet =
                new MetadataChangeSet.Builder()
                        .setMimeType("application/json")
                        .setTitle("attendees.json")
                        .build();
        // Set up options to configure and display the create file activity.
        CreateFileActivityOptions createFileActivityOptions =
                new CreateFileActivityOptions.Builder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContents)
                        .build();

        return mDriveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        new Continuation<IntentSender, Void>() {
                            @Override
                            public Void then(@NonNull Task<IntentSender> task) throws Exception {
                                startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR, null, 0, 0, 0);
                                return null;
                            }
                        });
    }

    private void createJsonFolder() {
        String path = Environment.getDataDirectory().getAbsolutePath().toString() +
                "/com.madrefoca.alumnostango/jsonFolder";
        File file = new File(getApplicationContext().getFilesDir(), "example.json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos;
        DataOutputStream dos;
        String s = "";
        try {
            File f = getApplicationContext().getFilesDir();
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
    @OnClick(R.id.exportDbButton)
    public void onClickExportDatabase() {
        //save database
        //path: /data/data/com.madrefoca.alumnostango/databases/AlumnosTango.db

        // Use the last signed in account here since it already have a Drive scope.
        mDriveClient = Drive.getDriveClient(this.getApplicationContext(), GoogleSignIn.getLastSignedInAccount(this.getApplicationContext()));
        // Build a drive resource client.
        mDriveResourceClient =
                Drive.getDriveResourceClient(this.getApplicationContext(), GoogleSignIn.getLastSignedInAccount(this.getApplicationContext()));

        //create folder in internal storage if does not exist.
        //createJsonFolder();
        saveFileToDrive();

        Log.d("Database path: ",databaseHelper.getReadableDatabase().getPath());
    }

    @Optional
    @OnClick(R.id.importContactsButton)
    public void onClickImportContactsButton() {
        count =1;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        UtilImportContacts utilImportContacts = new UtilImportContacts(getApplicationContext(), progressBar);
        utilImportContacts.execute(481);

    }
}
