package com.mattdenisbeck.csc699ecommerceclient.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by icart on 8/3/16.
 */
public class MeasureProxyPerformance {

    private static final String BASEURL = "http://10.2.56.30:8080/gs-rest-service-0.1.0/product-catalog/id/";
    private static final String[] PROXYIDS = {"26d29665-a56e-4b9c-b946-033fabbb7d37","205f2a82-4e2b-4311-ba51-1ef198ea230f",
                                                "c7b6f817-f81c-44b1-9e38-5353a8c899f0","79f82a7d-6887-475c-97bc-3adea7641bf9",
                                                "064630c8-b2c3-4ec3-9349-33642b409fbe","8e97e515-e68e-4645-a44f-9edbe99a2626",
                                                "23869842-3d6c-4e26-9cc9-2a9996bcbcc9","41e0e30b-26c1-4962-a544-6f3275d81b7b",
                                                "41e0e30b-26c1-4962-a544-6f3275d81b7b","c84024c1-0483-4d35-bbc7-a3c8798f84d1"};
    private static final String[] CLOUDIDS = {"df3c0539-0ce9-4b69-958b-f39d5afa77c3","2fc73369-816b-441f-a983-b0cbd78cebdd",
                                                "36d55f8f-ffe8-495e-b078-495da4ad79c5","1a4e93ac-62f5-4db5-9dd2-2c12c8f421d3",
                                                "843c5db3-fde1-4ad5-b71d-af4bcb0128a4","b6ff7e40-87b9-4572-bc9d-c004605d98ad",
                                                "fefdd274-26f7-4463-8a67-1cf530819155","661bf086-b00d-4fbb-b870-e0e8de2b5999",
                                                "ad74e77b-0bf7-4e12-9a54-f03af537316c","9f401561-cf01-4d93-9a59-1300bbdcf660"};
    private static final DecimalFormat DF2 = new DecimalFormat(".##");
    private static final double HITRATE = 0.9;
    private static final int SAMPLESIZE = 100;
    private static double proxyThroughput, cloudThroughput, throughputImprovement, proxyResponseTime,
            cloudResponseTime, responseImprovement, percentDecrease;
    private static long before, after, elapsed, beforeTP;

    public static void main(String args[]) throws MalformedURLException {
        System.out.println("Testing Response Time...");
        testResponseTime();

        System.out.println("Testing Throughput");
        testThroughput();

        printResults();
    }

    private static void printResults() {
        System.out.println("\nResults: ");
        System.out.println("\nTest 1 - Response Time (Sample Size = " + SAMPLESIZE + ")");
        System.out.println("Average Response Time with Proxy (Hit rate = " + HITRATE*100 + "%): " + DF2.format(proxyResponseTime) + "ms");
        System.out.println("Average Response Time without Proxy: " + DF2.format(cloudResponseTime) + "ms");
        System.out.println("Average Improvement: " + DF2.format(responseImprovement) + "ms | " + DF2.format(percentDecrease) + "% speed up");

        System.out.println("\nTest 2 - Throughput (Sample Size = " + SAMPLESIZE + ")");
        System.out.println("Throughput with Proxy (Hit rate = " + HITRATE*100 + "%): " + DF2.format(proxyThroughput*1000) + " reads/second");
        System.out.println("Throughput without Proxy: " + DF2.format(cloudThroughput*1000) + " reads/second");
        System.out.println("Throughput improved by " + DF2.format(throughputImprovement) + "%");
    }

    private static void testThroughput() throws MalformedURLException {
        Random rnd = new Random();
        int index, hitOrMiss;

        //test proxy throughput with hit rate
        List<String> proxyBooks = new ArrayList<>();
        before = System.currentTimeMillis();
        for(int i = 0; i < SAMPLESIZE; i++){
            hitOrMiss = rnd.nextInt(100);
            if(hitOrMiss <= 100*HITRATE){
                index = rnd.nextInt(PROXYIDS.length);
                proxyBooks.add(getBook(index, PROXYIDS));
            }
            else{
                index = rnd.nextInt(CLOUDIDS.length);
                proxyBooks.add(getBook(index, CLOUDIDS));
            }

        }
        after = System.currentTimeMillis();
        elapsed = after - before;
        proxyThroughput = ((double)SAMPLESIZE / elapsed);

        //test cloud throughput
        List<String> cloudBooks = new ArrayList<>();
        before = System.currentTimeMillis();
        for(int i = 0; i < SAMPLESIZE; i++){
            index = rnd.nextInt(CLOUDIDS.length);
            proxyBooks.add(getBook(index, CLOUDIDS));
        }
        after = System.currentTimeMillis();
        elapsed = after - before;
        cloudThroughput = ((double)SAMPLESIZE / elapsed);
        throughputImprovement = ((proxyThroughput - cloudThroughput)/cloudThroughput)*100;
    }

    private static String getBook(int index, String[] ids) throws MalformedURLException {
        String book = "";
        URL url = new URL(BASEURL + ids[index]);
        try {

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Sending 'GET' request to URL : " + url + " : Response Code : " + responseCode);

            BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            book = response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }

    public static void testResponseTime() throws MalformedURLException {
        Random rnd = new Random();
        int index, hitOrMiss;
        double proxySum = 0;
        double cloudSum = 0;

        for(int i = 0; i < SAMPLESIZE; i++){
            hitOrMiss = rnd.nextInt(100);
            if(hitOrMiss <= HITRATE*100){
                index = rnd.nextInt(PROXYIDS.length);
                proxySum += getResponseTime(index,PROXYIDS);
            }
            else{
                index = rnd.nextInt(CLOUDIDS.length);
                proxySum += getResponseTime(index,CLOUDIDS);
            }

            index = rnd.nextInt(CLOUDIDS.length);
            cloudSum += getResponseTime(index,CLOUDIDS);
        }
        proxyResponseTime = proxySum/SAMPLESIZE;
        cloudResponseTime = cloudSum/SAMPLESIZE;
        responseImprovement = cloudResponseTime - proxyResponseTime;
        percentDecrease = (responseImprovement/cloudResponseTime)*100;
    }

    private static long getResponseTime(int index, String[] ids) throws MalformedURLException {
        long before, after;
        long elapsed = 0;
        URL url = new URL(BASEURL + ids[index]);
        try {
            before = System.currentTimeMillis();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Sending 'GET' request to URL : " + url + " : Response Code : " + responseCode);

            BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            after = System.currentTimeMillis();
            elapsed = after - before;
            //print result
            System.out.println(response.toString());
            System.out.println("Elapsed: " + elapsed + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return elapsed;
    }
}
