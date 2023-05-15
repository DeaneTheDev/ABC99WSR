# Weather Sensor REST Application

### Prerequisites
- Maven and Java (17) installed
- Clone the code to your local machine

*It might be possible to use a Maven Wrapper instead of installing*

### Running the Application
1. Open terminal or cmd and run the following command 
```
mvn clean install
```
2. Now run the following command
```
mvn spring-boot:run
```

*The application should now be running on port 8081*

## Requests
### Creating a Sensor


```
Post : http://localhost:8081/sensor
```
The body should be in Json format
```
{
  "title":"NorthSensor"
}
```

*A sensor's title must be between 1 and 25 characters in length, the ID will be automatically generated*

### Adding a Weather Report to a Sensor

```
Post : http://localhost:8081/weather/{sensor_identifier}
```
The default sensor_identifier is title, if you wish to use the Sensor's ID do the following
```
Post : http://localhost:8081/weather/{id}?identifier=id
```
The body should look like this:
```
{
  "temperature":50,
  "humidity":60,
  "windSpeed":70
}
```
**Note** *Only one stat is required per request, for example temperature is valid to be sent on its own.*

**Only realistic values will be acceptede for each**

### Getting Sensor information and metrics

To get details about all sensors with all data

```
Get : http://localhost:8081/sensor
```
To get details about specific sensors with all data using title

```
Get : http://localhost:8081/sensor?title={sensor1title},{sensor2title}
```
To get details about specific sensors with all data using id

```
Get : http://localhost:8081/sensor?id={sensor1id},{sensor2id}
```
*A combination of both id and title can be used seperated by &*

To specify which statistic you want to receive (options include: AVERAGE, MIN, MAX, SUM) *Average is default*

```
Get : http://localhost:8081/sensor?stat=MIN
```

To specify a date range, note format must be yyyy-MM-dd *both startDate and endDate must be included*

```
Get : http://localhost:8081/sensor?startDate=2023-01-02&endDate=2023-01-09
```

*A combination of all the above can be used to get specific results*

______________________________

*Any questions of issue please let me know*
