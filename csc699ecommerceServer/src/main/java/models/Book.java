package models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by icart on 6/22/16.
 */

@DynamoDBTable(tableName="ProductCatalog")
public class Book extends Product {

    private static final String DEFAULT_CATEGORY = "Book";

    public Book(){
        super();
        this.setCategory(DEFAULT_CATEGORY);
    }

    public Book(String _id, String name, String description, String features,
                String language, String genre, String image, double price){
        super(_id, name, DEFAULT_CATEGORY, description, features, language, genre, image, price);
    }
}
