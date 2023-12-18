# Fordsworth
## Getting Started
- Make sure you have installed PostgreSQL, Quarkus, and Maven.
- If you don't have them you can install them from the following links:
1. Java Development Kit (JDK) 11 or later: https://adoptopenjdk.net/
2. Apache Maven: https://maven.apache.org/install.html
3. PostgreSQL: https://www.postgresql.org/download/

## Setup
- open the project with IDE of your choice e.g. VS Code and IntelliJ etc.
- Before running the project open the resource folder -> application.properties and configure depending on your Postgres DB  details.
- Open PgAdmin and create a Database Called db.

## Run
- run the project using the command: quarkus:dev or mvn compile quarkus:dev
- the project should listen to port 8080
- For Get Navigate to: http://localhost:8080/persons

