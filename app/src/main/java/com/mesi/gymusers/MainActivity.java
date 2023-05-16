package com.mesi.gymusers;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity {
    private String[] READPHONESTATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        READPHONESTATE = new String[]{Manifest.permission.READ_PHONE_STATE};

        Calendar finalDate = Calendar.getInstance();

        finalDate.set(Calendar.DATE, 13);
        finalDate.set(Calendar.MONTH, Calendar.JUNE);
        finalDate.set(Calendar.YEAR, 2023);


        Log.d("final Date", finalDate.getTime() + "");
        if (Calendar.getInstance().getTime().after(finalDate.getTime()) || !(new DbHelper(this).permissionExists())) {
            checkUserId();
        } else {
            fragmentSwitch();
        }

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

            resultLauncher.launch(Manifest.permission.READ_PHONE_STATE);

        }

    }

    private boolean checkPhoneState() {
        return ContextCompat.checkSelfPermission(this, READPHONESTATE[0]) == PackageManager.PERMISSION_GRANTED;
    }

    ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

        if (isGranted) {
            readPhoneStateGetId();
        } else {
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
        alert.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.activation_dialog, null);

        TextView divId;
        EditText userIn;
        Button actiate;
        TextView telegramLink;


        divId = v.findViewById(R.id.device_id);
        userIn = v.findViewById(R.id.user_key);
        actiate = v.findViewById(R.id.activate_u);
        telegramLink = v.findViewById(R.id.telegramLink);

        telegramLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+khg5yXVGrv04NzJk"));
                startActivity(i);
            }
        });

        divId.setText(getIMEIDeviceId(this));
        actiate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInVal = userIn.getText().toString().trim();

                if(insertUserKyIfMatchs(userInVal))
                {
                    insertUserKey();
                    recreate();
                }else
                {
                    Toast.makeText(MainActivity.this, "Invalid activation key.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setView(v);
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private boolean insertUserKyIfMatchs(String userInVal) {

        String val = encrypt(getIMEIDeviceId(this));

        return userInVal.equals(val.substring(0,10));
    }

    private void readPhoneStateGetId() {


        String val = encrypt(getIMEIDeviceId(this));

        if (val.substring(0,10).equals(getencryptedKey())) {

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


        Log.d("device id", deviceId);

        return deviceId;
    }

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final String SECRET_KEY = "1234567812121921";

    public static String encrypt(String plainText) {

        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String encryptedText) {
        try {

            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //insert permission to db
    private String getencryptedKey() {

        DbHelper db = new DbHelper(this);

        return db.getKey();
    }

    private void insertUserKey() {

        DbHelper db = new DbHelper(this);
        if (db.permissionExists())
        {

        }else
        {

            String val = encrypt(getIMEIDeviceId(this));

            db.insertPermission(val.substring(0,10));
        }

    }


}



