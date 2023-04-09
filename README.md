# Little Social Network
## General info
Web applications with functions: sending private messages, adding posts with description and pictures, likes system, followers systems, comment system for posts.
### Deployed Web application
#### Signin and Signup pages
![image](https://github.com/sm1l43s/Little_Social_Network/blob/master/src/main/resources/img/github/img.png)
![image](https://github.com/sm1l43s/Little_Social_Network/blob/master/src/main/resources/img/github/img_1.png)
#### Wall posts and comments
![image](https://github.com/sm1l43s/Little_Social_Network/blob/master/src/main/resources/img/github/img_2.png)
![image](https://github.com/sm1l43s/Little_Social_Network/blob/master/src/main/resources/img/github/img_3.png)
![image](https://github.com/sm1l43s/Little_Social_Network/blob/master/src/main/resources/img/github/img_4.png)
#### User profile
![image](https://github.com/sm1l43s/Little_Social_Network/blob/master/src/main/resources/img/github/img_5.png)
![image](https://github.com/sm1l43s/Little_Social_Network/blob/master/src/main/resources/img/github/img_8.png)
#### Chat using Websockets
![image](https://github.com/sm1l43s/Little_Social_Network/blob/master/src/main/resources/img/github/img_6.png)
![image](https://github.com/sm1l43s/Little_Social_Network/blob/master/src/main/resources/img/github/img_7.png)

## Technologies
* Java - version 8
* Spring Boot - version 2.3.4
* Spring Data JPA - version 2.3.4
* Spring Security - version 2.3.4
* MySQL - version 5.7
* Project Lombok
* Thymeleaf
* GSON

## Setup and Installation

1. #### Download or clone the repository from GitHub
```
git clone https://github.com/sm1l43s/Little_Social_Network.git
cd little_social_network
```
2. #### Install required programs

In order to follow along user needs to have MySQL.
<br>
Bellow are short terminal lines for easy installation for Linux systems.
```
sudo apt update
sudo apt install mysql-server
```
3. #### (Optional) Update database configurations in application.properties
If you have changed defualt user for creating database with some different username and password, update the src/main/resources/application.properties file accordingly:
```
spring.jpa.hibernate.ddl-auto=update #for first time running MUST be set to create, for every consecutive time set to update (if you care to have permanent database, otherwise it is deleted after every consecutive jar run)
spring.datasource.url=jdbc:mysql://localhost/social_network
spring.datasource.username=root
spring.datasource.password=root
```

4. #### Run the spring boot application
If you download/clone repo elsewhere, change path update accordingly
```
cd little_social_network
mvn clean install
java -jar target/social_network-0.0.1.jar
```
this runs at port 8080 and hence all endpoints can be accessed starting from http://localhost:8080

6. #### Create database objects (If you want some prerecorded values in local database)
