package lambda;

import java.net.URI;
import org.json.simple.parser.JSONParser;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class PersonApi {

  protected static final String LOCALSTACK_HOSTNAME = System.getenv("LOCALSTACK_HOSTNAME");
  // credentials that can be replaced with real AWS values
  private static final String ACCESS_KEY = "test";
  private static final String SECRET_KEY = "test";

  protected static String TABLE_NAME = "person";

  protected JSONParser parser = new JSONParser();

  private static AwsCredentialsProvider credentials = StaticCredentialsProvider.create(
      AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY));

  // create the dynamoDB client using the credentials and specific region
  private static Region region = Region.US_EAST_1;

  // create a dynamoDB client
  protected static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
      .region(region)
      .credentialsProvider(
          credentials)
      .endpointOverride(URI.create("http://localhost.localstack.cloud:4566"))
      .build();
}
