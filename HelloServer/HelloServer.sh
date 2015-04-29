#!/bin/bash

cd src

# Compiles the server
if [[ "$1" == "server"  ]]; then
  javac -cp . net/dalesalter/Server.java
  java -cp . net.dalesalter.Server 9999
fi

# Compiles the Client
if [[ "$1" == "client"  ]]; then
  javac -cp . net/dalesalter/Client.java
  java -cp . net.dalesalter.Client 127.0.0.1 9999
fi


