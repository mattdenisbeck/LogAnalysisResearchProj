package com.mattdenisbeck.csc699ecommerceclient.test;

import com.mattdenisbeck.csc699ecommerceclient.Product;

import java.util.*;
import java.util.Date;

/**
 * Created by icart on 7/4/16.
 */
public class ShoppingSession {
    private String userRegion;
    private UserCoordinates userCoordinates;
    private List<BrowseEvent> browseEvents;
    private Calendar startTime;

    private static Map<String, Map<String, Double>> regionToLangProbabilityMap = new HashMap<>();
    private static Map<Double, Double> priceProbabilityMap = new HashMap<>();

    private static final double DIFF_LANG_PROB = .05;
    private static final double DIFF_GENRE_PROB = .4;

    private static final String[] GENRES = {"Computer Science", "Mystery", "Graphic Novel",
            "Philosophy", "Biography"};

    //set up probability matrices
    static {
        regionToLangProbabilityMap.put("USA", new HashMap<String, Double>());
        regionToLangProbabilityMap.get("USA").put("English", .9);
        regionToLangProbabilityMap.get("USA").put("Spanish", .03);
        regionToLangProbabilityMap.get("USA").put("Chinese", .03);
        regionToLangProbabilityMap.get("USA").put("Arabic", .02);
        regionToLangProbabilityMap.get("USA").put("Hindi", .02);
        regionToLangProbabilityMap.put("China", new HashMap<String, Double>());
        regionToLangProbabilityMap.get("China").put("English", .03);
        regionToLangProbabilityMap.get("China").put("Spanish", .02);
        regionToLangProbabilityMap.get("China").put("Chinese", .9);
        regionToLangProbabilityMap.get("China").put("Arabic", .02);
        regionToLangProbabilityMap.get("China").put("Hindi", .03);
        regionToLangProbabilityMap.put("South America", new HashMap<String, Double>());
        regionToLangProbabilityMap.get("South America").put("English", .03);
        regionToLangProbabilityMap.get("South America").put("Spanish", .9);
        regionToLangProbabilityMap.get("South America").put("Chinese", .03);
        regionToLangProbabilityMap.get("South America").put("Arabic", .02);
        regionToLangProbabilityMap.get("South America").put("Hindi", .02);
        regionToLangProbabilityMap.put("India", new HashMap<String, Double>());
        regionToLangProbabilityMap.get("India").put("English", .03);
        regionToLangProbabilityMap.get("India").put("Spanish", .02);
        regionToLangProbabilityMap.get("India").put("Chinese", .03);
        regionToLangProbabilityMap.get("India").put("Arabic", .02);
        regionToLangProbabilityMap.get("India").put("Hindi", .9);
        regionToLangProbabilityMap.put("Middle East", new HashMap<String, Double>());
        regionToLangProbabilityMap.get("Middle East").put("English", .03);
        regionToLangProbabilityMap.get("Middle East").put("Spanish", .02);
        regionToLangProbabilityMap.get("Middle East").put("Chinese", .03);
        regionToLangProbabilityMap.get("Middle East").put("Arabic", .9);
        regionToLangProbabilityMap.get("Middle East").put("Hindi", .02);

        priceProbabilityMap.put(19.99, .3);
        priceProbabilityMap.put(39.99, .3);
        priceProbabilityMap.put(89.99, .15);
        priceProbabilityMap.put(129.99, .13);
        priceProbabilityMap.put(199.99, .12);
    }

    public ShoppingSession(){}

    public ShoppingSession(String userRegion, List<BrowseEvent> browseEvents){
        this.userRegion = userRegion;
        this.browseEvents = browseEvents;
    }

    public String getUserRegion(){ return this.userRegion; }
    public void setUserRegion(String region){ this.userRegion = region; }

    public UserCoordinates getUserCoordinates() { return userCoordinates; }
    public void setUserCoordinates(UserCoordinates userCoordinates){ this.userCoordinates = userCoordinates; }

    public List<BrowseEvent> getBrowseEvents(){ return this.browseEvents; }
    public void setBrowseEvents(List<BrowseEvent> browseEvents){ this.browseEvents = browseEvents; }

    public void addBrowseEvent(BrowseEvent event) {
        this.browseEvents.add(event);
    }

    public Calendar getStartTime() { return this.startTime; }
    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public BrowseEvent generateRandomBrowseEvent(Date startTime) {
        Product product = new Product();
        String genre, language;
        if(this.getBrowseEvents().size() > 0){
            //get genre and lang of last browse event
            BrowseEvent lastBrowseEvent = this.getBrowseEvents().get(this.getBrowseEvents().size() - 1);
            Product lastProduct = lastBrowseEvent.getProduct();
            genre = lastProduct.getGenre();
            language = lastProduct.getLanguage();

            //generate genre and lang for next event
            if(Math.random() <= DIFF_LANG_PROB){
                product.setLanguage(this.getRandomLang());
            }
            else{
                product.setLanguage(language);
            }
            if(Math.random() <= DIFF_GENRE_PROB){
                product.setGenre(this.getRandomGenre());
            }
            else{
                product.setGenre(genre);
            }
        }
        else{
            //generate genre and lang for 1st event
            product.setLanguage(this.getRandomLang());
            product.setGenre(this.getRandomGenre());
        }
        product.setPrice(this.getRandomPrice());
        product.setCategory("Book");
        product.setDescription("The descriptions...");
        product.setFeatures("The features...");
        product.setName("A Book");
        product.setId("1");
        BrowseEvent event = new BrowseEvent();
        event.setStartTime(startTime);
        event.setProduct(product);
        event.setEndTime(getRandomEndTime(startTime));

        return event;
    }

    private Date getRandomEndTime(Date startTime) {
        long start = startTime.getTime();
        Random rnd = new Random();
        int seconds = rnd.nextInt(120) + 5;
        long end = start + (seconds * 1000);
        return new Date(end);
    }

    public String getRandomLang() {
        String region = this.getUserRegion();
        Map<String, Double> probabilities = regionToLangProbabilityMap.get(region);
        double random = Math.random();
        String lang = "";
        for (Map.Entry<String, Double> entry : probabilities.entrySet())
        {
            random -= entry.getValue();
            if (random <= 0.0d)
            {
                lang = entry.getKey();
                break;
            }
        }
        return lang;
    }

    public String getRandomGenre(){
        Random rand = new Random();
        int rndInt = rand.nextInt(GENRES.length);
        return GENRES[rndInt];
    }

    public double getRandomPrice() {
        double price = 0;
        double random = Math.random();
        for (Map.Entry<Double, Double> entry : priceProbabilityMap.entrySet())
        {
            random -= entry.getValue();
            if (random <= 0.0d)
            {
                price = entry.getKey();
                break;
            }
        }
        return price;
    }
}
