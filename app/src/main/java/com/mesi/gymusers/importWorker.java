package com.mesi.gymusers;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class importWorker extends Worker {

    public importWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            unzipfile(Environment.getExternalStorageDirectory() + "/Gym_Data/file.zip", Environment.getExternalStorageDirectory() + "/Gym_Data/file");


            DbHelper db = new DbHelper(getApplicationContext());

            File file = new File(Environment.getExternalStorageDirectory() + "/Gym_Data/file/db.csv");

            FileReader fileReader = new FileReader(file);
            CSVReader reader = new CSVReader(fileReader);

            String[] nextReader;

            while ((nextReader = reader.readNext()) != null) {
                for (int i = 0; i < nextReader.length; i++) {
                    if (!db.checkUserNameDuplication(nextReader[0]))
                        db.insertUser(nextReader[0], nextReader[1], nextReader[2], nextReader[3], nextReader[4]);
                }

            }

            toastMethod("ተሳክቶአል");


        } catch (Exception e) {
            toastMethod("አልተሳካም");
        }

        return Result.success();
    }

    public void toastMethod(String message) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        }, 1);

    }

    private static void unzipfile(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // creating an output directory if it doesn't exist already
        if (!dir.exists()) dir.mkdirs();
        FileInputStream FiS;
        // buffer to read and write data in the file
        byte[] buffer = new byte[1024];
        try {
            FiS = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(FiS);

            ZipEntry ZE = zis.getNextEntry();
            while (ZE != null) {
                String fileName = ZE.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println(" Unzipping to " + newFile.getAbsolutePath());
                // create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                // close this ZipEntry
                zis.closeEntry();
                ZE = zis.getNextEntry();
            }
            // close last ZipEntry
            zis.closeEntry();
            zis.close();
            FiS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
