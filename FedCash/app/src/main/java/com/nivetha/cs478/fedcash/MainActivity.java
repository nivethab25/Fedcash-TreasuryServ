package com.nivetha.cs478.fedcash;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.nivetha.cs478.treasuryServCommon.TreasuryService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "FedCash";

    // Widgets to get Service type and input parameters
    EditText etxtMYear, etxtYYear, etxtDate, etxtWorkinDays;
    Button btnSend, btnUnbind, btnView;
    RadioButton rbDaily, rbMonthly, rbYearly;

    // TreasuryService instance
    protected TreasuryService mService;

    // ServiceConnection
    ServiceConnection mServiceConnection;

    // Private fields for service requests and response data
    private boolean isBound;
    private String mDate;
    private int mWorkingDays;
    private int mDay, mMonth, mYear;
    private int[] mDailyCash, mMonthlyCash;
    private int mYearlyAverage;
    private ArrayList<String> serviceCalls = new ArrayList<>();
    private ArrayList<String> serviceResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Removing the default focus on first child view
        ScrollView view = (ScrollView)findViewById(R.id.scrollView);
        view.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });

        isBound = false;

        // Binding to view elemenets
        etxtMYear = (EditText) findViewById(R.id.etxtMYear);
        etxtYYear = (EditText) findViewById(R.id.etxtYYear);
        etxtDate = (EditText) findViewById(R.id.etxtDate);
        etxtWorkinDays = (EditText) findViewById(R.id.etxtWorkDays);

        // Send Button and click listener
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean b = false;

                // If not already bound to service, bind now
                if(!isBound){
                    Intent i = new Intent(TreasuryService.class.getName());
                    ResolveInfo info = getPackageManager().resolveService(i, Service.BIND_AUTO_CREATE);
                    i.setComponent(new ComponentName(info.serviceInfo.packageName,info.serviceInfo.name));

                    //binding to remote service
                    b = bindService(i, mServiceConnection, Service.BIND_AUTO_CREATE);

                }

                if (isBound||b) {
                    // If dailyCash
                    if(rbDaily.isChecked()) {
                        mDate = etxtDate.getText().toString();
                        if(!mDate.equals("") && mDate.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})") && !etxtWorkinDays.getText().toString().equals("")) {
                            mDay = Integer.parseInt(mDate.substring(0,mDate.indexOf("/")));
                            mMonth = Integer.parseInt(mDate.substring(mDate.indexOf("/")+1,mDate.lastIndexOf("/")));
                            mYear = Integer.parseInt(mDate.substring(mDate.lastIndexOf("/")+1));
                            mWorkingDays = Integer.parseInt(etxtWorkinDays.getText().toString());
                            if(mDay > 0 && mDay < 32 && mMonth > 0 && mMonth < 13 && mYear >= 2006 && mYear <= 2016 && mWorkingDays >= 5 && mWorkingDays <= 25) {
                                Toast.makeText(getApplicationContext(),"Sending Service Request...",Toast.LENGTH_LONG).show();
                                Thread worker = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            mDailyCash = mService.dailyCash(mDay, mMonth, mYear, mWorkingDays);
                                            String str="[";
                                            for(int val:mDailyCash) {
                                                str+=String.valueOf(val)+", ";
                                            }
                                            serviceResults.add(str.substring(0,str.lastIndexOf(','))+"]");
                                        }
                                        catch (RemoteException e) {}
                                    }
                                });
                                worker.start();
                                serviceCalls.add("dailyCash("+mDay+","+mMonth+","+mYear+","+mWorkingDays+")");
                            }
                            else {
                                if(mYear > 2016 || mYear < 2006)
                                    Toast.makeText(getApplicationContext(),"Please enter a date in years 2006-2016",Toast.LENGTH_SHORT).show();
                                if(mWorkingDays > 25 || mWorkingDays < 5)
                                    Toast.makeText(getApplicationContext(),"Please enter no. of working days in range 5-25",Toast.LENGTH_SHORT).show();
                                if(mDay <= 0 || mDay > 31 || mMonth <= 0 || mMonth > 12)
                                    Toast.makeText(getApplicationContext(),"Please enter a valid date!",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (!mDate.equals("") && !mDate.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})"))
                            Toast.makeText(getApplicationContext(),"Please enter date in format DD/MM/YYYY",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(),"Please enter input parameters for Service Request",Toast.LENGTH_SHORT).show();

                    }

                    // If monthlyCash
                    else if(rbMonthly.isChecked()) {
                        if(!etxtMYear.getText().toString().equals("")) {
                            mYear = Integer.parseInt(etxtMYear.getText().toString());
                            if(mYear >= 2006 && mYear <= 2016){
                                Toast.makeText(getApplicationContext(),"Sending Service Request...",Toast.LENGTH_LONG).show();
                                Thread worker = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            mMonthlyCash = mService.monthlyCash(mYear);
                                            String str="[";
                                            for(int val:mMonthlyCash) {
                                                str+=String.valueOf(val)+", ";
                                            }
                                            serviceResults.add(str.substring(0,str.lastIndexOf(','))+"]");
                                        }
                                        catch (RemoteException e) {}
                                    }
                                });
                                worker.start();
                                serviceCalls.add("monthlyCash("+mYear+")");
                            }
                            else
                                Toast.makeText(getApplicationContext(),"Please enter a year in range 2006-2016",Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(getApplicationContext(),"Please enter input parameters for Service Request",Toast.LENGTH_SHORT).show();
                    }

                    // If yearlyCash
                    else if(rbYearly.isChecked()) {
                        if(!etxtYYear.getText().toString().equals("")) {
                            mYear = Integer.parseInt(etxtYYear.getText().toString());
                            if(mYear >= 2006 && mYear <= 2016) {
                                Toast.makeText(getApplicationContext(),"Sending Service Request...",Toast.LENGTH_LONG).show();
                                Thread worker = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            mYearlyAverage = mService.yearlyAvg(mYear);
                                            serviceResults.add(String.valueOf(mYearlyAverage));
                                        } catch (RemoteException e) {
                                        }
                                    }
                                });
                                worker.start();
                                serviceCalls.add("yearlyAvg(" + mYear + ")");
                            }
                            else
                                Toast.makeText(getApplicationContext(),"Please enter a year in range 2006-2016",Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Please enter input parameters for Service Request",Toast.LENGTH_SHORT).show();
                    }

                    else
                        Toast.makeText(getApplicationContext(),"Please select a Service Request",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't bind to TreasuryService!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Unbind service button and click listener
        btnUnbind = (Button) findViewById(R.id.btnUnbind);
        btnUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBound) {
                    Toast.makeText(getApplicationContext(),"Unbinding from TreasuryService...",Toast.LENGTH_LONG).show();
                    unbindService(mServiceConnection);
                    isBound = false;
                }
                else
                    Toast.makeText(getApplicationContext(),"Not bound to any Service...",Toast.LENGTH_LONG).show();
            }
        });

        // View service calls button and listener
        btnView = (Button) findViewById(R.id.btnView);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startServiceCalls = new Intent(getApplicationContext(), ServiceCalls.class);
                startServiceCalls.putStringArrayListExtra("serviceCalls", serviceCalls);
                startServiceCalls.putStringArrayListExtra("serviceResults", serviceResults);
                startActivity(startServiceCalls);
            }
        });

        rbDaily = (RadioButton) findViewById(R.id.rbDaily);
        rbMonthly = (RadioButton) findViewById(R.id.rbMonthly);
        rbYearly = (RadioButton) findViewById(R.id.rbYearly);

        initConnection();
    }

    // Instantiating service connection
    void initConnection(){
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
                mService = null;
                isBound = false;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                // TODO Auto-generated method stub
                mService = TreasuryService.Stub.asInterface(service);
                isBound = true;
            }
        };
        if(mService == null)
        {
            Intent it = new Intent(TreasuryService.class.getName());
            ResolveInfo info = getPackageManager().resolveService(it, Service.BIND_AUTO_CREATE);
            it.setComponent(new ComponentName(info.serviceInfo.packageName,info.serviceInfo.name));

            //binding to remote service
            boolean b = false;
            b = bindService(it, mServiceConnection, Service.BIND_AUTO_CREATE);
            if (b) {
                Log.i(TAG, "bindService() succeeded!");
            } else {
                Log.i(TAG, "bindService() failed!");
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        isBound = false;

        // stop the Treasury and record that the treasury has stopped

    }

    public void clearEditTexts(View v){
        etxtMYear.setText("");
        etxtMYear.clearFocus();
        etxtYYear.setText("");
        etxtYYear.clearFocus();
        etxtDate.setText("");
        etxtDate.clearFocus();
        etxtWorkinDays.setText("");
        etxtWorkinDays.clearFocus();
    }

}
