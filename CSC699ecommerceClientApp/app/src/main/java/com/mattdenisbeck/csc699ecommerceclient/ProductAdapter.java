package com.mattdenisbeck.csc699ecommerceclient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;


public class ProductAdapter extends ArrayAdapter<Object> {

    Context context;
    int layoutResourceId;
    Object data[] = null;
    NetworkSingleton networkSingletonInstance;

    public ProductAdapter(Context context, int layoutResourceId, Object[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        networkSingletonInstance = NetworkSingleton.getInstance(context.getApplicationContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        ProductHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ProductHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtDesc = (TextView)row.findViewById(R.id.txtDesc);
            holder.txtPrice = (TextView)row.findViewById(R.id.txtPrice);

            row.setTag(holder);
        }

        else{
            holder = (ProductHolder)row.getTag();
        }

        Product product = (Product)data[position];
        holder.txtTitle.setText(product.getName());
        holder.txtDesc.setText(product.getDescription());
        holder.txtPrice.setText(String.valueOf(product.getPrice()));

        final ProductHolder finalHolder = holder;
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

        return row;
    }

    public void clear(){
        data = null;
    }

    public void addAll(Object[] data){
        this.data = data;
    }

    static class ProductHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtDesc;
        TextView txtPrice;
    }
}
