package com.mattdenisbeck.csc699ecommerceclient.test;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsEvent;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.mattdenisbeck.csc699ecommerceclient.Product;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by icart on 7/1/16.
 */
public class LogGenerator {

    private static MobileAnalyticsManager analytics;
    private static String appId = "6ec86ee18c084eb0b53743f89c9696c0";
    private static String identityPoolId = "us-east-1:27cfb42b-fd5e-4c71-a113-d1748d68c75d";
    //private Context context = this.getApplication();

    private static final double KEEP_SHOPPING_PROB = .75;
    private static final double[][] usaCoords = {{32,-117},{25,-80}, {46,-68}, {56,-96}, {65,-149}, {47,-121}, {45,-101},
            {34,-84}, {27,-98}, {35,-114}, {37,-86}, {40,-82}, {43,-108}, {37,-122}, {34,-117}, {40,-74},
            {38,-77}, {61,-149}, {21,-157}, {41,-87}};
    private static final double[][] southAmericaCoords = {{19,-99},{10,-66},{4,-73},{0,-78},{-16,-47},{-23,-43},{-23,-46},
            {-12,-76},{-16,-68},{-33,-70},{-34,-58},{-54,-67},{-25,-57},{20,-76},{10,-84},{9,-79},{6,-58},
            {5,-55},{4,-52},{-3,-59}};
    private static final double[][] middleEastCoords = {{36,37},{33,36},{32,35},{31,35},{33,35},{36,43},{35,44},{33,44},
            {30,48},{24,47},{25,55},{21,40},{2,45},{15,32},{30,31},{32,13},{36,10},{32,5},{33,-7},{12,15}};
    private static final double[][] indiaCoords = {{31,75},{28,77},{27,85},{25,85},{27,89},{27,94},{25,94},{22,91},{22,88},
            {17,83},{7,80},{9,78},{13,75},{19,73},{22,70},{21,79},{22,73},{18,73},{29,77},{19,72}};
    private static final double[][] chinaCoords = {{22,114},{25,122},{25,118},{31,121},{40,116},{44,125},{30,91},{40,76},
            {43,87},{37,97},{38,102},{29,106},{25,103},{23,108},{28,113},{32,119},{39,117},{34,109},{30,121},{48,88}};

