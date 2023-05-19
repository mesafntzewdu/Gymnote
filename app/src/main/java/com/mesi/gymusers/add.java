package com.mesi.gymusers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.Manifest;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class add extends Fragment {

    Button submit;
    Spinner spinner;
    ImageView uImg;
    Bitmap bml = null;
    private String shiftSelected;
    private String imageAbsolutePath = "";

    private String[] PERMISSION_ARRAY;

    public add() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add, container, false);
        PERMISSION_ARRAY = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};


        spinnerListener(v);
        getImageAndSave(v);
        buttonClickListner(v);
        return v;
    }


    private void spinnerListener(View v) {
        spinner = v.findViewById(R.id.shift);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(), R.array.shift, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                shiftSelected = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getImageAndSave(View v) {

        uImg = v.findViewById(R.id.add_img);
        uImg.setOnClickListener(view ->
        {
            if (checkCameraPermissionIfGranted())
                takePicCameraGallery();
            else
                cameraLauncher.launch(PERMISSION_ARRAY[0]);
        });

    }

    ActivityResultLauncher<String> storageLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

        if (isGranted) {
            getBitmapAndStoreItOnTheObjct();
        } else {
            Toast.makeText(getContext(), "To continue \"Storage\" permission has to be allowed.", Toast.LENGTH_SHORT).show();
        }
    });

    //Camera permission here
    ActivityResultLauncher<String> cameraLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

        if (isGranted) {
            takePicCameraGallery();
        } else {
            Toast.makeText(getContext(), "To continue \"Camera\" permission has to be allowed.", Toast.LENGTH_SHORT).show();
        }

    });

    //check camera permission and if it is granted allow the user to continue to the next step
    public boolean checkCameraPermissionIfGranted() {
        return ContextCompat.checkSelfPermission(getContext(), PERMISSION_ARRAY[0]) == PackageManager.PERMISSION_GRANTED;
    }

    private void takePicCameraGallery() {

        String[] values = {"Camera", "Gallery"};
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setCancelable(true);
        alert.setItems(values, (dialogInterface, itemPosition) ->
        {
            if (values[itemPosition].equals("Camera"))
                getImageFormCamera();
            if (values[itemPosition].equals("Gallery"))
                getImageFromGallery();
        });
        AlertDialog builder = alert.create();
        builder.show();
    }

    private void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 121);
    }

    private void getImageFormCamera() {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 120);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //image from camera
            if (requestCode == 120) {

                if (data != null) {
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    bml = bm;
                    uImg.setImageBitmap(bm);
                }
            }

            //image from gallery
            if (requestCode == 121) {

                if (data != null) {

                    try {

                        InputStream input = getContext().getContentResolver().openInputStream(data.getData());
                        Bitmap bm = BitmapFactory.decodeStream(input);
                        bml = bm;

                        uImg.setImageBitmap(bm);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                }

            }

        }
    }

    //Insert data into the database
    private void buttonClickListner(View v) {
        submit = v.findViewById(R.id.submit);

        submit.setOnClickListener(view -> {

            saveUserDataToDatabase(v);

        });
    }

    //Save user data into the database on user register method
    private void saveUserDataToDatabase(View v) {

        EditText fname = v.findViewById(R.id.user_name);
        EditText phone = v.findViewById(R.id.phone_no);
        EditText date = v.findViewById(R.id.adate);

        DbHelper db = new DbHelper(getContext());

        if (fname.getText().toString().equals("")) {
            Toast.makeText(getContext(), "ስም ባዶ መሆን የለበትም፡፡", Toast.LENGTH_LONG).show();
            return;
        }
        if (date.getText().toString().equals("")) {
            Toast.makeText(getContext(), "ቀን ባዶ መሆን የለበትም፡፡", Toast.LENGTH_LONG).show();
            return;
        }

        //Check for the duplicated user name
        if (db.checkUserNameDuplication(fname.getText().toString())) {
            Toast.makeText(getContext(), "ያልተመዘገበ ስም ይጠቀሙ፡፡", Toast.LENGTH_SHORT).show();
            return;
        }


        if (bml == null) {
            Toast.makeText(getContext(), "ምስል ባዶ መሆን የለበትም፡፡", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("Testing the permission", ""+checkStoragePermissionIfGranted());

        if (checkStoragePermissionIfGranted()) {
            //SAVE IMAGE ABSOLUTE PATH IN THE DATABASE HERE WHEN THE USER CLICK THE REGISTER BUTTON
            getBitmapAndStoreItOnTheObjct();


            if (db.insertUser(fname.getText().toString(), phone.getText().toString(), calculateUeserFinalDate(date.getText().toString()), shiftSelected, imageAbsolutePath))
                Toast.makeText(getContext(), "ተሳክቶአል", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "አልተሳካም", Toast.LENGTH_SHORT).show();

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    if (!Environment.isExternalStorageManager())
                    {
                        Intent i = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                        i.setData(uri);
                        startActivity(i);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Intent i = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(i);

                }

            } else {
                storageLauncher.launch(PERMISSION_ARRAY[1]);
            }

        }
    }

    //Calculate user final date and assign the result into the local variable
    private String calculateUeserFinalDate(String userRegDate) {

        //Add user registered date to the current date using calendar
        Calendar calRed = Calendar.getInstance();
        calRed.add(Calendar.DATE, Integer.parseInt(userRegDate));

        //Format the date using dd/mm/yyyy format to save on the local variable
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        return sdf.format(calRed.getTime());
    }

    private void getBitmapAndStoreItOnTheObjct() {

        if (checkStoragePermissionIfGranted()) {
            //Then save image inside the folder in this method
            saveImageInsideTheFolder();
            Log.d("Absolute path", imageAbsolutePath);

        } else {
            requestPermissions(PERMISSION_ARRAY, 201);
        }
    }

    //Save image inside the folder
    private void saveImageInsideTheFolder() {
        try {


            if (createFolder()) {

                Calendar cal = Calendar.getInstance();
                File file = new File(Environment.getExternalStorageDirectory() + "/Gym_Data/file/", "gym_img" + cal.getTimeInMillis() + ".png");
                FileOutputStream fout = new FileOutputStream(file);

                bml.compress(Bitmap.CompressFormat.PNG, 25, fout);

                //save image absolute path in the local variable to store on the database
                imageAbsolutePath = file.getAbsolutePath();

                fout.flush();
                fout.close();

                Log.d("BitMap", "BitMap successfully created");
            }

        } catch (Exception e) {

        }
    }

    //Create folder inside the external storage
    public boolean createFolder() {
        File f = new File(Environment.getExternalStorageDirectory(), "/Gym_Data/file");
        if (f.exists())
            return true;

        return f.mkdirs();
    }

    //check if permission is granted
    private boolean checkStoragePermissionIfGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(requireContext(), PERMISSION_ARRAY[1]) == PackageManager.PERMISSION_GRANTED;
        }
    }
}