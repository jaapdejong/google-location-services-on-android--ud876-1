package com.example.jdj.activityrecognition;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private Button requestUpdatesButton;
    private Button removeUpdatesButton;
    private TextView statusText;
    private ActivityDetectionBroadcastReceiver activityDetectionBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestUpdatesButton = (Button) findViewById(R.id.request_activity_updates_button);
        removeUpdatesButton = (Button) findViewById(R.id.remove_activity_updates_button);
        statusText = (TextView) findViewById(R.id.detectedActivities);

        activityDetectionBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        buildGoogleApiClient();
    }

    public void requestActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
        requestUpdatesButton.setEnabled(false);
        removeUpdatesButton.setEnabled(true);
    }

    public void removeActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
        requestUpdatesButton.setEnabled(true);
        removeUpdatesButton.setEnabled(false);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
        LocalBroadcastManager.getInstance(this).registerReceiver(activityDetectionBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        // Unregister the broadcast receiver that was registered during onResume().
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityDetectionBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.e(TAG, "Successfully added activity detection.");

        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    private class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        private static final String TAG = "ActivityDetectionBroadcastReceiver";

        /**
         * Returns a human readable String corresponding to a detected activity type.
         */
        public String getActivityString(Resources resources, int detectedActivityType) {
            switch (detectedActivityType) {
                case DetectedActivity.IN_VEHICLE:
                    return resources.getString(R.string.in_vehicle);
                case DetectedActivity.ON_BICYCLE:
                    return resources.getString(R.string.on_bicycle);
                case DetectedActivity.ON_FOOT:
                    return resources.getString(R.string.on_foot);
                case DetectedActivity.RUNNING:
                    return resources.getString(R.string.running);
                case DetectedActivity.STILL:
                    return resources.getString(R.string.still);
                case DetectedActivity.TILTING:
                    return resources.getString(R.string.tilting);
                case DetectedActivity.UNKNOWN:
                    return resources.getString(R.string.unknown);
                case DetectedActivity.WALKING:
                    return resources.getString(R.string.walking);
                default:
                    return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Resources resources = context.getResources();
            ArrayList<DetectedActivity> detectedActivities = intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            String status = "";
            for (DetectedActivity detectedActivity : detectedActivities) {
                status += getActivityString(resources, detectedActivity.getType()) + " " + detectedActivity.getConfidence() + "%\n";
            }
            statusText.setText(status);
        }
    }
}
