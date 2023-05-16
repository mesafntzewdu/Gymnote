package com.mesi.gymusers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sport_users";
    private static final int VERSION = 1;
    private SQLiteDatabase db;
    private Cursor c;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table users(id INTEGER primary key, fname TEXT, phone TEXT, date TEXT, shift TEXT, img TEXT)");
        db.execSQL("create table permissions(id INTEGER primary key, u_id TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("drop table if exists users");
    }

    //insert users into the sqlite database
    public boolean insertUser(String fname, String phone, String date, String shift, String img){

        ContentValues c = new ContentValues();
        c.put("fname", fname);
        c.put("phone", phone);
        c.put("date", date);
        c.put("shift", shift);
        c.put("img", img);

        try{
            db = this.getWritableDatabase();
            long x = db.insert("users", null, c);

            return x!=-1;
        }catch (Exception e){

        }finally {
            if (db.isOpen())
                db.close();
        }
        return false;
    }

    //delete users from the database
    public boolean deleteUsers(String id){
        try {
            db = this.getWritableDatabase();
           long x =  db.delete("users", "id=?", new String[]{id});

            return x!=-1;
        }catch (Exception e){

        }finally {
            if (db.isOpen())
                db.close();
        }
        return false;
    }


    //update user
    public boolean updateUsers(String id, String fname, String phone, String date, String shift){

        ContentValues c = new ContentValues();
        c.put("fname", fname);
        c.put("phone", phone);
        c.put("date", date);
        c.put("shift", shift);

        try{
            db = this.getWritableDatabase();
            long x = db.update("users", c, "id=?", new String[]{id});
            Log.d("Id cheking", id);

            return x!=-1;
        }catch (Exception e){


            e.printStackTrace();
        }finally {
            if (db.isOpen())
                db.close();
        }

        return false;
    }

    public ArrayList<UserDAO> getAllUsers(){
        ArrayList<UserDAO> uList = new ArrayList<>();

        try{
            db = this.getReadableDatabase();

             c = db.rawQuery("select * from users" ,null);
            if (c.getCount()>0)
                c.moveToFirst();
            while (!c.isAfterLast()){
                UserDAO U = new UserDAO(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5));
                        uList.add(U);
                        c.moveToNext();
            }

            Log.d("Table size", c.getCount()+"");
        }catch (Exception e){}
        finally {

                db.close();
                c.close();

        }

        return uList;
    }

    //getSingle user
    public void getSingleUser(String id){

        try{
            db = this.getReadableDatabase();
            c = db.rawQuery("select * from users where id=?", new String[]{id});
            if (c.getCount()>0)
                c.moveToFirst();
            SingleDAO.getSingleDAOInstance().setId(Integer.parseInt(id));
            SingleDAO.getSingleDAOInstance().setFname(c.getString(1));
            SingleDAO.getSingleDAOInstance().setPhone(c.getString(2));
            SingleDAO.getSingleDAOInstance().setDate(c.getString(3));
            SingleDAO.getSingleDAOInstance().setShift(c.getString(4));
            SingleDAO.getSingleDAOInstance().setImg(c.getString(5));
        }catch (Exception e){}
        finally {
                db.close();
                c.close();

        }
    }

    public boolean checkUserNameDuplication(String fname){

       try{
           db = this.getReadableDatabase();

           c = db.rawQuery("select * from users where fname=? ", new String[]{fname});

           if (c.getCount()>0)
               return true;
       }catch (Exception e)
       {
           return false;
       }finally {
           db.close();
           c.close();
       }

        return false;
    }

    public String clientSize(){
        db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from users", null);

        String val = String.valueOf(c.getCount());

        db.close();
        c.close();

        return val;
    }


    public boolean  permissionExists(){
        db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from permissions", null);

        if(c.getCount()>0)
        {
            return true;
        }
        //String val = String.valueOf(c.getCount());

        db.close();
        c.close();

        return false;
    }

    public void insertPermission(String key){

        ContentValues c = new ContentValues();
        c.put("u_id", key);

        try{
            db = this.getWritableDatabase();
            db.insert("permissions", null, c);

        }catch (Exception e){

        }finally {
            if (db.isOpen())
                db.close();
        }
    }

    public String getKey(){
        db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from permissions", null);
        String val = null;
        if (c.getCount()>0)
        {
            c.moveToFirst();
             val = String.valueOf(c.getString(1));
        }

        db.close();
        c.close();

        return val;
    }


}
