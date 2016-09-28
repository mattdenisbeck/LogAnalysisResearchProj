package models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import java.io.Serializable;

@DynamoDBDocument
public class Customer implements Serializable {

    private String fName, lName, street, city, state, zip,
            phone, email, ccType;

    public Customer(){}

    public Customer(String fName, String lName, String street, String city,
                    String state, String zip, String phone, String email, String ccType){
        this.fName = fName;
        this.lName = lName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
        this.ccType = ccType;
    }

    @DynamoDBAttribute(attributeName = "FirstName")
    public String getfName() { return fName; }
    public void setfName(String fName) { this.fName = fName; }

    @DynamoDBAttribute(attributeName = "LastNAme")
    public String getlName() { return lName; }
    public void setlName(String lName) { this.lName = lName; }

    @DynamoDBAttribute(attributeName = "Street")
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    @DynamoDBAttribute(attributeName = "City")
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    @DynamoDBAttribute(attributeName = "State")
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    @DynamoDBAttribute(attributeName = "Zip")
    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    @DynamoDBAttribute(attributeName = "Phone")
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @DynamoDBAttribute(attributeName = "Email")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @DynamoDBAttribute(attributeName = "CCType")
    public String getCcType() { return ccType; }
    public void setCcType(String ccType) { this.ccType = ccType; }
}
