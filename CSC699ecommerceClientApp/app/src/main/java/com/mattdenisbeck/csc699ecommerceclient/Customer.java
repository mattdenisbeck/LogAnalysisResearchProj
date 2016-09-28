package com.mattdenisbeck.csc699ecommerceclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Customer implements Serializable {

    private String fName, lName, street, city, state, zip,
            phone, email, ccType;

    public Customer(){}

    public Customer(String fName, String lName, String street, String city,
                    String state, String zip, String phone, String email, String ccType){
        this.fName = fName;
        this.lName = lName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
        this.ccType = ccType;
    }

    public String toString(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("fName",fName);
            obj.put("lName",lName);
            obj.put("street",street);
            obj.put("city",city);
            obj.put("state",state);
            obj.put("zip",zip);
            obj.put("phone",phone);
            obj.put("email",email);
            obj.put("ccType", ccType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
