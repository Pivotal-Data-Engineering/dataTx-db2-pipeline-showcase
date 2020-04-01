
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

Set the following environment variables 

```shell script
export spring_datasource_driverClassName=com.ibm.db2.jcc.DB2Driver
export spring_datasource_url=jdbc:db2://localhost:50000/testdb
export spring_jpa_properties_hibernate_dialect=org.hibernate.dialect.DB2Dialect 
export spring_jpa_properties_hibernate_dialect=org.hibernate.dialect.DB2Dialect 
export spring_jpa_generate_ddl=true
export spring_datasource_username=db2inst1
export spring_datasource_password=<password>

```


spring_jpa_hibernate_ddl-auto=update
spring_jpa_generate-ddl=true