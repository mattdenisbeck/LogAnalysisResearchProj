package repositories;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import models.Book;
import models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepo {

    //DB client for main server
    private AmazonDynamoDBClient mainDB = new AmazonDynamoDBClient(new ProfileCredentialsProvider());

    //DB client for proxy server
    private final AmazonDynamoDBClient proxyDB = new AmazonDynamoDBClient(new ProfileCredentialsProvider()).withEndpoint("http://localhost:8000");
    private final DynamoDB mainDynamoDB = new DynamoDB(mainDB);
    private final DynamoDB proxyDynamoDB = new DynamoDB(proxyDB);

    private DynamoDBMapper mainMapper = new DynamoDBMapper(mainDB);
    private DynamoDBMapper proxyMapper = new DynamoDBMapper(proxyDB);

    public ProductRepo() { }

    public void saveProduct(Product product){
        mainMapper.save(product);
    }

    public void saveProducts(List<Product> products){
        List<Product> toDelete = new ArrayList<>();
        mainMapper.batchWrite(products, toDelete);
    }

    //queries proxy db first, before checking cloud deployed db
    public Product getProduct(String _id){
        //hitting Tokyo database instead of Virginia
        mainDB.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

        Product product = new Product();
        product.setId(_id);
        DynamoDBQueryExpression<Product> queryExpression;
        queryExpression = new DynamoDBQueryExpression<Product>().withHashKeyValues(product);

        List<Product> productList = proxyMapper.query(Product.class, queryExpression);
        if(productList.size() == 0){
            productList = mainMapper.query(Product.class, queryExpression);
        }
        return productList.get(0);
    }

    public List<Product> getProductsByCategory(String category){

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val", new AttributeValue().withS(category));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("Category = :val")
                .withExpressionAttributeValues(eav)
                .withLimit(100);

        return mainMapper.scan(Product.class, scanExpression);

    }

    public List<Product> getAllProducts(){

        Book book = new Book();
        book.setCategory("Book");

        DynamoDBQueryExpression<Product> expression = new DynamoDBQueryExpression<Product>()
                .withIndexName("CategoryIndex")
                .withConsistentRead(false)
                .withHashKeyValues(book)
                .withLimit(100);
        //return mapper.scan(Product.class, scanExpression);
        QueryResultPage<Product> resultPage = mainMapper.queryPage(Product.class, expression);
        return resultPage.getResults();
    }

    public List<Product> searchProducts(String query) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val", new AttributeValue().withS(query));
        Map<String, String> ean = new HashMap<>();
        ean.put("#productName", "Name");

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withExpressionAttributeNames(ean)
                .withExpressionAttributeValues(eav)
                .withFilterExpression("contains(Category, :val)" +
                        "OR contains(Features, :val)" +
                        "OR contains(Description, :val)" +
                        "OR contains(#productName, :val)")
                .withLimit(100);

        return mainMapper.scan(Product.class, scanExpression);
    }

    public void deleteProduct(Product product){
        Map<String,AttributeValue> map = new HashMap<>();
        AttributeValue id = new AttributeValue(product.getId());
        AttributeValue price = new AttributeValue();
        price.setN(String.valueOf(product.getPrice()));
        map.put("_id", id);
        map.put("Price", price);
        mainDB.deleteItem("ProductCatalog", map);
        //mapper.delete(product);
    }

    public List<Product> getProductsByLang(String language) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val", new AttributeValue().withS(language));
        Map<String, String> ean = new HashMap<>();
        ean.put("#attribute", "Language");

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#attribute = :val")
                .withExpressionAttributeValues(eav)
                .withExpressionAttributeNames(ean)
                .withLimit(1000);

        return mainMapper.scan(Product.class, scanExpression);
    }

    public void saveProductstoProxy(List<Product> products) {
        List<Product> toDelete = new ArrayList<>();
        proxyMapper.batchWrite(products, toDelete);
    }

    public void clearProxy() {

        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        List<Product> result = proxyMapper.scan(Product.class, expression);

        for(Product p : result){
            deleteProductfromProxy(p);
        }
    }

    public void deleteProductfromProxy(Product product){
        Map<String,AttributeValue> map = new HashMap<>();
        AttributeValue id = new AttributeValue(product.getId());
        AttributeValue price = new AttributeValue();
        price.setN(String.valueOf(product.getPrice()));
        map.put("_id", id);
        map.put("Price", price);
        proxyDB.deleteItem("BookCatalog", map);
    }
}
