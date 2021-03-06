# client-server-app

A system to transfer web pages over the network using client/server architecture. The system consists of two main distributed components: server and client, which may run on different hosts in the network. A server is a program that accepts multiple incoming TCP connections from clients and responds to requests sent by clients.

A protocol called Json-based Hypertext Transfer Protocol (JHTTP) which is used by server and clients to communicate. JHTTP has a set of request methods to indicate the desired action to be performed for a given web page. Please note that web pages are text files only.

The system has been designed to use TCP Sockets. Server creates multiple threads and uses them to process clients’ requests. All the communicated messages between the server and clients must be encapsulated in the form of JSON messages.


# Server
A server maintains a root folder (directory) which contains all the web pages named as `www`.

When the server program is executed, the program checks that if `www` folder exists on the same folder in which the
server executable file resides. If this folder does not exists, server program should create it automatically.

Server accepts TCP connection requests from clients and responds to the communicated JHTTP requests. The server must be able to serve multiple clients concurrently. As soon as a client is connected to the server, the server is ready to accept request messages including `GET`, `PUT`, `DELETE`, `DISCONNECT` requests.

Table 1
| Request Type | Meaning                                                             |
|--------------|:-------------------------------------------------------------------:|
|GET           |A GET request is used to retrieve a web page content from the server.|
|PUT           |A PUT request replaces or adds the target web page content on the server with the request payload.|
|DELETE        |A DELETE request deletes the specified web page.|
|DISCONNECT    |Terminates the connection with the server|

Path to one file should follow this rule: Starts with slash ‘/’ + between 0 to 10 nested folders (the length of folder name ranges from 1 to 20 ACCEPTED CHARACTERS) + End with slash ‘/’ + file name (the length of file name ranges from 1 to 10 ACCEPTED CHARACTERS) + Dot ‘.’ + file extension (the length of file extension ranges from 1 to 5 ACCEPTED CHARACTERS). In what follows, you can find some examples:
- /index.html (ACCEPTED)
- /A1/B/C532vaAZu67/6D/7/A2/1rule.ex3 (ACCEPTED)
- /index123450.html543 (NOT ACCEPTED)

Path to one folder should follow this rule: Starts with slash ‘/’ + between 1 to 10 nested folders (the length of folder name ranges from 1 to 20 ACCEPTED CHARACTERS) + ends with slash ‘/’. In what follows, you can find some examples:
- /A/B/C/ (ACCEPTED)
- /A1/B/C532vaAZu67/6D/7/A2/ (ACCEPTED)
- / (NOT ACCEPTED)
- The List of ACCEPTED CHARACTERS: [a-z, A-Z, , 0-9]

# Client
A client is a command-line interface (CLI) that accepts commands from the end user, sends JSON request messages to the server accordingly, and shows server responses on the CLI in the desired format. Table 2 shows lists of commands and their arguments that the end-user can type.

The end-user types a command and its required arguments on the CLI. Commands are executed by Enter (Return) key. According to each command, the client program creates a corresponding request message from Table 1 and sends it to the server. Upon on receiving a request message by the server, a response message is generated by the server and is sent back to the client.

Please note that messages sent between the client and the server are all in JSON format (more details in Section 4). The response message can be either success or error. Client shows appropriate texts on the CLI based on the message received.

- If an end-user enters a command which is not defined, has missing or extra arguments, or it has error in any forms, client program prints nothing on the CLI and goes to the next line waiting for a new command.
- Commands are case-sensitive.
- If the source file in the put command cannot be found on the client system, the CLI shows Source file not found.
- The program is supposed to only accept relative paths, for example, `/index.html` or `/cmd/index.html`. A relative path refers to a location that is relative to a root folder i.e. `www` folder. Please note that the root folder is not part of the target path. If `www` appears as part of the path, for example, `/www/index.html`, it means that there is a subfolder called www within the root folder.

Table 2
| Request Type | Arguments         | Meaning      | Example           |
|--------------|:-----------------:|:------------:|:-----------------:|
|connect | `ip`, `port` | connect to a server with the IP address `ip` and port address `port` | connect 192.168.0.10 2000 |
|get | `target` | retrieve the content of the specified web page from the server | get /index.html |
|put | `source`, `target` | upload the content of the specified `source` web page on the client file system to the specified `target` web page on the server | put /test.html /finance/index.html |
|delete | `target` | delete the specified web page from the server `target` can be an empty folder | delete /finance/test.html |
|disconnect |  | disconnect from the server | disconnect |

# Request Messages
All request messages have a key-value pair that shows the `type` of the request message. `type` can either of the following: `GET`, `PUT`, `DELETE`, `DISCONNECT`.
If `type` is `GET` the format is:
```json
{
  "message": "request",
  "type": "GET" ,
  "target": "/cmd/index.html"
}
```

If `type` is `PUT` the format is:
```json
{
  "message": "request",
  "type": "PUT" ,
  "target": "/cmd/index.html",
  "content": "web page content in utf-8 format"
}
```

If `type` is `DELETE` the format is:
```json
{
  "message": "request",
  "type": "DELETE" ,
  "target": "/index.hmtl"
}
```

If `type` is `DISCONNECT` the format is:
```json
{
  "message": "request",
  "type": "DISCONNECT"
}
```
# Response Messages
JSON format for response messages is as follows:
```json
{
"message":"response",
"code":"a valid JSON number data type" ,
"content":"a valid JSON string data type"
}
```

The list of “code” values and content values are shown in Table 3.
- Please note that CLI shows the value of the “content” key for all “code” values, for example OK for code 201.

Table 3
| Code | Meaning           | Content      |
|------|:-----------------:|:------------:|
| 200 | `GET` request was successfull | The content of the target web page in utf-8 format |
| 201 | `PUT` request was successful, new file created | Ok |
| 202 | `PUT` request was successful, but file is overwritten | Modified |
| 203 | `DELETE` request was successful | Ok |
| 400 | Target web page does not exist (`GET` or `DELETE`) | Not Found |
| 401 | Request format is incorrect, missing or extra agruments and wrong message type and invalid targets | Bad Request |
| 402 | Any other erros | Unknown Error |

# Connect and Disconnet
The end-user enters connect command immediately after running the client program. This command results in a TCP connection establishment with a server, for example:
`connect 192.168.0.10 2000`

The end-user enters disconnect command to close down connection and quits program. This command results in a TCP disconnection with a server, for example:
`disconnect`

# Running the program
Run client and server executable as follows:
`$ java -jar server.jar -p port`
`$ java -jar client.jar`
