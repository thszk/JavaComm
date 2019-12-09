# Java Group Communication

This program implements a simple Multicast Group Communication with n processes and one external process.


# Usage

The program usage is manually. To compile and execute the files is necessary be inside the root project directory.

To Compile files:

``` $ javac src/*.java -d out/ -Xlint ```

To start client:

``` $ java -cp out Client ```

To run external process

``` $ java -cp out ExternalProcess <leader_ip>```


# Messages TAGs

``` ce- ``` Client External - followed by the message, to send from client to external process.

``` cg- ``` Client Group - followed by the message, to send from client to multicast group.

``` lg- ``` Leader Group - followed by the message, to send from leader process to multicast group. Only used by Leader Thread.

``` eg- ``` External Group - followed by the message, to send from external process to multicast group. Only used on Receive Thread by leader.


# Directory Structure

```
JavaComm/
│
├── out/ - contains the .class files generated from compile process.
│   ├── Client.class
│   ├── ClientThread.class
│   .
│   .
|   .
│   └── SendThread.class
│
├── src/ - contains the .java files
│   ├── Client.java
│   ├── ClientThread.java
│   .
│   .
|   .
│   └── SendThread.java
│
└── README.md - It's this file.
```