[[overview]]

The samples-rest-mongo project is very light poc demonstrate for accessing data from mongodb, memorydb(h2):

### Environment Properties

"application-gen.yml" is the environment file.
~~~~
server:
  port: ${port:8080}
spring:
  profiles:
    active:  default
  session:
    store-type: hash_map
  datasource:
    driverClassName: org.h2.Driver
    url: jdbc:h2:mem:SAMPLES;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;
    name: SAMPLES
    username: sa
    password:
    validationQuery: SELECT 1
    platform: h2
    initialize: true
    schema: classpath:/schema-h2.sql
    data: classpath:/data-h2.sql
  jackson:
    serialization-inclusion: non_nul
    serialization.indent_output: true

  data:
    mongodb:
      host: 192.168.30.198
      port: 27017
      database: SAMPLES
# H2 Web Console
  h2:
    console:
      path: /h2-console
      enabled: true
      settings:
        web-allow-others: true
  jpa:
    hibernate:
      ddl-auto: none
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true
logging:
  pattern:
    console: "%d %-5level %logger : %msg%n"
  level:
    root: info
    org.springframework: info
    org.chiwooplatform.samples: debug

---
spring:
  profiles: dev

logging:
  config: classpath:logback-dev.xml

server:
  port: ${port:8082}}
~~~~


### H2 Web Console
You can manage data with query in H2 Web Console.
~~~
http://localhost:8080/h2-consol
~~~

ddl file is schema-h2.sql "schema-h2.sql" and initilize data file is "data-h2.sq" .

### Test Authentication
You can test authentication with cURL or Postman.
~~~
curl -X POST \
  http://localhost:8080/auth/token \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 76b66090-1965-0fb0-d2fa-a16ed46bf8b7' \
  -d '{
    "username" : "lamp.java@gmail.com",
    "password" : "qwer1234"
}'
~~~
