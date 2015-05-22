#!/bin/bash

cd src

# Compiles the server
if [[ "$1" == "server"  ]]; then
  javac -cp . net/dalesalter/Server.java
  java -cp . net.dalesalter.Server $2
fi

# Compiles the Client
if [[ "$1" == "client"  ]]; then
  javac -cp . net/dalesalter/Client.java
  java -cp . net.dalesalter.Client $2 $3
fi

echo 'Usage Client: client <server IP> <server port>'
echo 'Usage Server: server <server port>'
