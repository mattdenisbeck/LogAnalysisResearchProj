package com.mattdenisbeck.csc699ecommerceclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;

import java.text.DecimalFormat;

public class Checkout extends AppCompatActivity {

    private Cart myCart = Cart.getInstance();
    private TextView txtSubtotal;
    private TextView txtTax;
    private TextView txtShipping;
    private TextView txtTotal;
    private static DecimalFormat df2 = new DecimalFormat("#.00");
    private Button btnSubmitOrder;
    private EditText edtFName, edtLName, edtStreetAddress, edtCity,
            edtState, edtZip, edtPhone, edtEmail, edtCCNumber, edtCVV;
    private RadioGroup ccTypeGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        //calculate total bill
        double subtotal = myCart.getTotal();
        double tax = subtotal * .06;
        double shipping = myCart.getCount() * 1.99; //Shipping is $1.99 per item
        double total = subtotal + tax + shipping;

        //find and set text views
        txtSubtotal = (TextView)findViewById(R.id.txtSubtotal);
        txtSubtotal.setText(df2.format(subtotal));
        txtTax = (TextView)findViewById(R.id.txtTax);
        txtTax.setText(df2.format(tax));
        txtShipping = (TextView)findViewById(R.id.txtShipping);
        txtShipping.setText(df2.format(shipping));
        txtTotal = (TextView)findViewById(R.id.txtTotal);
        txtTotal.setText(df2.format(total));

        //find and set submit order button
        btnSubmitOrder = (Button)findViewById(R.id.btnSubmitOrder);
        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if form valid
                if(isFormValid()) {
                    String ccType = ((RadioButton)findViewById(ccTypeGroup.getCheckedRadioButtonId())).getText().toString();
                    Customer customer = new Customer(edtFName.getText().toString(), edtLName.getText().toString(),
                            edtStreetAddress.getText().toString(), edtCity.getText().toString(), edtState.getText().toString(),
                            edtZip.getText().toString(), edtPhone.getText().toString(), edtEmail.getText().toString(), ccType);
                    ConfirmOrderDialog confirmOrderDialog = new ConfirmOrderDialog();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("customer", customer);
                    confirmOrderDialog.setArguments(bundle);
                    confirmOrderDialog.show(getSupportFragmentManager(), "confirmOrder");
                }
                else {
                    ValidateFormDialog validateFormDialog = new ValidateFormDialog();
                    validateFormDialog.show(getSupportFragmentManager(), "validateForm");
                }
            }
        });
    }

    private boolean isFormValid() {
        //find and check EditText views
        edtFName = (EditText)findViewById(R.id.edtFName);
        int firstlen = edtFName.getText().length();
        edtLName = (EditText)findViewById(R.id.edtLName);
        int lastlen = edtLName.getText().length();
        edtStreetAddress = (EditText)findViewById(R.id.edtStreetAddress);
        int streetlen = edtStreetAddress.getText().length();
        edtCity = (EditText)findViewById(R.id.edtCity);
        int citylen = edtCity.getText().length();
        edtState = (EditText)findViewById(R.id.edtState);
        int statelen = edtState.getText().length();
        edtZip = (EditText)findViewById(R.id.edtZIP);
        int ziplen = edtZip.getText().length();
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        int phonelen = edtPhone.getText().length();
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        int emaillen = edtEmail.getText().length();
        edtCCNumber = (EditText)findViewById(R.id.edtCCNumber);
        int cclen = edtCCNumber.getText().length();
        edtCVV = (EditText)findViewById(R.id.edtCVV);
        int ccvlen = edtCVV.getText().length();
        ccTypeGroup = (RadioGroup)findViewById(R.id.ccTypeGroup);
        int select = ccTypeGroup.getCheckedRadioButtonId();

        if(firstlen == 0 || lastlen == 0 || streetlen == 0 || citylen == 0 || statelen == 0 || ziplen == 0
                || phonelen == 0 || emaillen == 0 || cclen == 0 || ccvlen == 0 || select == -1) {
            return false;
        }

        return true;
    }

}
