import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import models.Book;
import models.Customer;
import models.Order;
import models.Product;
import repositories.OrderRepo;
import repositories.ProductRepo;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    private static final String[] GENRES = {"Computer Science", "Mystery", "Graphic Novel",
                                            "Philosophy", "Biography"};
    private static final String[] LANGUAGES = {"Chinese", "Spanish", "English", "Hindi", "Arabic"};
    private static int[] genreCounts = {0,0,0,0,0};
    private static final double[] PRICES = {19.99, 39.99, 89.99, 129.99, 199.99};
    private static final int PRODUCT_COUNT = 10000;

    public static void main(String[] args) {
        //createTable();
        System.out.println("hello");
        //populateDB();
        //deleteAllProducts();

        //OrderRepo orderRepo = new OrderRepo();

        /*Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Customer customer = new Customer("Matthew", "Beck", "405 E 5th St.", "Newport",
                "KY", "41071", "859-992-6463", "mattdenisbeck@gmail.com", "Visa");

        Product p1 = new Product("10", "Product10", "Laptop", "It's a cool ultrabook",
                "It's very light and fast", "img url", 899.99);
        Product p2 = new Product("11", "Product11", "Shoes", "Leather sandals",
                "High quality leather. Size 10", "img url", 79.99);
        Product p3 = new Product("12", "Product12", "Bedding", "Memory foam mattress",
                "Comfortable. Queen size", "img url", 699.99);

        Map<String, Integer> cart = new HashMap<>();
        cart.put(p1.getId(), 2);
        cart.put(p2.getId(), 3);
        cart.put(p3.getId(), 1);

        Order order = new Order(timestamp, customer, cart);
        orderRepo.saveOrder(order);*/
        //orderRepo.deleteOrder("eb453d28-0c16-463e-b405-27506d6bccdc");
    }

    static void createTable() {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient().withEndpoint("http://localhost:8000");
        // Attribute definitions
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("_id")
                .withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("Price")
                .withAttributeType("N"));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("Category")
                .withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("Genre")
                .withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("Language")
                .withAttributeType("S"));

// Table key schema
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
        tableKeySchema.add(new KeySchemaElement()
                .withAttributeName("_id")
                .withKeyType(KeyType.HASH));  //Partition key
        tableKeySchema.add(new KeySchemaElement()
                .withAttributeName("Price")
                .withKeyType(KeyType.RANGE));  //Sort key

// PrecipIndex
        GlobalSecondaryIndex categoryIndex = new GlobalSecondaryIndex()
                .withIndexName("CategoryIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 10)
                        .withWriteCapacityUnits((long) 1))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        GlobalSecondaryIndex genreIndex = new GlobalSecondaryIndex()
                .withIndexName("GenreIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 10)
                        .withWriteCapacityUnits((long) 1))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        GlobalSecondaryIndex languageIndex = new GlobalSecondaryIndex()
                .withIndexName("LanguageIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 10)
                        .withWriteCapacityUnits((long) 1))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        ArrayList<KeySchemaElement> catindexKeySchema = new ArrayList<>();
        ArrayList<KeySchemaElement> genindexKeySchema = new ArrayList<>();
        ArrayList<KeySchemaElement> langindexKeySchema = new ArrayList<>();

        catindexKeySchema.add(new KeySchemaElement()
                .withAttributeName("Category")
                .withKeyType(KeyType.HASH));  //Partition key
        catindexKeySchema.add(new KeySchemaElement()
                .withAttributeName("Price")
                .withKeyType(KeyType.RANGE));  //Sort key

        genindexKeySchema.add(new KeySchemaElement()
                .withAttributeName("Genre")
                .withKeyType(KeyType.HASH));  //Partition key
        genindexKeySchema.add(new KeySchemaElement()
                .withAttributeName("Price")
                .withKeyType(KeyType.RANGE));  //Sort key

        langindexKeySchema.add(new KeySchemaElement()
                .withAttributeName("Language")
                .withKeyType(KeyType.HASH));  //Partition key
        langindexKeySchema.add(new KeySchemaElement()
                .withAttributeName("Price")
                .withKeyType(KeyType.RANGE));  //Sort key

        categoryIndex.setKeySchema(catindexKeySchema);
        genreIndex.setKeySchema(genindexKeySchema);
        languageIndex.setKeySchema(langindexKeySchema);

        ArrayList<GlobalSecondaryIndex> gsi = new ArrayList<>();
        gsi.add(categoryIndex);
        gsi.add(genreIndex);
        gsi.add(languageIndex);

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName("ProductCatalog")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 5)
                        .withWriteCapacityUnits((long) 1))
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(tableKeySchema)
                .withGlobalSecondaryIndexes(gsi);

        CreateTableResult table = client.createTable(createTableRequest);
        System.out.println(table.getTableDescription());
    }

    private static void deleteAllProducts() {
        ProductRepo productRepo = new ProductRepo();
        List<Product> products = productRepo.getAllProducts();
        for (Product product : products){
            productRepo.deleteProduct(product);
        }
    }

    private static void populateDB() {
        for(int i = 0; i < PRODUCT_COUNT; i+=25) {
            List<Product> books = new ArrayList<>();
            for (int j = 0; j < 25; j++) {
                Book book = createRandomBook();
                books.add(book);
            }
            saveBookstoDB(books);
        }

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File file = new File("/users/icart/genrecountsnew.txt");
        // if file doesnt exists, then create it
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Genre counts: " + genreCounts[0] + "-" + genreCounts[1] + "-" +
                    genreCounts[2] + "-" + genreCounts[3] + "-" + genreCounts[4]);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void saveBooktoDB(Book book) {
        ProductRepo productRepo = new ProductRepo();
        productRepo.saveProduct(book);
    }

    private static void saveBookstoDB(List<Product> books) {
        ProductRepo productRepo = new ProductRepo();
        productRepo.saveProducts(books);
    }

    private static Book createRandomBook() {
        Random rnd = new Random();
        int langIndex = rnd.nextInt(LANGUAGES.length);
        String language = LANGUAGES[langIndex];

        int genreIndex = rnd.nextInt(GENRES.length);
        String genre = GENRES[genreIndex];
        int genreCount = ++genreCounts[genreIndex];

        int priceIndex = rnd.nextInt(PRICES.length);
        double price = PRICES[priceIndex];

        Book book = new Book();
        book.setName(genre + " Book " + genreCount);
        book.setDescription("A very good book");
        book.setFeatures("Pages, chapters, words, ideas");
        book.setImage("https://localhost:8443/gs-rest-service-0.1.0/img/file-roller.png");
        book.setPrice(price);
        book.setLanguage(language);
        book.setGenre(genre);

        return book;
    }
}
