# Javanaise Project

## Introduction
**1. Objectives**

The objective of the Javanaise project is to implement a distributed object cache in Java. Java applications using Javanaise can create and access distributed objects cached locally.

**2. Implementation Principles**

The Javanaise service is implemented through a centralized server that manages the distributed objects (this server is called the coordinator) and a library (jar) that allows any application to use the distributed object cache. To use the Javanaise service, an application must instantiate a specific class provided in this library (this class is named JvnServer). Instantiating the JvnServer class creates a local server for the application, which will enable it to create and access distributed objects (JvnLocalServer interface).

---

To manage the consistency of the various copies of distributed objects, every access to a distributed object is intercepted. In other words, every locally cached object has an indirection object (also called an interceptor) that intercepts invocations to execute the necessary control actions to manage the consistency of the local copy (updates, invalidations, etc.).

The different Javanaise servers communicate with the coordinator via the JvnRemoteCoord interface. Conversely, the coordinator can interact with the servers through their remote interfaces (JvnRemoteServer). All remote communications are based on the use of Java/RMI. The following figure shows the overall architecture of Javanaise.

## Tests
For an easy launch in the root folder you can test Javanaise 1 with:
```
./javanaiseV1.sh [CLIENTS_NUMBER]
```

And Javanaise 2 with:
```
./javanaiseV2.sh [CLIENTS_NUMBER]
```

Each script will launch 3 clients: one for reading, another for writing, and a third for both reading and writing. If you set CLIENTS_NUMBER=2, 6 clients will be active.

Note that on Windows OS, you can launch Javanaise 1 and 2 using the `.\windows_javanaiseV[1|2].bat` scripts.

---

You can test JVN1 with a server limited cache with:
```
./javanaiseV1_limitedcache.sh [NUMBER]
```

Or on Windows OS `./windows_javanaiseV1_limitedcache.sh [NUMBER]`


## Development
### Compile classes from root
```
javac -cp DONNE2/SOURCES/src/ DONNE2/SOURCES/src/jvn/*.java DONNE2/SOURCES/src/irc/*.java DONNE2/SOURCES/src/tests/*.java
```
