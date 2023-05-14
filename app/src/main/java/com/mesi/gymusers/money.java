package com.mesi.gymusers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.Manifest;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

public class money extends Fragment {

    TextView clientNumber;
    Button importDatabase;
    Button exportDatabase;

    WorkManager workManager;
    WorkRequest importWorkRequest;
    WorkRequest exportWorkRequest;

    private String[] STORAGE_PERMISSION;

    public money() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_money, container, false);

        importWorkRequest = OneTimeWorkRequest.from(importWorker.class);
        exportWorkRequest = OneTimeWorkRequest.from(exportWorker.class);


        STORAGE_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        getClinetTotalNumber(v);
        importUserDataFromCSV(v);
        exportUserDataFromCSV(v);


        return v;
    }

    private void exportUserDataFromCSV(View v) {
        exportDatabase = v.findViewById(R.id.exportDatabase);
        exportDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storagePermissionGranted()) {
                    createFolder();
                    WorkManager.getInstance(getContext()).enqueue(exportWorkRequest);
                } else {
                    getStoragePermission();
                }
            }
        });
    }


    private void importUserDataFromCSV(View v) {
        importDatabase = v.findViewById(R.id.importDatabase);
        importDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (storagePermissionGranted()) {
                    createFolder();
                    WorkManager.getInstance(getContext()).enqueue(importWorkRequest);
                } else {
                    getStoragePermission();
                }

            }
        });
    }

    private void getStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            try {
                Intent i = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                i.setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
                startActivity(i);
            }catch (Exception e)
            {
                Intent i = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
               // i.setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
                startActivity(i);
            }
        }else
        {
            importResultLauncher.launch(STORAGE_PERMISSION[0]);

        }
    }

    private void getClinetTotalNumber(View v) {
        clientNumber = v.findViewById(R.id.clientNumber);

        DbHelper db = new DbHelper(getContext());
        clientNumber.setText(db.clientSize());
    }

    ActivityResultLauncher<String> importResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

        if (isGranted) {
            createFolder();
            WorkManager.getInstance(getContext()).enqueue(importWorkRequest);
        } else {
            Toast.makeText(getContext(), "Allow ሚለውን ካልመረጡ \"Import\" \"Export\" ማድረግ አንችልም!", Toast.LENGTH_LONG).show();
        }

    });

    ActivityResultLauncher<String> exportResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

        if (isGranted) {
            createFolder();
            WorkManager.getInstance(getContext()).enqueue(exportWorkRequest);
        } else {
            Toast.makeText(getContext(), "Allow ሚለውን ካልመረጡ \"Import\" \"Export\" ማድረግ አንችልም!", Toast.LENGTH_LONG).show();
        }

    });

    private boolean storagePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return Environment.isExternalStorageManager();
        else
            return ContextCompat.checkSelfPermission(getContext(), STORAGE_PERMISSION[0]) == PackageManager.PERMISSION_GRANTED;
    }

    public void createFolder() {
        File file = new File(Environment.getExternalStorageDirectory() + "/Gym_Data/file");
        if (!file.exists())
            file.mkdirs();
    }
}