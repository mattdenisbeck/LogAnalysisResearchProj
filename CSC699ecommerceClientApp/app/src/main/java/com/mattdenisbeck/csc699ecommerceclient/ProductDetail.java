package com.mattdenisbeck.csc699ecommerceclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

public class ProductDetail extends AppCompatActivity {
    Context detail = this;
    NetworkSingleton networkSingletonInstance = NetworkSingleton.getInstance(this);
    Cart myCart = Cart.getInstance();
    Product product = new Product();
    private Button btnDetailCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Bundle extras = getIntent().getExtras();
        product = (Product) extras.get("product");

        btnDetailCart = (Button)findViewById(R.id.btnDetailCart);
        btnDetailCart.setText("Cart: " + myCart.getCount());

        TextView name = (TextView)findViewById(R.id.productName);
        TextView category = (TextView)findViewById(R.id.productCategory);
        TextView price = (TextView)findViewById(R.id.productPrice);
        TextView description = (TextView)findViewById(R.id.productDescription);
        TextView features = (TextView)findViewById(R.id.productFeatures);
        final ImageView image = (ImageView)findViewById(R.id.productImage);
        Button btnAddtoCart = (Button)findViewById(R.id.btnAddtoCart);
        Button btnViewCart = (Button)findViewById(R.id.btnViewCart);

        name.setText(product.getName());
        category.setText(product.getCategory());
        price.setText(String.valueOf(product.getPrice()));
        description.setText(product.getDescription());
        features.setText(product.getFeatures());


        ImageRequest request = new ImageRequest(product.getImage(),
                new Response.Listener<Bitmap>(){
                    @Override
                    public void onResponse(Bitmap bmp){
                        image.setImageBitmap(bmp);
                    }
                }, 0, 0, null,
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                    }
                });

        networkSingletonInstance.addToRequestQueue(request);

        btnAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myCart = Cart.getInstance();
                myCart.addProduct(product);
                btnDetailCart.setText("Cart: " + myCart.getCount());
            }
        });

        btnViewCart.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchCart = new Intent(detail, ViewCart.class);
                startActivity(launchCart);
            }
        }));

        btnDetailCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchCart = new Intent(detail, ViewCart.class);
                startActivity(launchCart);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        btnDetailCart = (Button)findViewById(R.id.btnDetailCart);
        btnDetailCart.setText("Cart: " + myCart.getCount());
    }
}
