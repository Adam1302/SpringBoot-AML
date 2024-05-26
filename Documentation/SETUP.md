### Setup Instructions
Set up or re-run docker container:
- If you've never created the container on your machine:
  - use statement: **docker run --name postgres-spring -e POSTGRES_PASSWORD=[yourPassword] -d -p 5432:5432 postgres:alpine**
- Prepare the port using command: **docker port postgres-spring**
- To list exited containers: **docker ps -a**
- Using the id of the desired container, you can start it: **docker start ---container_id---**
- Using the id of the desired container, yoi can execute the app: **docker exec -it ---container_id--- bin/bash**
- To connect to Postgres: **psql -U postgres**

Setup and modify database within Postgres (with UUID support):
- **CREATE DATABASE ---db-name---;**
- **\c ---db-name---;**
- **CREATE EXTENSION "uuid-ossp";**
- **CREATE EXTENSION citext;**
- **SELECT uuid_generate_v4();**

Within this project:
- Change the password in application.yml to [yourPassword]
- Add a file in the project root called "**flyway.conf**" with information in the following format:
  flyway.user=postgres
  flyway.password=[yourPassword]
  flyway.schemas=public
  flyway.url=jdbc:postgresql://localhost:5432/demodb
  flyway.locations=filesystem:db/migration

Within Postgres (with db already setup):
- To list existing databases: **\l**
- View table: **\d ---table-name---**


### Run Instructions

After everything is setup, we can test in Postman:
- Open the project in an IDE (I've used IntelliJ) and run
- In Postman:
  - Send requests to **localhost:8080/api/v1/book**
  - Format is **json**
- To send requests to this API (how I do it):
  - Use Postman to request to localhost:8080/api/v1/book
  - Use JSON Format:
    {
    "work_title": "Poetics",
    "primary_author": "Aristotle",
    "year_published": -330,
    "word_count": 11000
    }
    */

Clone frontend: https://github.com/Adam1302/SpringBoot-AML-frontend
- Ensure backend is running
- Then, from within the frontend repo, run:
  - npm install
  - npm run dev

