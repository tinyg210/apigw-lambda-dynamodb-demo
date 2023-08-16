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
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class GetPerson extends PersonApi implements RequestStreamHandler {

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
      throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    JSONObject responseJson = new JSONObject();
    String person = null;
    try {
      JSONObject event = (JSONObject) parser.parse(reader);
      JSONObject responseBody = new JSONObject();

      if (event.get("queryStringParameters") != null) {
        JSONObject qparam = (JSONObject) event.get("queryStringParameters");
        if (qparam.get("personId") != null) {
          String personId = (String)qparam.get("personId");
          person = readEntryFromDynamoDB(personId);
          System.out.println("PERSON" + person);

        }
      }
      if (person != null) {
        responseBody.put("personInfo", person);
        responseJson.put("statusCode", 200);
      } else {
        responseBody.put("message", "No item found");
        responseJson.put("statusCode", 404);
      }

      responseJson.put("body", responseBody.toString());

    } catch (ParseException pex) {
      responseJson.put("statusCode", 400);
      responseJson.put("exception", pex);
    }

    OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
    writer.write(responseJson.toString());
    writer.close();
  }


  private static String readEntryFromDynamoDB(String id) {
    try {
      DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
          .dynamoDbClient(dynamoDbClient)
          .build();
      DynamoDbTable<Person> table = enhancedClient.table(TABLE_NAME,
          TableSchema.fromBean(Person.class));

      Person person = table.getItem(Key.builder().partitionValue(id).build());

      if (person != null) {
        return person.toString();
      }
    } catch (DynamoDbException exception) {
      System.out.println("Something happened: " + exception.getMessage());
    }
    return null;
  }
}
