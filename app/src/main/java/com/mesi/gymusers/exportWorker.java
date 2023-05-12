package com.mesi.gymusers;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class exportWorker extends Worker{

    public exportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        try {

            //StoragePath
            File file = new File(Environment.getExternalStorageDirectory() + "/Gym_Data/file/db.csv");

            FileWriter fw = new FileWriter(file);

            CSVWriter csvWrite = new CSVWriter(fw);

            DbHelper db = new DbHelper(getApplicationContext());

            List<UserDAO> usersList = db.getAllUsers();

            String[] userListArray;

            for (int i = 0; i<usersList.size(); i++){

                userListArray = new String[]{usersList.get(i).getFname(), usersList.get(i).getPhone(), usersList.get(i).getDate(), usersList.get(i).getShift(), usersList.get(i).getImg()};
                csvWrite.writeNext(userListArray);
            }

            csvWrite.flush();
            csvWrite.close();
            fw.close();

            String inputFolder = Environment.getExternalStorageDirectory()+"/Gym_Data/file/";
            String output = Environment.getExternalStorageDirectory() + "/Gym_Data/file.zip";
            zipFolder(inputFolder, output);

            toastMethod();


        }catch (Exception e){

        }


        return Result.success();
    }

    public void toastMethod(){

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "ተሳክቶአል", Toast.LENGTH_SHORT).show();
            }
        },1);
    }

    private static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                Log.d("", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
        }
    }
}
