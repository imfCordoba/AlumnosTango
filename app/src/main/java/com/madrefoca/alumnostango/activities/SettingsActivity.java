package com.madrefoca.alumnostango.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.common.api.Scope;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;
import com.madrefoca.alumnostango.model.Attendee;
import com.madrefoca.alumnostango.model.Event;
import com.madrefoca.alumnostango.utils.JsonUtil;
import com.madrefoca.alumnostango.utils.UtilImportContacts;
import com.madrefoca.alumnostango.utils.UtilImportEvents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Nullable
    @BindView(R.id.contact_filter_word)
    EditText contactFilterWord;

    private DatabaseHelper databaseHelper = null;

    private static final String TAG = "Settings activity";
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    protected static final int REQUEST_CODE_OPEN_ITEM = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final String FROM_PHONE = "phone";
    private static final String FROM_GOOGLE_DRIVE = "drive";

    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private Dao<Attendee, Integer> attendeeDao;
    private Dao<Event, Integer> eventsDao;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private AlertDialog.Builder filterContactsDialog;
    private View view;
    private String workingTable;
    private String fileName;

    @Override
    protected void onStart() {
        super.onStart();
        signIn();

        int hasReadContactsPermission = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this, this);

        databaseHelper = OpenHelperManager.getHelper(this.getApplicationContext(),DatabaseHelper.class);

        try {
            attendeeDao = databaseHelper.getAttendeeDao();
            eventsDao = databaseHelper.getEventsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.initDialog();
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    Log.e(TAG, "Sign-in failed.");
                    Toast.makeText(getApplicationContext(),
                            "Sign-in failed.",
                            Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    Log.e(TAG, "Sign-in failed.");
                    Log.e(TAG, "Sign-in failed.");
                    Toast.makeText(getApplicationContext(),
                            "Sign-in failed.",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    mOpenItemTaskSource.setResult(driveId);
                } else {
                    Log.e(TAG, "Unable to open file.");
                    Log.e(TAG, "Sign-in failed.");
                    Toast.makeText(getApplicationContext(),
                            "Sign-in failed.",
                            Toast.LENGTH_LONG).show();
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Starts the sign-in process and initializes the Drive client.
     */
    protected void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    /**
     * Continues the sign-in process, initializing the Drive clients with the current
     * user's account.
     */
    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
    }

    protected void onDriveClientReady() {
        pickJsonFile()
                .addOnSuccessListener(this,
                        new OnSuccessListener<DriveId>() {
                            @Override
                            public void onSuccess(DriveId driveId) {
                                retrieveContents(driveId.asDriveFile());
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "No file selected", e);
                        showMessage(getString(R.string.file_not_selected));
                        finish();
                    }
                });
    }

    private void retrieveContents(DriveFile file) {
        // [START open_file]
        Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        // [END open_file]
        // [START read_contents]
        openFileTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents contents = task.getResult();
                        // Process contents...
                        // [START_EXCLUDE]
                        // [START read_as_string]
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(contents.getInputStream()))) {
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }

                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setProgress(0);

                            switch (workingTable) {
                                case "attendees":
                                    UtilImportContacts utilImportContacts = new UtilImportContacts(getApplicationContext(), progressBar, builder.toString());
                                    utilImportContacts.execute(FROM_GOOGLE_DRIVE);
                                    break;
                                case "event":
                                    UtilImportEvents utilImportEvents = new UtilImportEvents(getApplicationContext(), progressBar, builder.toString());
                                    break;
                                case "all":
                                    break;
                            }

                            showMessage(getString(R.string.content_loaded));
                            //mFileContents.setText(builder.toString());
                            Log.i("file loaded ---->", builder.toString());
                        }
                        // [END read_as_string]
                        // [END_EXCLUDE]
                        // [START discard_contents]
                        Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
                        // [END discard_contents]
                        return discardTask;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        // [START_EXCLUDE]
                        Log.e(TAG, "Unable to read contents", e);
                        showMessage(getString(R.string.read_failed));
                        finish();
                        // [END_EXCLUDE]
                    }
                });
        // [END read_contents]
    }

    /**
     * Prompts the user to select a text file using OpenFileActivity.
     *
     * @return Task that resolves with the selected item's ID.
     */
    protected Task<DriveId> pickJsonFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "application/json"))
                        .setActivityTitle(getString(R.string.select_file))
                        .build();
        return pickItem(openOptions);
    }

    /**
     * Prompts the user to select a folder using OpenFileActivity.
     *
     * @param openOptions Filter that should be applied to the selection
     * @return Task that resolves with the selected item's ID.
     */
    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        mOpenItemTaskSource = new TaskCompletionSource<>();
        getDriveClient()
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith(new Continuation<IntentSender, Void>() {
                    @Override
                    public Void then(@NonNull Task<IntentSender> task) throws Exception {
                        startIntentSenderForResult(
                                task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                        return null;
                    }
                });
        return mOpenItemTaskSource.getTask();
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
        // Get an output stream for the contents.
        OutputStream outputStream = driveContents.getOutputStream();
        String jsonFile = prepareJsonToSave(workingTable);

        Log.i(TAG, "Json content from " + workingTable + ": " + jsonFile);
        try {
            outputStream.write(jsonFile.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Unable to write file contents.", e);
        }

        // Create the initial metadata - MIME type and title.
        // Note that the user will be able to change the title later.
        MetadataChangeSet metadataChangeSet =
                new MetadataChangeSet.Builder()
                        .setMimeType("application/json")
                        .setTitle(fileName + ".json")
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
                                startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR,
                                        null, 0, 0, 0);
                                return null;
                            }
                        });
    }

    private String prepareJsonToSave(String tableToExport) {
        String jsonString;
        switch (tableToExport) {
            case "attendees":
                List<Attendee> attendees = null;
                try {
                    attendees = attendeeDao.queryForAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                jsonString = JsonUtil.attendeesToJSon(attendees);
                break;
            case "events":
                List<Event> events = null;
                try {
                    events = eventsDao.queryForAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                jsonString = JsonUtil.eventsToJSon(events);
                break;
            default:
                jsonString = "";
                break;
        }
        return jsonString;
    }

    private void initDialog()  {
        filterContactsDialog = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_contacts_filter, null);
        contactFilterWord = (EditText)view.findViewById(R.id.contact_filter_word);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        filterContactsDialog.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.filter_import_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(0);

                        UtilImportContacts utilImportContacts = new UtilImportContacts(getApplicationContext(), progressBar);
                        utilImportContacts.setFilterWord(contactFilterWord.getText().toString());
                        utilImportContacts.execute(FROM_PHONE);
                    }
                });

    }

    @Optional
    @OnClick(R.id.exportDbButton)
    public void onClickExportDatabase() {
        //save database
        //path: /data/data/com.madrefoca.alumnostango/databases/AlumnosTango.db
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        this.initializeDriveClient(signInAccount);

        //create folder in internal storage if does not exist.
        //createJsonFolder();
        workingTable = "attendees";
        fileName = getString(R.string.file_name_attendees);
        saveFileToDrive();

        Log.d("Database path: ",databaseHelper.getReadableDatabase().getPath());
    }

    @Optional
    @OnClick(R.id.exportEventsButton)
    public void onClickExportEventsButton() {

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        this.initializeDriveClient(signInAccount);
        workingTable = "events";
        fileName = getString(R.string.file_name_events);
        saveFileToDrive();

        Log.d("Database path: ",databaseHelper.getReadableDatabase().getPath());
    }

    @Optional
    @OnClick(R.id.importContactsButton)
    public void onClickImportContactsButton() {
        this.removeView();
        filterContactsDialog.show();
    }

    @Optional
    @OnClick(R.id.importDbButton)
    public void onClickImportDbButton() {
        onDriveClientReady();
    }

    /**
     * Shows a toast message.
     */
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    protected DriveClient getDriveClient() {
        return mDriveClient;
    }

    protected DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }
}
