package com.example.events;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by rufflez on 8/31/14.
 */
@ParseClassName("Event")
public class Event extends ParseObject {

    public String getName(){
        return getString("Name");
    }

    public void setName(String name){
        put("Name", name);
    }

    public String getNumOfTicketsLeft(){
        return getString ("NumOfTicketsLeft");
    }

    public void setNumOfTicketsLeft(String numOfTicketsLeft){
        put("NumOfTicketsLeft", numOfTicketsLeft);
    }

    public String getPrice(){
        return getString ("Price");
    }

    public void setPrice(String price){
        put("Price", price);
    }

    @Override
    public String toString(){
        return getString("Name") + "\n" +
                       getString("Price") + "$" + "\n" +
                       getString("NumOfTicketsLeft");
    }
}