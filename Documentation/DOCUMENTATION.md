### DATABASE LAYER

- We use a Data Access Object (DAO) interface that is implemented by a service.
- We interact with our database using JDBC. Eventually, I'll want to switch to JPA since it provides more features, is more commonly used, and is not SQL-dialect-specific.
- We use **flyway** to track our database schema changes over time
  - This requires a flyway configuration file flyway.conf for our database
  - We can view the evolution of our schema in src/main/resources/db/migration
    - If you want to make a change to the schema, you'll need to add a file to this folder defining the changes you're making

### API Layer
http://localhost:8080/swagger-ui/index.html




