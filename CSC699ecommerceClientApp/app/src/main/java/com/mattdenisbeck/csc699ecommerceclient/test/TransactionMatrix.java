package com.mattdenisbeck.csc699ecommerceclient.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TransactionMatrix {
    static Map<String, Map<String,Integer>> matrix;

    public static void main(String args[]){
        matrix = buildMatrix();
        print(matrix);
    }

    private static Map<String,Map<String,Integer>> buildMatrix() {
        Map<String, Map<String,Integer>> matrix = new HashMap<>();
        JsonObject attributes, metrics;
        String userRegion, language, genre, price, key;
        int count;
        File logF = new File("simulatedMobileAnalyticsLog.txt");
        JsonParser parser = new JsonParser();
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new FileReader(logF));
            String line = null;
            int lineCount = 0;
            while((line = buffer.readLine()) != null){
                JsonElement el = parser.parse(line);
                attributes = ((JsonObject)el).getAsJsonObject("attributes");
                metrics = ((JsonObject)el).getAsJsonObject("metrics");
                userRegion = attributes.get("UserRegion").getAsString();
                language = attributes.get("Language").getAsString();
                genre = attributes.get("Genre").getAsString();
                price = metrics.get("Price").getAsString();
                key = language + "-" + genre + "-" + price;
                //if this is a new tuple, initialize
                if(!matrix.containsKey(key)){
                    matrix.put(key, new HashMap<String, Integer>());
                    matrix.get(key).put("USA", 0);
                    matrix.get(key).put("South America", 0);
                    matrix.get(key).put("China", 0);
                    matrix.get(key).put("India", 0);
                    matrix.get(key).put("Middle East", 0);
                    matrix.get(key).put(userRegion, 1);
                }
                //this is not a new tuple
                else {
                    count = matrix.get(key).get(userRegion);
                    count++;
                    matrix.get(key).put(userRegion,count);
                }
                lineCount++;
            }
            System.out.println("log entries: " + lineCount);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return matrix;
    }

    //prints matrix to console and file
    public static void print(Map<String, Map<String,Integer>> matrix){
        String key = matrix.entrySet().iterator().next().getKey();
        for(Map.Entry i : matrix.get(key).entrySet()){
            System.out.print(i.getKey() + " ");
        }
        TreeMap<String,Map<String,Integer>> sortedMatrix = new TreeMap(matrix);
        System.out.print("\n");

        try {
            File file = new File("transactionMatrix.txt");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            System.out.println("Tuple,South America,Middle East,USA,China,India");
            bw.write("Tuple,South America,Middle East,USA,China,India\n");
            int matrixTotal = 0;
            for (Map.Entry e : sortedMatrix.entrySet()) {
                System.out.print(e.getKey());
                bw.write(e.getKey().toString());
                Map<String, Integer> val = (Map<String, Integer>) e.getValue();

                for (Map.Entry f : val.entrySet()) {
                    Integer num = (Integer)f.getValue();
                    System.out.print("," + num);
                    bw.write("," + num);
                    matrixTotal += num;
                }
                System.out.print("\n");
                bw.write("\n");
            }
            System.out.println("Matrix Total: " + matrixTotal);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printTuples(JsonArray log){
        for(JsonElement obj : log){
            JsonObject attributes = ((JsonObject)obj).getAsJsonObject("attributes");
            JsonObject metrics = ((JsonObject)obj).getAsJsonObject("metrics");
            System.out.print(attributes.get("UserRegion")  + ": ");
            System.out.print(attributes.get("Language")  + "-");
            System.out.print(attributes.get("Genre") + "-");
            System.out.print(metrics.get("Price"));
            System.out.print("\n");
        }
    }
}
