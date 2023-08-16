package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class CreatePerson extends PersonApi implements RequestStreamHandler {

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
      throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    JSONObject responseJson = new JSONObject();
    try {
      JSONObject event = (JSONObject) parser.parse(reader);

      if (event.get("body") != null) {
        Person person = new Person((String) event.get("body"));

        addEntryToDynamoDB(person);
        create200Response(responseJson, person);

      }

    } catch (ParseException exception) {
      create400Response(responseJson, exception);
    }

    OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
    writer.write(responseJson.toString());
    writer.close();
  }

  private static void addEntryToDynamoDB(Person person) {
    try {
      DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
          .dynamoDbClient(dynamoDbClient)
          .build();
      // use the enhanced client to interact with the table
      DynamoDbTable<Person> table = enhancedClient.table(TABLE_NAME,
          TableSchema.fromBean(Person.class));

      table.putItem(person);

      System.out.println("Entry added successfully!");
    } catch (DynamoDbException exception) {
      System.out.println("An error occurred: " + exception.getMessage());
    }
  }

  private void create200Response(JSONObject responseJson, Person person) {
    JSONObject responseBody = new JSONObject();
    responseBody.put("message", "New person has been added.");
    responseBody.put("savedEntity", person.toString());

    JSONObject headerJson = new JSONObject();

    responseJson.put("statusCode", 200);
    responseJson.put("headers", headerJson);
    responseJson.put("body", responseBody.toString());
  }

  private void create400Response(JSONObject responseJson, Exception exception) {
    JSONObject responseBody = new JSONObject();
    responseBody.put("message", "Exception occurred.");
    responseBody.put("exception", exception.getMessage());

    JSONObject headerJson = new JSONObject();

    responseJson.put("statusCode", 400);
    responseJson.put("headers", headerJson);
    responseJson.put("body", responseBody.toString());

  }
}
