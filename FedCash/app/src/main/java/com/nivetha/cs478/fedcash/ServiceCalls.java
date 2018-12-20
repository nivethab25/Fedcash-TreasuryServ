package com.nivetha.cs478.fedcash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class ServiceCalls extends AppCompatActivity implements ServiceRequestsFragment.ListSelectionListener{
    public static ArrayList<String> mRequestList;
    public static ArrayList<String> mResponseList;
    private ServiceResponsesFragment mResponsesFragment;

    private static final String TAG = "ServiceCalls";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");

        // Get the string arrays with the requests and responses
        mRequestList = getIntent().getStringArrayListExtra("serviceCalls");
        mResponseList = getIntent().getStringArrayListExtra("serviceResults");

        setContentView(R.layout.activity_service_calls);

        // Get a reference to the ServiceResponsesFragment
        mResponsesFragment =
                (ServiceResponsesFragment) getFragmentManager().findFragmentById(R.id.responses);
    }

    // Implement ListSelectionListener interface
    // Called by ServiceRequestsFragment when the user selects an item in the ServiceRequestsFragment
    @Override
    public void onListSelection(int index) {
        if (mResponsesFragment.getShownIndex() != index) {

            // Tell the QuoteFragment to show the quote string at position index
            mResponsesFragment.showQuoteAtIndex(index);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onPause()");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onRestart()");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onResume()");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onStart()");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onStop()");
        super.onStop();
    }

}
