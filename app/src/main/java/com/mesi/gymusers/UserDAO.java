package com.mesi.gymusers;

public class UserDAO {

    private int id;
    private String fname;
    private String phone;
    private String date;
    private String shift;
    private String img;

    public UserDAO(int id, String fname, String phone, String date, String shift, String img) {
        this.id = id;
        this.fname = fname;
        this.phone = phone;
        this.date = date;
        this.shift = shift;
        this.img = img;
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
