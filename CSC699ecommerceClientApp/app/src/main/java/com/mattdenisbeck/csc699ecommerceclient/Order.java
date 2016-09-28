package com.mattdenisbeck.csc699ecommerceclient;

import java.sql.Timestamp;
import java.util.Date;

public class Order {
    private Cart cart;
    private Customer customer;
    private Timestamp timeStamp;

    public Order(){}

    public Order(Cart cart, Customer customer){
        this.cart = cart;
        this.customer = customer;
        Date date = new Date();
        timeStamp = new Timestamp(date.getTime());
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public Customer getCustomer(){
        return customer;
    }

    public Cart getCart(){
        return cart;
    }
}
