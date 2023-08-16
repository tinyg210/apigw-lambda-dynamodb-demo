package lambda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Person {

  private String id;
  private String name;
  private Date birthdate;

  public Person() {
  }

  public Person(String person) {
    Gson gson = new Gson();
    Person input = gson.fromJson(person, Person.class);
    this.id = input.getId();
    this.name = input.getName();
    this.birthdate = input.getBirthdate();
  }

  public String toString() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(this);
  }

  @DynamoDbPartitionKey
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @DynamoDbConvertedBy(PersonBirthdateConverter.class)
  public Date getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(Date birthdate) {
    this.birthdate = birthdate;
  }


  public void setBirthdateFromString(String birthdateStr) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    this.birthdate = sdf.parse(birthdateStr);
  }


//  @Override
//  public String toString() {
//    return "Person{" +
//        "id='" + id + '\'' +
//        ", name='" + name + '\'' +
//        ", birthdate=" + birthdate +
//        '}';
//  }
}
