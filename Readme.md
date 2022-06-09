# Sample Project of Spring boot reactive

## Main functions

1. HTTP request handle by [Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
2. Accessing MySQL by [R2DBC](https://r2dbc.io/)
3. Access control implemented by Spring Web Security.
4. Using Redis for Spring Session storage.

## How to run

1. Fill in MySQL and Redis connection properties.
2. Build jar package
```shell
mvn clean package
```
3. Run jar by java
```shell
java -jar ./target/webflux-0.0.1-SNAPSHOT.jar
```