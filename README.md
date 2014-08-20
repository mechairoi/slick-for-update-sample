# FOR UPDATE on Slick 2.1.0 Lifted Embedding with PostgreSQL Driver

```
$ createdb test1
$ sbt run
[info] Running myapp.MyApp
SQL: Preparing statement: create table "SUPPLIERS" ("SUP_ID" INTEGER NOT NULL PRIMARY KEY,"SUP_NAME" VARCHAR(254) NOT NULL,"STREET" VARCHAR(254) NOT NULL,"CITY" VARCHAR(254) NOT NULL,"STATE" VARCHAR(254) NOT NULL,"ZIP" VARCHAR(254) NOT NULL)
SQL: Preparing statement: select x2."COF_NAME", x3."SUP_NAME" from "COFFEES" x2, "SUPPLIERS" x3 where (x2."PRICE" < 9.0) and (x3."SUP_ID" = x2."SUP_ID") for update of x2
[success] Total time: 1 s, completed 2014/08/20 19:12:10.
```

