package com.mattdenisbeck.csc699ecommerceclient;

import java.io.Serializable;

public class Product implements Serializable{

    private String _id;
    private String name;
    private String category;
    private String genre;
    private String language;
    private String description;
    private String features;
    private String image;       //URI to product image
    private double price;


    public Product(){}

    public Product(String _id, String name, String category, String description,
                   String features, String image, String language, String genre, double price) {
        this._id = _id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.features = features;
        this.image = image;
        this.price = price;
        this.genre = genre;
        this.language = language;
    }

    public String getId() { return _id; }
    public void setId(String _id) { this._id = _id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof Product) && (((Product) obj).getId().equals(this.getId()))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = (int) (Integer.parseInt(this.getId()) / 11);
        return result;
    }

}
