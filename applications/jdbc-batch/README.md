 Install oracle JDBC driver
 
 
 ```
 mvn install:install-file -Dfile=lib/ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dpackaging=jar
```


    mvn clean flyway:migrate