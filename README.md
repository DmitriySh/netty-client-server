# netty-client-server

My pet project.  
This is a prototype game. Consist of two parts: client and server. **Client** performs requests to server and receive response. **Server** listerns soket and waiting to recieve a request. Powered by: Netty Framework over HTTP, MongoDB and JSON like a protocol for messages comminication. 
  
  
Rules:  

1) client creates message with text "ping" and sends Http Request to server by POST method.
```javascript
{ "action": "ping"}
```
2) server receives request and should creates Http Response to back client with message "pong N"; _N_ is a quantity of requests from current client.
```javascript
{"action":"pong","content":"pong 4","status":"200 OK"}
```
3) server might have high load from huge number of clients.  
  





