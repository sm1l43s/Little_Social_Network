# Little Social Network
## General info
Web applications with functions: sending private messages, adding posts with description and pictures, likes system, followers systems, comment system for posts.
### Deployed Web application

## Technologies
* Java - version 11
* Spring Boot - version 2.7.8
* Spring Data JPA - version 2.7.8
* Spring Security - version 2.7.8
* PostgreSQL - version 15
* Apache Maven - version 3.9.1
* Spring doc Swagger UI
* Project Lombok
* Java JWT - version 4.2.

## Setup and Installation

1. #### Download or clone the repository from GitHub
```
git clone https://github.com/sm1l43s/movies.git
cd movies
```
2. #### Install required programs

In order to follow along user needs to have PostgreSQL and Postman (you can use Swagger UI insted of Postman).
<br>
Bellow are short terminal lines for easy installation for Linux systems.
```
sudo apt update
sudo snap install postman
sudo apt install postgresql postgresql-contrib
```
3. #### (Optional) Update database configurations in application.properties
If you have changed defualt user for creating database with some different username and password, update the src/main/resources/application.properties file accordingly:
```
spring.jpa.hibernate.ddl-auto=update #for first time running MUST be set to create, for every consecutive time set to update (if you care to have permanent database, otherwise it is deleted after every consecutive jar run)
spring.datasource.url=jdbc:postgresql://localhost/movies
spring.datasource.username=postgres
spring.datasource.password=postgres
```

4. #### Run the spring boot application
If you download/clone repo elsewhere, change path update accordingly
```
cd Documents/movies
mvn clean install
java -jar target/movies-1.0.jar
```
this runs at port 8080 and hence all endpoints can be accessed starting from http://localhost:8080

6. #### Create database objects (If you want some prerecorded values in local database)
