package com.mesi.gymusers;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private String[] READPHONESTATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        READPHONESTATE = new String[]{Manifest.permission.READ_PHONE_STATE};

            checkUserId();

        //replace fragment over the user selection
        fragmentSwitch();

    }


    public void fragmentSwitch() {
        //First load the home page
        replaceFragment(new home());
        //Bottom navigation listener and layout switching method
        BottomNavigationView navigationView = findViewById(R.id.navbar);

        navigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.nav_home:
                    replaceFragment(new home());
                    break;

                case R.id.nav_add:
                    replaceFragment(new add());
                    break;

                case R.id.nav_money:
                    replaceFragment(new money());
                    break;

                case R.id.nav_profile:
                    replaceFragment(new info());
                    break;

            }

            return true;
        });
    }

    public void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_frame, fragment);
        fragmentTransaction.commit();
    }

    private void checkUserId() {

        if (checkPhoneState()) {
            readPhoneStateGetId();
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               resultLauncher.launch(Manifest.permission.READ_PHONE_STATE);
            }

        }

    }

    private boolean checkPhoneState() {
        return ContextCompat.checkSelfPermission(this, READPHONESTATE[0]) == PackageManager.PERMISSION_GRANTED;
    }
    
    ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted->{

         if (isGranted)
         {
             readPhoneStateGetId();
         }else
         {
             alertDialogForDeny();
         }

    });

    private void alertDialogForDeny() {

        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(this);
        alert.setTitle("Permission Alert.");
        alert.setMessage("ይህንን መተግበሪያ ለመጠቀም  \"Allow\" ሚለውን ይምረጡ፤ ድጋሚ ዘግተው መክፈት ይጠበቅቦታል");
        alert.setCancelable(false);

        androidx.appcompat.app.AlertDialog builder = alert.create();
        builder.show();
    }

    private void lockScreen() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("ለማስከፈት በዚ ስልክ ይደውሉ\n0916073333\n0703747672\nID:" + getIMEIDeviceId(this));
        alert.setCancelable(false);

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void readPhoneStateGetId() {


        if (getIMEIDeviceId(this).equals("0ec1d940b1023fb4")) {

            fragmentSwitch();

        } else {
            lockScreen();
        }

    }


    public static String getIMEIDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    deviceId = mTelephony.getImei();
                } else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        return deviceId;
    }

}