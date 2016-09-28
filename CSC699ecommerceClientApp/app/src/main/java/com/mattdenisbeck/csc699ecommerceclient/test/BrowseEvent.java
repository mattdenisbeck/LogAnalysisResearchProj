package com.mattdenisbeck.csc699ecommerceclient.test;

import com.mattdenisbeck.csc699ecommerceclient.Product;

import java.util.Date;

/**
 * Created by icart on 7/4/16.
 */
public class BrowseEvent {
    private Product product;
    private Date startTime;
    private Date endTime;

    public BrowseEvent(){}

    public BrowseEvent(Product product, Date startTime, Date endTime){
        this.product = product;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
}


