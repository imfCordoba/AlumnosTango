package com.madrefoca.alumnostango.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.madrefoca.alumnostango.R;
import com.madrefoca.alumnostango.helpers.DatabaseHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        progressBar.setMax(10);

        databaseHelper = OpenHelperManager.getHelper(this.getApplicationContext(),DatabaseHelper.class);

        this.signIn();

        // Use the last signed in account here since it already have a Drive scope.
        mDriveClient = Drive.getDriveClient(this.getApplicationContext(), GoogleSignIn.getLastSignedInAccount(this.getApplicationContext()));
        // Build a drive resource client.
        mDriveResourceClient =
                Drive.getDriveResourceClient(this.getApplicationContext(), GoogleSignIn.getLastSignedInAccount(this.getApplicationContext()));
    }
}
