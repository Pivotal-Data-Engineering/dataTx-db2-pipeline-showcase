
# A TDD Approach to Data Transformation of Legacy RDBMS

This project provides a test driven development approach to building
data domains where the source system of record is a legacy Relation Database
Management System.

The initial code will focus on mainframe based DB2 migration.
The pattern will be applicable to similar solutions using 
Oracle, Sybase and other similar traditional relational databases.

The following are the highlighted technologies;

- Apache Geode
- Spring Data Geode
- Spring Data/JDBC
- Kakfa
- Spring Cloud Stream
- Spring Task/Spring Batch
- Spring Cloud DataFlow

# Database setup

```postgres-sql
CREATE TABLE ACCOUNT
(

   ACCOUNT_NBR varchar(100),
   LOC_ADDR1 varchar(1000),
   LOC_ADDR2 varchar(1000)
);
CREATE UNIQUE INDEX ACCOUNT_PK ON "DB2INST1".ACCOUNT(ACCOUNT_NBR);

insert into account(ACCOUNT_NBR,
LOC_ADDR1,
LOC_ADDR2)
values ('1','loc1','loc2');
```


## Running Embedded GemFire

The key to running GemFire cluster in embedded mode is to not mix client and server 
Spring Data Geode configuration in the same profile.
  
In the following configuration, this client Geode configuration is only loaded when the 
default Spring profile is used.
  
Integration and unit test can exclude the default profile with 
a expected external Geode cluster in order to use and embedded version.
   
```java
@Configuration
@EnableBatchProcessing()
@ClientCacheApplication
@EnableSecurity
@EnableStatistics
@EnableLogging
@EnablePdx
@Profile("default")
public class GeodeClientConfig
{
    //.....
}
   ```

The **@ClientCacheApplication** annotation marks the application as a Geode client once the annotation is loaded.
The **@Profile("default")** annotation will inform Spring to only load this configuration with the default
profile.



## Running the application

```shell script
export spring_datasource_driverClassName=com.ibm.db2.jcc.DB2Driver
export spring_datasource_url=jdbc:db2://localhost:50000/testdb
export spring_jpa_properties_hibernate_dialect=org.hibernate.dialect.DB2Dialect 
export spring_jpa_properties_hibernate_dialect=org.hibernate.dialect.DB2Dialect 
export spring_jpa_generate_ddl=true
export spring_datasource_username=db2inst1
export spring_datasource_password=<password>

```

    java -jar target/db2-geode-batch-0.0.1-SNAPSHOT.jar
