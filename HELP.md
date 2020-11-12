# Instruction to run the project
If you don't have running instance of postgreSQL, then you can start it up by using the following command:

* docker-compose up -d

Docker-compose will start up two application:

* PostgreSQL
* Adminer (on 8080 port. You can use Adminer for easier administration of PostgreSQL database)

With a properly configured instance of PostgreSQL you should be able to start up the application by the following command:
* ./gradlew bootRun

By default applications uses 8081 port.

#Test the project
For testing purposes you can use provided json configuration for Postman.
The name of the json configuration is 'cityService.postman.json'.

Also, there is integration test 'CityServiceITest' which you can modify for using it with your own input data.