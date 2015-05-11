#!/bin/bash


# Compiles the server
javac -cp . src/net/dalesalter/*.java
java -cp src/ net.dalesalter.ChatPeer $1 $2
