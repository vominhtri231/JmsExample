## JMS EXAMPLE

An simple maven example for JMS Usage

#### How to run
User must have ActiveMq running at port tcp:61616, and can config it is in Config class.

1. Consumer: 
```
 mvn exec:java -D exec.mainClass="Consumer"
```
To stop the consumer , type an thing in the terminal.

2. Producer
```
 mvn exec:java -D exec.mainClass="Producer"
```

