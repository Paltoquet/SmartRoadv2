package com.westfolk.smartroadv2;

import java.util.Date;

/**
 * Created by thibault on 05/12/2016.
 */
public class Checkpoint {

    private String latitude;
    private String longitude;
    private int id;
    private Date date;

    public Checkpoint(String _lat,String _long){
        this.latitude = _lat;
        this.longitude = _long;
    }

    public Checkpoint(String _lat,String _long,int _id){
        this.latitude = _lat;
        this.longitude = _long;
        this.id = _id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
