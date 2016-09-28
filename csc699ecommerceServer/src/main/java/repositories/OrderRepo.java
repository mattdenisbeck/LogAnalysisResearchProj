package repositories;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import models.Order;

import java.util.List;

public class OrderRepo {

    //Uncomment when deployed to AWS
    //private final AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());

    //Comment out when deployed to AWS
    private final AmazonDynamoDBClient client = new AmazonDynamoDBClient().withEndpoint("http://localhost:8000");

    private final DynamoDBMapper mapper = new DynamoDBMapper(client);

    public OrderRepo() {}

    public void saveOrder(Order order){
        mapper.save(order);
    }

    public Order getOrder(String _id){
        Order order = new Order();
        order.setId(_id);
        DynamoDBQueryExpression<Order> queryExpression;
        queryExpression = new DynamoDBQueryExpression<Order>().withHashKeyValues(order);

        List<Order> orderList = mapper.query(Order.class, queryExpression);
        return orderList.get(0);
    }

    public void deleteOrder(String id){
        Table table = (new DynamoDB(client)).getTable("Orders");
        DeleteItemOutcome outcome = table.deleteItem("_id", id);
    }
}
