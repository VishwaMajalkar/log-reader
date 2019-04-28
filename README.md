# Coding Task Summary

Our custom-build server logs different events to a file. Every event has 2 entries in log - one entry when the event was started and another when
the event was finished. The entries in log file have no specific order (it can happen that finish event is logged before event start)
Every line in the file is a JSON object containing event data:
* id - the unique event identifier
* state - whether the event was started or finished (can have values "STARTED" or "FINISHED")
* timestamp - the timestamp of the event in milliseconds

Application Server logs also have the additional attributes:
* type - type of log
* host - hostname

# How to run solution

How to run spring boot application
```
./gradlew bootRun -Pargs=sample.json
```
For bulk json file load
```
./gradlew bootRun -Pargs=bulk_record.json
```
To Generate Bulk Json file - Please run JsonFileGenerator Main

# Test Result Verified
Json File Size:11MB, Processing of 100000 Log lines took 19sec

# Prerequisite For MySQL Server
* Please make sure local MySQL Server is up and running
* For database connection properties please update DBConnector Java File
* Application is loading bulk data from CSV to Event table, please check if "local_in" file option enabled or not by running below query on MySQL.

  SHOW VARIABLES LIKE 'local_infile';

  If query result is "OFF", then run below query to enable it.

  SET GLOBAL local_infile = 1;

