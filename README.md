# ZAD - Software Engineer Test Case

## Tech Stack

- **Java 21**
- **Spring Boot 3.5.3** with:
  - Web 
  - JPA, Hibernate,
  - Kafka
  - Modulith
  - Testcontainers
  - Postgres
  - flyway
  - openapi
  - Mapstruct
  - Shedlock

## How to run

On the project folder:

- `./mvnw clean package -DskipTests`
- `docker build -t my-springboot-app .`
- `docker compose up`

Then open the following link:
- `http://localhost:8080/swagger-ui/index.html`

## How to run test cases

- Test cases heavily rely on testcontainers. To run test cases make sure that you are running docker:

```bash
fedora:~$ docker --version
Docker version 28.2.2, build e6534b4
```
- Then run test cases with `mvn test`
  - Testcontainers will automatically runs required containers (postgres, kafka)
  - For user flow look at the `ZadUserAndAccountFlowTest`
  - For transaction(s) among user's account `TransactionServiceModuleTest`
  - For transaction(s) between different users `TransactionServiceExchangeModuleTest`

## Other Notes

- Spring modulith is a great tool to visualize modules in your application. And verify that none of the modules has circular reference to each other. You may find the app architecture inside `resources/static` folder
  - This module generates picture of the app when you run test case

- To handle transaction without getting any concurrency update issue and to handle transaction(s) in async manner, I have decided to use Kafka. 
  - After created transaction as waiting, TransactionStatusChangeListener runs and publish the event to the kafka
  - Then KafkaListener processes the given transaction
  - **To handle account's transaction one by one: I decided to use `account.getId()` as Kafka's key before publishing to the kafka. In that way I can handle the transaction for the specific account in an ordered manner**
  - To handle notification, I am using the same kafkaListener with different groupId

## Future improvements

- Depends on the usage, we may partition the transaction table per TransactionStatus. 
  - With that partition, we may speed up to process waiting transactions (i am assuming that waiting transaction should always be zero or close to zero)
- While creating the transaction, we may put the exchange information (with expiration time) into Transaction entity. Instead of searching while processing the transaction