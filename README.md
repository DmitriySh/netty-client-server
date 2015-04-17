netty-client-server
=======

My pet project.  
This is a prototype game. Consist of two parts: client and server. **Client** performs requests to server and receive response. **Server** listerns soket and waiting to recieve a request. Powered by: Netty Framework over HTTP, MongoDB and JSON like a protocol for messages comminication. 
  
  
## Rules:  

  * Client creates message with text `ping` and sends Http Request to server by POST method; example:`{ "action": "ping"}`.  
  * Server receives request and should creates Http Response to back client with message `pong N`; _N_ is a quantity of requests from current client; example: `{"action":"pong","content":"pong 4","status":"200 OK"}`.  
  * Server might have high load from huge number of clients.  
  
## Requirements:

  * Java SE Development Kit 7 (or higher)  
  * Apache Maven 3.x  
  * Netty 4.0.27.Final (boundle: netty/all in one)  
  * MongoDB 2.6.x (older versions might be unsupported by Java MongoDB Driver)


