package com.mattdenisbeck.csc699ecommerceclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

public class CartAdapter extends ArrayAdapter<Object> {

    Context context;
    int layoutResourceId;
    Object data[][] = null;
    NetworkSingleton networkSingletonInstance;
    Cart myCart = Cart.getInstance();
    Product product;
    Activity callingActivity;
    CartProductHolder holder;

    public CartAdapter(Context context, int layoutResourceId, Object[][] data, Activity callingActivity) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        networkSingletonInstance = NetworkSingleton.getInstance(context.getApplicationContext());
        this.callingActivity = callingActivity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row = convertView;
        holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CartProductHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtDesc = (TextView)row.findViewById(R.id.txtDesc);
            holder.txtUnitPrice = (TextView)row.findViewById(R.id.txtUnitPrice);
            holder.txtQty = (TextView)row.findViewById(R.id.txtQty);
            holder.btnDeleteProduct = (Button)row.findViewById(R.id.btnDeleteProduct);

            row.setTag(holder);
        }

        else{
            holder = (CartProductHolder)row.getTag();
        }

        product = (Product)data[position][0];
        holder.txtTitle.setText(product.getName());
        holder.txtDesc.setText(product.getDescription());
        holder.txtUnitPrice.setText(String.valueOf(product.getPrice()));
        holder.txtQty.setText(String.valueOf(data[position][1]));

        final CartProductHolder finalHolder = holder;
        ImageRequest request = new ImageRequest(product.getImage(),
                new Response.Listener<Bitmap>(){
                    @Override
                    public void onResponse(Bitmap bmp){
                        finalHolder.imgIcon.setImageBitmap(bmp);
                    }
                }, 0, 0, null,
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                    }
                });

        networkSingletonInstance.addToRequestQueue(request);

        holder.btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCart.deleteProduct(product);
                Intent refresh = new Intent(getContext(), ViewCart.class);
                callingActivity.finish();
                context.startActivity(refresh);
            }
        });

        return row;
    }

    static class CartProductHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtDesc;
        TextView txtUnitPrice;
        TextView txtQty;
        Button btnDeleteProduct;
    }
}
