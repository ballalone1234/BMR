package com.example.bmr;

public class historys {
    //private variables
    int _id;
    String _time;
    String _BMR;
    // Empty constructor
    public historys(){
    }
    // constructor
    public historys(int id, String name, String BMR){
        this._id = id;
        this._time = name;
        this._BMR = BMR;
    }
    // constructor
    public historys(String name, String _BMR){
        this._time = name;
        this._BMR = _BMR;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
    // setting id
    public void setID(int id){
        this._id = id;
    }
    // getting name
    public String getName(){
        return this._time;
    }
    // setting name
    public void setName(String name){
        this._time = name;
    }
    // getting phone number
    public String getPhoneNumber(){
        return this._BMR;
    }
    // setting phone number
    public void setPhoneNumber(String phone_nb){
        this._BMR = phone_nb;
    }
}

