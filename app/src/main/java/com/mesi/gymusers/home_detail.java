package com.mesi.gymusers;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class home_detail extends Fragment {

    TextView uName;
    TextView phone;
    EditText date;
    TextView shift;
    ImageView uImg;

    Button update;
    Button delete;
    SingleDAO sDao;

    public home_detail() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_detail, container, false);

        //List view clicked user detail
        userDetail(v);

        return v;
    }

    private void userDetail(View v) {
        uName = v.findViewById(R.id.detail_full_name);
        phone = v.findViewById(R.id.detail_phone);
        date = v.findViewById(R.id.detail_dateL);
        shift = v.findViewById(R.id.detail_shift);
        uImg = v.findViewById(R.id.detail_img);

        update = v.findViewById(R.id.update_user);
        delete = v.findViewById(R.id.delete_user);

        //single user detail object from single dao class
        sDao = SingleDAO.getSingleDAOInstance();

        //set the object value to the text view
        uName.setText(sDao.getFname());
        phone.setText(sDao.getPhone());
        date.setText(String.valueOf(Math.abs(userLeftDate(sDao.getDate()))));
        shift.setText(sDao.getShift());


        //get image url from the database and convert it into the bitmap and display it on the image view
        SingleDAO sd = SingleDAO.getSingleDAOInstance();
        File f = new File(sd.getImg());
        if (f.isFile() && f.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(sd.getImg());
            uImg.setImageBitmap(bm);
        }

        //Update on click listener
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setCancelable(true);
                alert.setTitle("መረጃውን ለመቀየር ይስማማሉ");
                alert.setPositiveButton("አዎ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateUser();
                    }
                });
                alert.setNegativeButton("አይ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog builder = alert.create();
                builder.show();
            }
        });

        //Delete on click listener
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setCancelable(true);
                alert.setTitle("መረጃውን ለማጥፍት ይስማማሉ");
                alert.setPositiveButton("አዎ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       deleteUser();
                    }
                });
                alert.setNegativeButton("አይ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog builder = alert.create();
                builder.show();

            }
        });

    }

    //delete user from the database
    private void deleteUser() {

        DbHelper db = new DbHelper(getContext());
        if (db.deleteUsers(String.valueOf(sDao.getId()))) {
            Toast.makeText(getContext(), "ተሳክቶአል", Toast.LENGTH_SHORT).show();
            File file = new File(sDao.getImg());
            if (file.exists())
                file.delete();

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_frame, new home()).commit();
        }
        else
            Toast.makeText(getContext(), "አልተሳካም", Toast.LENGTH_SHORT).show();
    }

    private void updateUser() {
        DbHelper db = new DbHelper(getContext());

        if (date.getText().toString().equals(""))
        {
            Toast.makeText(getContext(), "ቀን ባዶ መሆን አይችልም", Toast.LENGTH_SHORT).show();
            return;
        }
        if (db.updateUsers(String.valueOf(sDao.getId()), uName.getText().toString(), phone.getText().toString(), calculateUserReDate(date.getText().toString()),shift.getText().toString()))
        {
            Toast.makeText(getContext(), "ተሳክቶአል", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_frame, new home()).commit();
        }
        else
            Toast.makeText(getContext(), "አልተሳካም", Toast.LENGTH_SHORT).show();
    }

   public String calculateUserReDate(String date){

        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        currentDate.add(Calendar.DATE, Integer.parseInt(date));

     return sdf.format(currentDate.getTime());
   }

   //Get user left day from the current date

    private long userLeftDate(String date) {

        //Current date
        Calendar currentDate = Calendar.getInstance();

        long currentLong = currentDate.getTimeInMillis();

        char d = date.charAt(0);
        char dd = date.charAt(1);

        char m = date.charAt(3);
        char mm = date.charAt(4);

        char y = date.charAt(6);
        char yy = date.charAt(7);
        char yyy = date.charAt(8);
        char yyyy = date.charAt(9);

        String fDate = ""+d+dd;
        Log.d("fdate", fDate);

        String fMonth = ""+m+mm;
        Log.d("month", fMonth);

        String fYear = ""+y+yy+yyy+yyyy;
        Log.d("year", fYear);


        //User end date
        Calendar lastDate = Calendar.getInstance();

        lastDate.set(Calendar.DATE, Integer.parseInt(fDate));
        lastDate.set(Calendar.MONTH,  Integer.parseInt(fMonth)-1);
        lastDate.set(Calendar.YEAR, Integer.parseInt(fYear));

        long finalLong = lastDate.getTimeInMillis();

        return (finalLong-currentLong)/86400000;
    }
}