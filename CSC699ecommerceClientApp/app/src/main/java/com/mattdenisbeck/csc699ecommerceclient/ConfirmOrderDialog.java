package com.mattdenisbeck.csc699ecommerceclient;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConfirmOrderDialog extends DialogFragment {

    private AlertDialog.Builder builder;
    private Cart cart = Cart.getInstance();
    private Bundle bundle;
    private static String url = "https://10.0.2.2:8443/gs-rest-service-0.1.0/order/save";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        bundle = getArguments();
        // Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        final AlertDialog.Builder builder = this.builder.setMessage("Confirm Order?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //create order object
                        Customer customer = (Customer) (bundle.get("customer"));
                        Order order = new Order(cart.getDupCart(), customer);

                        //send order to database, can't get it to work
                        //saveOrder(getContext(), order);

                        //log order

                        //empty cart
                        cart.emptyCart();
                        SuccessDialog successDialog = new SuccessDialog();
                        successDialog.setCancelable(false);
                        successDialog.show(getFragmentManager(), "successDialog");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //close dialog
                    }
                });
        // Create the AlertDialog object and return it
        return this.builder.create();
    }

    public static void saveOrder(Context context, final Order order){
        NetworkSingleton networkSingleton = NetworkSingleton.getInstance(context);
        RequestQueue queue = networkSingleton.getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("Timestamp", order.getTimeStamp());
            jsonObject.put("Customer",order.getCustomer());
            jsonObject.put("Cart", order.getCart());
        } catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(request);
    }
}

