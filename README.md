# apigw-lambda-dynamodb-demo

## Prerequisites

- Maven 3.8.5
- Java 17

## Steps to get started quickly

1. `mvn clean package shade:shade` in the root folder
2. `docker compose up` also in the root -- the init hooks should take care of creating all the resources
3. (From a different terminal tab)

#### To post a Person object
```
curl --location 'http://id12345.execute-api.localhost.localstack.cloud:4566/dev/personApi' \
                                    --header 'Content-Type: application/json' \
                                    --data '{
                                        "id":"123456",
                                        "name":"John Doe",
                                        "birthdate": "1979-10-21"
                                         }'
```

#### To get a Person object
```
curl --location 'http://id12345.execute-api.localhost.localstack.cloud:4566/dev/personApi?personId=123456'

```

