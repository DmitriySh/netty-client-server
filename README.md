netty-client-server
=======

My pet project.  
This is a prototype game. Consist of two parts: client and server. **Client** performs requests to server and receive response. **Server** listerns soket and waiting to recieve a request. Powered by: Java SE, Netty Framework over HTTP, MongoDB and JSON like a protocol for messages comminication. Netty supports SEDA like highly customizable thread model: single thread, one or more thread pools and queue between them.
  
  
## Rules:  

  * Client creates message with text `ping` and sends Http Request to server by POST method; example:`{ "action": "ping"}`.  
  * Server receives request and should creates Http Response to back client with message `pong N`; _N_ is a quantity of requests from current client; example: `{"action":"pong","content":"pong 4","status":"200 OK"}`.  
  * Server might have high load from huge number of clients.  
  
## Requirements:

  * Java SE Development Kit 7 (or newer)  
  * Apache Maven 3.x  
  * Netty 4.0.27.Final (boundle: netty/all in one)  
  * MongoDB 2.6.x (older versions might be unsupported by Java MongoDB Driver)  
  * Git 1.7.x (or newer)  

## Setup and run:  

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
  
  