    public static void main(String args[]){

        //set start date/time to today at midnight
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        try {
            File file = new File("simulatedMobileAnalyticsLog.txt");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            //loop through 24 hrs
            Random rnd = new Random();
            ShoppingSession session;
            for (int i = 0; i < 24; i++) {
                System.out.println("Printing hour #" + i);
                //choose a log file

                //bw.write("\n---------------HOUR-" + i + "-----------------");
                //loop through the hour
                for (int j = 0; j < 60; j++) {
                    for (int k = 0; k < 60; k++) {
                        //get random millisecond val
                        date.set(Calendar.MILLISECOND, rnd.nextInt(1000));

                        //generate random sessions
                        for(int l = 0; l < 3; l++) {
                            session = generateRandomSession(date);
                            printShoppingSession(session, bw);
                        }

                        //increment second
                        int second = date.get(Calendar.SECOND) + 1;
                        date.set(Calendar.SECOND, second);
                    }
                }
            }
            bw.close();
            fw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void sendToMobileAnalytics(ShoppingSession session) {
        String region = session.getUserRegion();
        AnalyticsEvent event;
        String sessionID = generateShoppingSessionID();
        for(BrowseEvent browseEvent : session.getBrowseEvents()) {
            Product product = browseEvent.getProduct();
            String startTime = (new Timestamp(browseEvent.getStartTime().getTime())).toString();
            String endTime = (new Timestamp(browseEvent.getEndTime().getTime())).toString();
            event = analytics.getEventClient().createEvent("Browse Event")
                    .withAttribute("SessionID", sessionID)
                    .withAttribute("StartTimeStamp", startTime)
                    .withAttribute("EndTimeStamp", endTime)
                    .withAttribute("UserRegion", region)
                    .withAttribute("Language", product.getLanguage())
                    .withAttribute("Genre", product.getGenre())
                    .withMetric("Price", product.getPrice());

            //Record the Level Complete event
            analytics.getEventClient().recordEvent(event);
        }
    }

    private static void printShoppingSession(ShoppingSession session, BufferedWriter bw) throws IOException{
        String region = session.getUserRegion();
        String latitude = String.valueOf(session.getUserCoordinates().getLatitude());
        String longitude = String.valueOf(session.getUserCoordinates().getLongitude());
        String sessionID = generateShoppingSessionID();
        for (BrowseEvent event : session.getBrowseEvents()) {
            Product product = event.getProduct();
            bw.write("{\"event_type\":\"Browse Event\",\"event_timestamp\":" + event.getStartTime().getTime() + ",\"arrival_timestamp\"" +
                    ":" + event.getStartTime().getTime() + 12345 + ",\"event_version\":\"3.0\",\"application\":" +
                    "{\"app_id\":\"6ec86ee18c084eb0b53743f89c9696c0\",\"cognito_identity_pool_id\":" +
                    "\"us-east-1:27cfb42b-fd5e-4c71-a113-d1748d68c75d\",\"package_name\":\"" +
                    "com.mattdenisbeck.csc699ecommerceclient\",\"sdk\":{\"name\":\"aws-sdk-android\"," +
                    "\"version\":\"2.2.19\"},\"title\":\"Matt's E-Commerce App\",\"version_name\":" +
                    "\"1.0\",\"version_code\":\"1\"},\"client\":{\"client_id\":\"9a8fcd95-e905-48bb-87a0-06edbd2afd0f" +
                    "\",\"cognito_id\":\"us-east-1:d36b3aef-4311-40f6-a5d6-6a56c7acff05\"},\"device\":{\"locale\":" +
                    "{\"code\":\"en_US\",\"country\":\"US\",\"language\":\"en\"},\"make\":\"unknown\"," +
                    "\"model\":\"Android SDK built for x86\",\"platform\":{\"name\":\"ANDROID\",\"version\":" +
                    "\"6.0\"}},\"session\":{\"session_id\":\"bd2afd0f-20160706-202813664\",\"start_timestamp\":" +
                    event.getStartTime().getTime() + "},\"attributes\":{\"Longitude\":\"" + longitude + "\"," +
                    "\"Latitude\":\"" + latitude + "\",\"Language\":\"" + product.getLanguage() + "\"," +
                    "\"EndTimeStamp\":\"" + (new Timestamp(event.getEndTime().getTime())).toString() + "\",\"UserRegion\":" +
                    "\"" + region + "\",\"StartTimeStamp\":\"" + (new Timestamp(event.getStartTime().getTime())).toString() +
                    "\",\"Genre\":\"" + product.getGenre() + "\",\"SessionID\":\"" + sessionID + "\"}," +
                    "\"metrics\":{\"Price\":" + String.valueOf(product.getPrice()) + "}}\n");
        }

    }

    public static ShoppingSession generateRandomSession(Calendar sessionStartTime){
        ShoppingSession session = new ShoppingSession();
        session.setStartTime(sessionStartTime);
        session.setUserRegion(getRandomRegion(sessionStartTime));
        session.setUserCoordinates(getRandomCoordinates(session.getUserRegion()));
        session.setBrowseEvents(new ArrayList<BrowseEvent>());
        BrowseEvent event = session.generateRandomBrowseEvent(sessionStartTime.getTime());
        session.addBrowseEvent(event);

        //random chance to keep shopping
        while(Math.random() <= KEEP_SHOPPING_PROB && session.getBrowseEvents().size() < 100){
            Date start = event.getEndTime();
            event = session.generateRandomBrowseEvent(start);
            session.addBrowseEvent(event);
        }
        return session;
    }

    private static UserCoordinates getRandomCoordinates(String userRegion) {
        double latitude, longitude;
        double[][] coords;
        double adjustment = Math.random() * 2;
        if(userRegion.equals("USA")) {
            coords = usaCoords;
        }
        else if(userRegion.equals("South America")){
            coords = southAmericaCoords;
        }
        else if(userRegion.equals("Middle East")){
            coords = middleEastCoords;
        }
        else if(userRegion.equals("India")){
            coords = indiaCoords;
        }
        else {
            coords = chinaCoords;
        }
        Random rnd = new Random();
        int index = rnd.nextInt(coords.length);
        latitude = round(coords[index][0] + Math.random() * 2, 2);
        longitude = round(coords[index][1] + Math.random() * 2, 2);

        return new UserCoordinates(latitude, longitude);
    }

    public static String generateShoppingSessionID() {
        String id = UUID.randomUUID().toString();
        return id;
    }

    public static String getRandomRegion(Calendar startTime) {
        int hour = startTime.get(Calendar.HOUR_OF_DAY);

        //set probability weights based on time of day
        Map<String, Double> regionWeights = new HashMap<>();
        if (hour < 24 && hour >= 19){
            //weight china
            regionWeights.put("China",.8);regionWeights.put("India",.1);regionWeights.put("USA",.033);
            regionWeights.put("South America",.033);regionWeights.put("Middle East",.034);
        }
        else if (hour < 19 && hour >= 14){
            //weight USA & south america(2nd)
            regionWeights.put("USA",.8);regionWeights.put("South America",.1);regionWeights.put("Middle East",.033);
            regionWeights.put("India",.033);regionWeights.put("China",.034);
        }
        else if (hour < 14 && hour >= 9){
            //weight south america & USA(2nd)
            regionWeights.put("South America",.8);regionWeights.put("USA",.1);regionWeights.put("India",.033);
            regionWeights.put("China",.033);regionWeights.put("Middle East",.034);
        }
        else if (hour < 9 && hour >= 5){
            //weight middle east & india(2nd)
            regionWeights.put("Middle East",.8);regionWeights.put("India",.1);regionWeights.put("China",.033);
            regionWeights.put("USA",.033);regionWeights.put("South America",.034);
        }
        else { //(hour < 5 && hour >= 0)
            //weight india & china(2nd)
            regionWeights.put("India",.8);regionWeights.put("China",.1);regionWeights.put("USA",.033);
            regionWeights.put("South America",.033);regionWeights.put("Middle East",.034);
        }

        // Now choose a random region

        double random = Math.random();
        String region = "";
        for (Map.Entry<String, Double> entry : regionWeights.entrySet())
        {
            random -= entry.getValue();
            if (random <= 0.0d)
            {
                region = entry.getKey();
                break;
            }
        }

        return region;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void simulateAppTraffic(Context context) {
        //AWS Mobile Analytics code
        try{
            //analytics = MobileAnalyticsManager.getInstance(appId);
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    context,
                    appId,
                    identityPoolId);
        } catch (InitializationException ex){
            Log.e("Log Generator", "Failed to initialize Amazon Mobile Analytics", ex);
        }

        //set start date/time to today at midnight
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        //loop through 24 hrs
        Random rnd = new Random();
        ShoppingSession session;
        for (int i = 0; i < 1; i++) {
            //loop through the hour
            for (int j = 0; j < 60; j++) {
                for (int k = 0; k < 60; k++) {
                    //generate 10 sessions per second
                    for (int l = 0; l < 10; l++) {
                        //get random millisecond val
                        date.set(Calendar.MILLISECOND, rnd.nextInt(1000));
                        //generate random sessions
                        session = generateRandomSession(date);
                        sendToMobileAnalytics(session);
                    }
                    //increment second
                    int second = date.get(Calendar.SECOND) + 1;
                    date.set(Calendar.SECOND, second);
                }
            }
        }
    }
}
