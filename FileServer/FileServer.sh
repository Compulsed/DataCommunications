#!/bin/bash


# Compiles the server
if [[ "$1" == "server"  ]]; then
  javac -cp . src/net/dalesalter/FileServer.java
  java -cp src/ net.dalesalter.FileServer $2
fi

# Compiles the Client
if [[ "$1" == "client"  ]]; then
  javac -cp . src/net/dalesalter/FileClient.java
  java -cp  src/ net.dalesalter.FileClient $2 $3 $4
fi


