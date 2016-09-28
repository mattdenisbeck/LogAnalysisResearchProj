import models.Product;
import repositories.ProductRepo;

import java.util.Date;
import java.sql.Timestamp;

/**
 * Created by icart on 7/24/16.
 */
public class MeasurePerformance {
    private static final String proxyID = "26d29665-a56e-4b9c-b946-033fabbb7d37";
    private static final String cloudID = "df3c0539-0ce9-4b69-958b-f39d5afa77c3";
    private static final ProductRepo repo = new ProductRepo();
    private static long before, after, proxyElapsed, cloudElapsed;
    private static double difference;

    public static void main(String[] args) {
        //used to get threads/connections up and running
        //if ommitted results skewed in favor of second product
        Product dummyProduct = repo.getProduct(cloudID);

        before = System.currentTimeMillis();
        Product proxyProduct = repo.getProduct(proxyID);
        after = System.currentTimeMillis();
        System.out.println("Proxy Product: " + proxyProduct.getName() + " : " + proxyProduct.getLanguage() + " : " + proxyProduct.getGenre());
        System.out.println("Before: " + before );
        System.out.println("After: " + after );
        proxyElapsed = after - before;
        System.out.println("Elapsed: " + proxyElapsed );

        before = System.currentTimeMillis();
        Product cloudProduct = repo.getProduct(cloudID);
        after = System.currentTimeMillis();
        System.out.println("Cloud Product: " + cloudProduct.getName() + " : " + cloudProduct.getLanguage() + " : " + cloudProduct.getGenre());
        System.out.println("Before: " + before );
        System.out.println("After: " + after );
        cloudElapsed = after - before;
        System.out.println("Elapsed: " + cloudElapsed );

        difference = cloudElapsed - proxyElapsed;
        System.out.println("Improvement: " + difference + "ms - " + ((difference/cloudElapsed)*100) + "% speed up");
    }
}
