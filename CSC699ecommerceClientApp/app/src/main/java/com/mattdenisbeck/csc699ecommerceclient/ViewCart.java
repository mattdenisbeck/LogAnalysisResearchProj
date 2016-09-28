package com.mattdenisbeck.csc699ecommerceclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;


public class ViewCart extends AppCompatActivity {
    private Cart myCart = Cart.getInstance();
    private Object[][] data;
    private ListView cartListView;
    private TextView txtTotal;
    private TextView txtItems;
    private Button btnCheckout;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);

        data = myCart.toArray();
        cartListView = (ListView)findViewById(R.id.cartListView);
        txtTotal = (TextView)findViewById(R.id.txtTotal);
        DecimalFormat df = new DecimalFormat("#.##");
        txtTotal.setText(df.format(myCart.getTotal()));
        CartAdapter adapter = new CartAdapter(this, R.layout.cartlistview_item_row, data, this);
        View header = getLayoutInflater().inflate(R.layout.cartlistview_header_row, null);
        txtItems = (TextView)header.findViewById(R.id.txtItems);
        txtItems.setText("Items: " + myCart.getCount());
        cartListView.addHeaderView(header);
        cartListView.setAdapter(adapter);

        btnCheckout = (Button)findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Checkout.class);
                startActivity(intent);
            }
        });
    }
}
