package com.mattdenisbeck.csc699ecommerceclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.*;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsEvent;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.internal.core.util.DateUtil;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mattdenisbeck.csc699ecommerceclient.test.LogGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private Context main = this;
    private String url = "https://10.0.2.2:8443/gs-rest-service-0.1.0/product-catalog";
    private Cart myCart = Cart.getInstance();
    private Button btnCart;
    private Button btnSearch;
    private EditText edtSearch;
    private ListView productListView;
    private NetworkSingleton networkSingletonInstance;
    private ArrayList<Product> productsArray = new ArrayList<>();
    private ProductAdapter adapter;
    private Object[] data;

    private static MobileAnalyticsManager analytics;
    private static String appId = "6ec86ee18c084eb0b53743f89c9696c0";
    private static String identityPoolId = "us-east-1:27cfb42b-fd5e-4c71-a113-d1748d68c75d";

    private LogGenerator logGenerator;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        new NukeSSLCerts().nuke();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCart = (Button)findViewById(R.id.btnCart);
        btnCart.setText("Cart: " + myCart.getCount());

        btnSearch = (Button)findViewById(R.id.btnSearch);
        edtSearch = (EditText)findViewById(R.id.edtSearch);

        productListView = (ListView)findViewById(R.id.listView);

        networkSingletonInstance = NetworkSingleton.getInstance(this.getApplicationContext());

        final JsonArrayRequest productRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response){
                        try {
                            for(int i = 0; i < response.length(); i++){
                                JSONObject json_data = response.getJSONObject(i);
                                Product resultRow = new Product();
                                resultRow.setId(json_data.getString("id"));
                                resultRow.setName(json_data.getString("name") + " " + i);
                                resultRow.setCategory(json_data.getString("category"));
                                resultRow.setDescription(json_data.getString("description"));
                                resultRow.setFeatures(json_data.getString("features"));
                                //resultRow.setImage(json_data.getString("image"));
                                //temp product image
                                resultRow.setImage("https://10.0.2.2:8443/gs-rest-service-0.1.0/img/file-roller.png");
                                resultRow.setPrice(json_data.getDouble("price"));
                                productsArray.add(resultRow);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        data = productsArray.toArray();
                        adapter = new ProductAdapter(main, R.layout.listview_item_row, data);

                        View header = getLayoutInflater().inflate(R.layout.listview_header_row, null);
                        productListView.addHeaderView(header);
                        productListView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                    }
                });

        networkSingletonInstance.addToRequestQueue(productRequest);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
                Product choice = productsArray.get(position - 1); //header is in position 0
                Intent launchProductDetail = new Intent(view.getContext(), ProductDetail.class);
                launchProductDetail.putExtra("name", choice.getName());
                launchProductDetail.putExtra("category",choice.getCategory());
                launchProductDetail.putExtra("price", choice.getPrice());
                launchProductDetail.putExtra("description", choice.getDescription());
                launchProductDetail.putExtra("features", choice.getFeatures());
                launchProductDetail.putExtra("image", choice.getImage());
                launchProductDetail.putExtra("product", choice);
                startActivity(launchProductDetail);
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchCart = new Intent(main, ViewCart.class);
                startActivity(launchCart);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productsArray.clear();
                String query = "search/" + edtSearch.getText().toString();
                networkSingletonInstance.addToRequestQueue(createSearchRequest(query));
            }
        });

        /*//simulate traffic on app, send logs to MobileAnalyticsManager
        //AWS Mobile Analytics code
        try{
            //analytics = MobileAnalyticsManager.getInstance(appId);
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    main,
                    appId,
                    identityPoolId);
        } catch (InitializationException ex){
            Log.e("Log Generator", "Failed to initialize Amazon Mobile Analytics", ex);
        }*/
        logGenerator = new LogGenerator();
        logGenerator.simulateAppTraffic(main);

        /*//test MobileAnalytics Logging
        //AWS Mobile Analytics code
        try{
            //analytics = MobileAnalyticsManager.getInstance(appId);
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    main,
                    appId,
                    identityPoolId);
        } catch (InitializationException ex){
            Log.e("Log Generator", "Failed to initialize Amazon Mobile Analytics", ex);
        }
        AnalyticsEvent event = analytics.getEventClient().createEvent("Test")
                .withAttribute("My Key", "My Value")
                .withMetric("My Metric", 99.99);
        analytics.getEventClient().recordEvent(event);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        analytics.getEventClient().submitEvents();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        btnCart = (Button)findViewById(R.id.btnCart);
        btnCart.setText("Cart: " + myCart.getCount());

        if(analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
    }

    private JsonArrayRequest createSearchRequest(String query){

        return new JsonArrayRequest
                (Request.Method.GET, url + "/" + query, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response){
                        try {
                            for(int i = 0; i < response.length(); i++){
                                JSONObject json_data = response.getJSONObject(i);
                                Product resultRow = new Product();
                                resultRow.setId(json_data.getString("id"));
                                resultRow.setName(json_data.getString("name") + " " + i);
                                resultRow.setCategory(json_data.getString("category"));
                                resultRow.setDescription(json_data.getString("description"));
                                resultRow.setFeatures(json_data.getString("features"));
                                //resultRow.setImage(json_data.getString("image"));
                                //temp product image
                                resultRow.setImage("https://10.0.2.2:8443/gs-rest-service-0.1.0/img/file-roller.png");
                                resultRow.setPrice(json_data.getDouble("price"));
                                productsArray.add(resultRow);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        data = productsArray.toArray();
                        adapter = new ProductAdapter(main, R.layout.listview_item_row, data);
                        productListView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                    }
                });
    }
}
