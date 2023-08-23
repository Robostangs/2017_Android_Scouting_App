package me.a548.qrdisplay1;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Chronometer;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    public final static int QRcodeWidth = 500 ;
    private EditText etqr;
    private Button btn;
    private EditText teamNumber;
    private EditText matchNumber;
    private Switch autoGear;
    private Switch baseline;
    private Switch autoHighGoal;
    private int teleGearCount = 0;
    public TextView teleGear;
    public Button teleGearPlus;
    public Button teleGearMinus;
    public Button climbTimeButton;
    public Chronometer climbTime;
    private int climbButtonCount = 0;
    public String data;
    private Switch succClimb;
    private EditText comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etqr = (EditText) findViewById(R.id.etqr);
        btn = (Button) findViewById(R.id.btn);
        teamNumber = (EditText) findViewById(R.id.teamNumber);
        matchNumber = (EditText) findViewById(R.id.matchNumber);
        autoGear = (Switch) findViewById(R.id.autoGear);
        baseline = (Switch) findViewById(R.id.baseline);
        autoHighGoal = (Switch) findViewById(R.id.autoHighGoal);
        teleGearPlus = (Button) findViewById(R.id.teleGearPlus);
        teleGearMinus = (Button) findViewById(R.id.teleGearMinus);
        teleGear = (TextView) findViewById(R.id.teleGear);
        climbTimeButton = (Button) findViewById(R.id.climbTimeButton);
        climbTime = (Chronometer) findViewById(R.id.climbTime);
        succClimb = (Switch) findViewById(R.id.succClimb);
        comments = (EditText) findViewById(R.id.comments);

        //Following code allow the app packages to lock task in true kiosk mode
        // get policy manager
        DevicePolicyManager myDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // get this app package name
        ComponentName mDPM = new ComponentName(this, MyAdmin.class);

        if (myDevicePolicyManager.isDeviceOwnerApp(this.getPackageName())) {
            // get this app package name
            String[] packages = {this.getPackageName()};
            // mDPM is the admin package, and allow the specified packages to lock task
            myDevicePolicyManager.setLockTaskPackages(mDPM, packages);
            startLockTask();
        } else {
            Toast.makeText(getApplicationContext(),"Not owner", Toast.LENGTH_LONG).show();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Closes keyboard
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if(etqr.getText().toString().compareTo("20012477") == 0) {
                    stopLockTask();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Are you sure you want to submit? Tap anywhere outside of this box to go back.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "SUBMIT",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //Takes all the data collected and stores it as one String
                                    data = etqr.getText().toString() +
                                            "," + teamNumber.getText().toString() +
                                            "," + matchNumber.getText().toString() +
                                            "," + autoGear.isChecked() +
                                            "," + baseline.isChecked() +
                                            "," + autoHighGoal.isChecked() +
                                            "," + teleGear.getText().toString() +
                                            "," + climbTime.getText().toString() +
                                            "," + succClimb.isChecked() +
                                            "," + comments.getText().toString();

                                    //Uses Intent to send that string to DisplayActivity to be converted into a QR code
                                    Intent dataSend = new Intent(MainActivity.this, DisplayActivity.class);
                                    dataSend.putExtra("dat", data);
                                    startActivity(dataSend);

                                }
                            });
                    alertDialog.show();
                }
            }
        });

        teleGearPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleGearCount = teleGearCount + 1;
                teleGear.setText(String.valueOf(teleGearCount));
            }
        });

        teleGearMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teleGearCount = teleGearCount - 1;
                teleGear.setText(String.valueOf(teleGearCount));
            }
        });

        climbTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (climbButtonCount == 0) {
                    climbTime.setBase(SystemClock.elapsedRealtime());
                    climbTime.start();
                    climbButtonCount = 1;
                } else {
                    climbTime.stop();
                }
            }
        });
    }
}

