netty-client-server
=======

My pet project.  
This is a prototype game. Consist of two parts: client and server. `Client` performs requests to server and receive response. `Server` listerns soket and waiting to recieve a request. Powered by: `Java SE`, `Spring Data`, `Netty Framework` over HTTP, `MongoDB` and `JSON` like a protocol for messages communication. Netty supports SEDA like highly customizable thread model: single thread, one or more thread pools and queue between them.
  
  
## Rules:  

  * `Client` creates message with text `ping` and sends Http Request to server by POST method; example:`{ "action": "ping"}`.  
  * `Server` receives request and should creates Http Response to back client with message `pong N`; _N_ is a quantity of requests from current client; example: `{"action" : "pong", "content" : "pong 1", "profileid" : "8b939bb8-1faa-4d62-8b42-43d63774e1d0", "status" : "200 OK"}`.  
  * `Server` might have high load from huge number of clients.
  * `Client` perform requests to server.
  * `profileid` is a main possibility for server to know all clients: new and old. The type of `profileid` is an [UUID](https://docs.oracle.com/javase/7/docs/api/java/util/UUID.html) receives from client or generates on server side and sent back to client.
```sh
Server JSON document: 
       {
          "_id" : ObjectId("55510463b33e15e132620f4a"),
          "profileid" : BinData(3,"Yk2qH7ibk4vQ4XQ31kNCiw=="),
          "quantity" : 7
       }

Example of FindAndModify query: 
       {
          query: {"profileid" : BinData(3,"Yk2qH7ibk4vQ4XQ31kNCiw==")} ,
          update: {$inc: {"quantity" : 1}},
          new: true,
          upset: true
       }
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
    * MongoDB needs a folders (data and log directory) to store its data. By default, it will store in `/data/db/` and `/data/log/`, you should create those folders manually. MongoDB won't create it for you.
    * Create 2 new directories `/data/`, `/log/` near with `/bin/`.
    * Create a file `mongo.config` into `/bin/` directory, it’s just a YAML file:  
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
    

---  
  
##### Server and Client configuration  
  
  *  Go to the `Config` configuration file [`app.properties`](https://github.com/DmitriySh/netty-client-server/blob/develop/config/src/main/resources/app.properties) to make some changes if you need it:
```properties  

###################################  
# Configuration MongoDB  
###################################  
database.host=127.0.0.1
database.port=27017
database.user=pingponguser
database.password=pingponguser
database.name=pingpong
collection.name=profile

###################################  
# Configuration Ping Pong Server  
###################################  
bind.host=127.0.0.1
bind.port=80

###################################  
# Configuration Client  
###################################  
connect.host=127.0.0.1
connect.port=80
connect.uri=/handler
profile.id=8b939bb8-1faa-4d62-8b42-43d63774e1d0


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
[INFO] client-server ...................................... SUCCESS [  8.257 s]  
[INFO] config ............................................. SUCCESS [ 11.099 s]  
[INFO] client ............................................. SUCCESS [  8.244 s]  
[INFO] server ............................................. SUCCESS [  3.089 s]  
[INFO] ------------------------------------------------------------------------  
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------  
[INFO] Total time: 31.024 s
[INFO] Finished at: 2015-05-13T09:04:35+03:00
[INFO] Final Memory: 20M/54M
[INFO] ------------------------------------------------------------------------  

```  
   
  *  Build project and run tests. All tests are disabled by default. Was created a separate profile `test-server` for enabling tests into submodule of `Server`.  Go to the root path `/netty-client-server/` of the project and run:  
```sh
netty-client-server>mvn clean package -P test-server  

... <cut> ...


-------------------------------------------------------  
 T E S T S
-------------------------------------------------------  
Running ru.shishmakov.server.test.TestHttpRequest   
13.05.15 09:07:48 INFO  - TestHttpRequest      - Running test "testHttp200AuthorRequest" (TestBase.java:33)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Expected result: {"action":"author","content":"Dmitriy Shishmakov, https://github.com/DmitriySh","status":"200 OK"} (TestHttpRequest.java:160)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Actual result: {"action":"author","content":"Dmitriy Shishmakov, https://github.com/DmitriySh","status":"200 OK"} (TestHttpRequest.java:161)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Running test "testHttp400BadProtocolBody" (TestBase.java:33)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Expected result: {"action":"error","content":"Ping Pong server can not parse protocol of the request","status":"400 Bad Request"} (TestHttpRequest.java:136)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Actual result: {"action":"error","content":"Ping Pong server can not parse protocol of the request","status":"400 Bad Request"} (TestHttpRequest.java:137)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Running test "testHttp400EmptyProtocolBody" (TestBase.java:33)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Expected result: {"action":"error","content":"Ping Pong server can not parse protocol of the request","status":"400 Bad Request"} (TestHttpRequest.java:112)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Actual result: {"action":"error","content":"Ping Pong server can not parse protocol of the request","status":"400 Bad Request"} (TestHttpRequest.java:113)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Running test "testHttp405NotAllowedMethod" (TestBase.java:33)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Expected result: {"action":"error","content":"Ping Pong server failure","status":"405 Method Not Allowed"} (TestHttpRequest.java:89)  
13.05.15 09:07:48 INFO  - TestHttpRequest      - Actual result: {"action":"error","content":"Ping Pong server failure","status":"405 Method Not Allowed"} (TestHttpRequest.java:90)  
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.012 sec - in ru.shishmakov.server.test.TestHttpRequest  

Results :  

Tests run: 4, Failures: 0, Errors: 0, Skipped: 0  


... <cut> ...

```  
  
   
---  

## Run
  *  Go to the `Server` submodule path `/netty-client-server/server/target/` and run:
```sh
netty-client-server/server/target>java -jar server-1.0-SNAPSHOT.jar  

13.05.15 09:13:43 WARN  - Game                 - Check connection to MongoDB ...  (Game.java:59)  
13.05.15 09:13:43 WARN  - Game                 - Connected to MongoDB on 127.0.0.1:27017 (Game.java:62)  
13.05.15 09:13:43 WARN  - Game                 - Initialise server ... (Game.java:51)  
13.05.15 09:13:43 INFO  - Game                 - Start the server: Game. Listen on: /127.0.0.1:80 (Game.java:53)  


```
  *  Go to the `Client` submodule path `/netty-client-server/client/target/` and run:  
```sh
netty-client-server/client/target>java -jar client-1.0-SNAPSHOT.jar  

13.05.15 09:18:08 WARN  - Client               - Initialise client ... (Client.java:46)  
13.05.15 09:18:09 WARN  - Client               - Start the client: Client. Listen on local address: /127.0.0.1:63736; remote address: /127.0.0.1:80 (Client.java:48)  
13.05.15 09:18:09 INFO  - Client               - Send HTTP request: POST /handler HTTP/1.1; content: {"action":"ping","profileid":"8b939bb8-1faa-4d62-8b42-43d63774e1d0"} (Client.java:54)  
13.05.15 09:18:09 INFO  - HttpClientProcessorHandler - Receive HTTP response:HTTP/1.1 200 OK; content: {"action":"pong","content":"pong 10","profileid":"8b939bb8-1faa-4d62-8b42-43d63774e1d0","status":"200 OK"} (HttpClientProcessorHandler.java:42)  
13.05.15 09:18:09 WARN  - Client               - Client to close the connection: Client (Client.java:57)  

```  
  
  * `Server` log:
```sh
13.05.15 09:18:09 INFO  - RequestProcessor     - // ---------------- start client  (RequestProcessor.java:95)  
13.05.15 09:18:09 DEBUG - RequestProcessor     - Client localAddress: /127.0.0.1:80 (RequestProcessor.java:96)  
13.05.15 09:18:09 DEBUG - RequestProcessor     - Client remoteAddress: /127.0.0.1:63736 (RequestProcessor.java:97)  
13.05.15 09:18:09 INFO  - RequestProcessor     - Client uri: /handler data: {"action":"ping","profileid":"8b939bb8-1faa-4d62-8b42-43d63774e1d0"} (RequestProcessor.java:102)  
13.05.15 09:18:09 DEBUG - ResponseSender       - Sent the data:{"action":"pong","content":"pong 10","profileid":"8b939bb8-1faa-4d62-8b42-43d63774e1d0","status":"200 OK"} (ResponseSender.java:46)  

```
  
  
---


## The end.



  
