package com.mesi.gymusers;

public class SingleDAO {
    private int id;
    private String fname;
    private String phone;
    private String date;
    private String shift;
    private String img;

    private static SingleDAO singleInstance;

    public static SingleDAO getSingleDAOInstance(){

        if (singleInstance!=null)
            return  singleInstance;
        else
            return singleInstance = new SingleDAO();
    }


    private SingleDAO(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
