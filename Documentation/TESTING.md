# Testing

We're using JUnit5

To test our repository layer, we don't want to use our actual database. Instead, we use an in-memory database, H2: https://www.baeldung.com/spring-boot-h2-database

We use Mockito for mocking
- Why do we mock?
  - When we test, we want to limit our scope to the logic of whatever class we're testing. However, often times, our classes will call methods from another class (ex. we call Repository-layer methods from Service classes). In such cases, we have a problem: our service-level tests will be checking the logic of BOTH of service and repository layers
  - Enter MOCKing:
    - Mocking allows us to simulate an object and limit its return values without actually running any of its logic
    - So, with mocking, we can control what values are returned and what actions are taken when a mocked class method is called
