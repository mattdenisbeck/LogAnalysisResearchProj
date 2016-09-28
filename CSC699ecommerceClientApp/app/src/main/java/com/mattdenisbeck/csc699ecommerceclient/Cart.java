package com.mattdenisbeck.csc699ecommerceclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cart {
    private static Cart myCart;
    private static Cart dupCart;
    private Map<Product, Integer> productQuantities;
    private int count;

    private Cart() {
        productQuantities = new HashMap<>();
    }

    static synchronized Cart getInstance() {
        if (myCart == null) {
            myCart = new Cart();
        }
        return myCart;
    }


    public void addProduct(Product product){
        addProduct(product,1);
    }

    public Cart getDupCart(){
        dupCart = new Cart();
        dupCart.productQuantities = myCart.productQuantities;
        return dupCart;
    }

    public void addProduct(Product product, int quantity){
        if (productQuantities.containsKey(product)){
            productQuantities.put(product, productQuantities.get(product) + quantity);
        }
        else {
            productQuantities.put(product, quantity);
        }
        count += quantity;
    }

    public void deleteProduct(Product product){
        if (productQuantities.containsKey(product)) {
            count = count - productQuantities.get(product);
            productQuantities.remove(product);
        }

    }

    public void changeQuantity(Product product, int newQuantity){
        if(productQuantities.containsKey(product)) {
            count = count - productQuantities.get(product);
            productQuantities.put(product, newQuantity);
            count += newQuantity;
        }
    }

    public Set<Product> getProductSet(){
        return productQuantities.keySet();
    }

    public Integer getQuantity(Product product){
        if(productQuantities.containsKey(product)){
            return productQuantities.get(product);
        }
        return 0;
    }

    public double getTotal(){
        double total = 0;
        for (Product product: getProductSet()) {
            total += product.getPrice() * getQuantity(product);
        }
        return total;
    }

    public int getCount(){
        return count;
    }

    public Object[][] toArray(){
        Set<Product> productSet = getProductSet();
        int size = productSet.size();
        Object[][] data = new Object[size][2];

        int i = 0;
        for (Product product: productSet) {
            data[i][0] = product;
            data[i][1] = getQuantity(product);
            i++;
        }

        return data;
    }

    public void emptyCart(){
        productQuantities.clear();
        count = 0;
    }

    public Map<String,Integer> getIDQuantityMap(){
        Map<String, Integer> map = new HashMap<>();
        Set<Product> productSet = getProductSet();
        for (Product product: productSet) {
            map.put(product.getId(), productQuantities.get(product));
        }
        return map;
    }

    public String toString(){
        JSONObject obj = new JSONObject();
        Set<Product> productSet = getProductSet();
        try {
            for (Product product : productSet) {
                obj.put(product.getId(), String.valueOf(productQuantities.get(product)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
