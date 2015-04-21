netty-client-server
=======

My pet project.  
This is a prototype game. Consist of two parts: client and server. `Client` performs requests to server and receive response. `Server` listerns soket and waiting to recieve a request. Powered by: `Java SE`, `Netty Framework` over HTTP, `MongoDB` and `JSON` like a protocol for messages communication. Netty supports SEDA like highly customizable thread model: single thread, one or more thread pools and queue between them.
  
  
## Rules:  

  * `Client` creates message with text `ping` and sends Http Request to server by POST method; example:`{ "action": "ping"}`.  
  * `Server` receives request and should creates Http Response to back client with message `pong N`; _N_ is a quantity of requests from current client; example: `{"action":"pong","content":"pong 4","status":"200 OK"}`.  
  * `Server` might have high load from huge number of clients.  
  * `Client`: perform requests to server.
  * `HTTP cookie` is a main opportunity for server to know all clients: new and old. It produces hash code over all cookies (key:value) and this integer value is a key for making a decision.
```sh
Example of JSON document:  {"coockie_hash" : 77737217 , "quantity" : 2}

Example of FindAndModify query:  {query: {"coockie_hash" : 77737217} , sort: {"coockie_hash" : 1}, update: {$inc: {"quantity" : 1}}, new: true, upset: true}
```  
  * `Ping Pong Server` can handle some URLs:
    * [http://localhost/author](http://localhost/author) - OK  (POST Method; a little bit information about author)
    * [http://localhost/handler](http://localhost/handler) - OK (POST Method)
    * [http://localhost/any_unknown_uri](http://localhost/any_unknown_uri) - BAD (GET or POST Methods)
  
  
## Requirements:

  * Java SE Development Kit 7 (or newer)  
  * Apache Maven 3.x  
  * Netty 4.0.27.Final (boundle: netty/all in one)  
  * MongoDB 2.6.x (older versions might be unsupported by Java MongoDB Driver)  
  * Git 1.7.x (or newer)  

## Project configuration:  

##### MongoDB configuration

  * Configuration File  
    * MongoDB need a folders (data and log directory) to store its data. By default, it will store in `/data/db/` and `/data/log/`, create those folders manually. MongoDB won't create it for you.
    * Create a file `mongo.config` into `/bin` directory, it’s just a YAML file:  
```yaml 
--- #MongoDB configuration file
net:
   bindIp: 127.0.0.1
   port: 27017
storage:
    dbPath: "../data/"
    journal:
            enabled: true
    smallFiles: true
systemLog:
    destination: file
    path: "../log/mongod.log"
    
```   
    
  * Start MongoDB server:  
```sh 
mongodb/bin>mongod --config ./mongo.config
```   
    
  * Connect to the started MongoDB server:  
```sh
mongodb/bin>mongo
```   
    
  * Project uses db `pingpong` for all actions:
```sh
> use pingpong
switched to db pingpong

> db
pingpong
```   
    
  * Creates a user `pingponguser` for the database `pingpong` where the method runs.
```sh
> db.createUser({user: "pingponguser", pwd: "pingponguser", roles: ["readWrite", "dbAdmin"]})
Successfully added user: { "user" : "pingponguser", "roles" : [ "readWrite", "dbAdmin" ] }

> show dbs
admin     0.031GB
local     0.031GB
pingpong  (empty)
```     
    
  *  Creates first document which structure will be used:
```sh
> db.pingpong.insert({"coockie_hash": 48, "quantity": 0})
WriteResult({ "nInserted" : 1 })

> db.pingpong.find()
{ "_id" : ObjectId("552fcaadcebf0f9b1ae94ca4"), "coockie_hash" : 48, "coockie_source" : "0", "quantity" : 0 }
```   
    
  *  Creates an ascending index on the field `coockie_hash`
```sh 
> db.pingpong.createIndex({"coockie_hash": 1}, {"unique": true, "sparse": true})

{
        "createdCollectionAutomatically" : false,
        "numIndexesBefore" : 1,
        "numIndexesAfter" : 2,
        "ok" : 1
}

> db.pingpong.getIndexes()
[
        {
                "v" : 1,
                "key" : {
                        "_id" : 1
                },
                "name" : "_id_",
                "ns" : "pingpong.pingpong"
        },
        {
                "v" : 1,
                "unique" : true,
                "key" : {
                        "coockie_hash" : 1
                },
                "name" : "coockie_hash_1",
                "ns" : "pingpong.pingpong",
                "sparse" : true
        }
]

```     

---  
  
##### Server and Client configuration  
  
  *  Go to the `Config` configuration file `/netty-client-server/config/src/main/resources/config.xml` to make some changes if you need it:
```xml  
<?xml version="1.0" encoding="UTF-8" ?>
<config>
    <connect>
        <host>127.0.0.1</host>
        <port>80</port>
        <uri>/handler</uri>
    </connect>

    <cookie>
        <value>name1=value1; name2=value2</value>
    </cookie>

    <bind>
        <host>127.0.0.1</host>
        <port>80</port>
    </bind>

    <database>
        <host>127.0.0.1</host>
        <port>27017</port>
        <user>pingponguser</user>
        <password>pingponguser</password>
        <name>pingpong</name>
    </database>
</config>

```   
  

---  
  
##### Maven 
  *  Build project. Go to the root path `/netty-client-server/` of the project and run:  
```sh
netty-client-server>mvn clean package

...<cut>...

[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] client-server ...................................... SUCCESS [  0.916 s]
[INFO] config ............................................. SUCCESS [  1.579 s]
[INFO] client ............................................. SUCCESS [  1.119 s]
[INFO] server ............................................. SUCCESS [  0.764 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 4.585 s
[INFO] Finished at: 2015-04-21T07:44:19+03:00
[INFO] Final Memory: 19M/46M
[INFO] ------------------------------------------------------------------------

```  
   
  *  Run tests. All tests are disabled by default. Was created a separate profile "test-server" for enabling tests into submodule of `Server`.  Go to the root path `/netty-client-server/` of the project and run:  
```sh
netty-client-server>mvn clean package -P test-server  

... <cut> ...

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running ru.shishmakov.server.TestCookie
INFO  - TestCookie           - Running test "testHashCode" (TestBase.java:30)
INFO  - TestCookie           - Cookie 1: name=name1; value=value1 (TestCookie.java:32)
INFO  - TestCookie           - Cookie 2: name=name2; value=value2 (TestCookie.java:33)
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.154 sec - in ru.shishmakov.server.TestCookie
Running ru.shishmakov.server.TestHttpRequest
WARN  - Config               - Initialise configuration ... (Config.java:36)
WARN  - Config               - Configuration loaded from jar:file:/D:/work/github/netty-client-server/config/target/config-1.0-SNAPSHOT.jar!/config.xml (Config.java:27)
INFO  - TestHttpRequest      - Running test "testNotAllowedMethod" (TestBase.java:30)
INFO  - HttpServerProcessorHandler - client localAddress: /127.0.0.1:80 (HttpServerProcessorHandler.java:60)
INFO  - HttpServerProcessorHandler - client remoteAddress: /127.0.0.1:57211 (HttpServerProcessorHandler.java:61)
INFO  - HttpServerProcessorHandler - client cookie: name1=value1 (HttpServerProcessorHandler.java:63)
INFO  - HttpServerProcessorHandler - client cookie: name2=value2 (HttpServerProcessorHandler.java:63)
INFO  - HttpServerProcessorHandler - // ---------------- end client  (HttpServerProcessorHandler.java:67)
INFO  - TestHttpRequest      - Expected result: {"action":"error","content":"Ping Pong server failure","status":"405 Method Not Allowed"} (TestHttpRequest.java:79)
INFO  - TestHttpRequest      - Actual result: {"action":"error","content":"Ping Pong server failure","status":"405 Method Not Allowed"} (TestHttpRequest.java:80)
INFO  - TestHttpRequest      - Running test "testEmptyProtocolBody" (TestBase.java:30)
INFO  - HttpServerProcessorHandler - client localAddress: /127.0.0.1:80 (HttpServerProcessorHandler.java:60)
INFO  - HttpServerProcessorHandler - client remoteAddress: /127.0.0.1:57260 (HttpServerProcessorHandler.java:61)
INFO  - HttpServerProcessorHandler - client cookie: name1=value1 (HttpServerProcessorHandler.java:63)
INFO  - HttpServerProcessorHandler - client cookie: name2=value2 (HttpServerProcessorHandler.java:63)
INFO  - HttpServerProcessorHandler - // ---------------- end client  (HttpServerProcessorHandler.java:67)
INFO  - TestHttpRequest      - Expected result: {"action":"error","content":"Ping Pong server can not parse protocol of the request","status":"400 Bad Request"} (TestHttpRequest.java:102)
INFO  - TestHttpRequest      - Actual result: {"action":"error","content":"Ping Pong server can not parse protocol of the request","status":"400 Bad Request"} (TestHttpRequest.java:103)
INFO  - TestHttpRequest      - Running test "testBadProtocolBody" (TestBase.java:30)
INFO  - HttpServerProcessorHandler - client localAddress: /127.0.0.1:80 (HttpServerProcessorHandler.java:60)
INFO  - HttpServerProcessorHandler - client remoteAddress: /127.0.0.1:57309 (HttpServerProcessorHandler.java:61)
INFO  - HttpServerProcessorHandler - client cookie: name1=value1 (HttpServerProcessorHandler.java:63)
INFO  - HttpServerProcessorHandler - client cookie: name2=value2 (HttpServerProcessorHandler.java:63)
INFO  - HttpServerProcessorHandler - client uri: /handler data: {"altron":"ping"} (HttpServerProcessorHandler.java:89)
INFO  - HttpServerProcessorHandler - // ---------------- end client  (HttpServerProcessorHandler.java:67)
INFO  - TestHttpRequest      - Expected result: {"action":"error","content":"Ping Pong server can not parse protocol of the request","status":"400 Bad Request"} (TestHttpRequest.java:126)
INFO  - TestHttpRequest      - Actual result: {"action":"error","content":"Ping Pong server can not parse protocol of the request","status":"400 Bad Request"} (TestHttpRequest.java:127)
INFO  - TestHttpRequest      - Running test "testAuthorRequest" (TestBase.java:30)
INFO  - HttpServerProcessorHandler - client localAddress: /127.0.0.1:80 (HttpServerProcessorHandler.java:60)
INFO  - HttpServerProcessorHandler - client remoteAddress: /127.0.0.1:57358 (HttpServerProcessorHandler.java:61)
INFO  - HttpServerProcessorHandler - client cookie: name1=value1 (HttpServerProcessorHandler.java:63)
INFO  - HttpServerProcessorHandler - client cookie: name2=value2 (HttpServerProcessorHandler.java:63)
INFO  - HttpServerProcessorHandler - client uri: /author data: {"action":"ping"} (HttpServerProcessorHandler.java:89)
INFO  - HttpServerProcessorHandler - // ---------------- end client  (HttpServerProcessorHandler.java:67)
INFO  - TestHttpRequest      - Expected result: {"action":"author","content":"Dmitriy Shishmakov, https://github.com/DmitriySh","status":"200 OK"} (TestHttpRequest.java:150)
INFO  - TestHttpRequest      - Actual result: {"action":"author","content":"Dmitriy Shishmakov, https://github.com/DmitriySh","status":"200 OK"} (TestHttpRequest.java:151)
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.666 sec - in ru.shishmakov.server.TestHttpRequest

Results :

Tests run: 5, Failures: 0, Errors: 0, Skipped: 0

... <cut> ...

```  
  
   
---  

## Run
  *  Go to the `Server` submodule path `/netty-client-server/server/target/` and run:
```sh
netty-client-server/server/target>java -jar server-1.0-SNAPSHOT.jar

WARN  - Config                              - Initialise configuration ... (Config.java:32)
WARN  - Config                              - Configuration loaded from jar:file: ... server/target/server-1.0-SNAPSHOT.jar!/config.xml (Config.java:23)
WARN  - Database                            - Initialise connection to MongoDB ...  (Database.java:32)
WARN  - Database                            - Connected to MongoDB on 127.0.0.1:27017 (Database.java:48)
WARN  - Server                              - Initialise server ... (Server.java:38)
WARN  - Server                              - Start the server: Server. Listen on: /127.0.0.1:80 (Server.java:50)

```
  *  Go to the `Client` submodule path `/netty-client-server/client/target/` and run:  
```sh
netty-client-server/client/target>java -jar client-1.0-SNAPSHOT.jar

WARN  - Config                              - Initialise configuration ... (Config.java:32)
WARN  - Config                              - Configuration loaded from jar:file: ... client/target/client-1.0-SNAPSHOT.jar!/config.xml (Config.java:23)
WARN  - Client                              - Initialise client ... (Client.java:43)
WARN  - Client                              - Start the client: Client. Listen on local address: /127.0.0.1:63869; remote address: /127.0.0.1:80 (Client.java:55)
INFO  - Client                              - Send HTTP request: POST /handler HTTP/1.1; content: {"action":"ping"} (Client.java:58)
INFO  - HttpClientProcessorHandler          - Receive HTTP response: HTTP/1.1 200 OK; content: {"action":"pong","content":"pong 7","status":"200 OK"} (HttpClientProcessorHandler.java:30)
WARN  - Client                              - Client to close the connection: Client (Client.java:61)

```  
  
  * `Server` log:
```sh
INFO  - HttpServerProcessorHandler          - client localAddress: /127.0.0.1:80 (HttpServerProcessorHandler.java:60)
INFO  - HttpServerProcessorHandler          - client remoteAddress: /127.0.0.1:63869 (HttpServerProcessorHandler.java:61)
INFO  - HttpServerProcessorHandler          - client cookie: name1=value1 (HttpServerProcessorHandler.java:63)
INFO  - HttpServerProcessorHandler          - client cookie: name2=value2 (HttpServerProcessorHandler.java:63)
INFO  - HttpServerProcessorHandler          - client uri: /handler data: {"action":"ping"} (HttpServerProcessorHandler.java:89)
INFO  - HttpServerProcessorHandler          - // ---------------- end client  (HttpServerProcessorHandler.java:67)

```
  
  





  
