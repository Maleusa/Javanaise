#!/bin/bash

javac -cp DONNE2/SOURCES/src/ DONNE2/SOURCES/src/jvn/*.java DONNE2/SOURCES/src/irc/*.java DONNE2/SOURCES/src/tests/*.java
echo "Compilation réussie !\n Lancement des clients JVN1 "ClientReadLockTest", "ClientWriteLockTest", "ClientReadWriteLockTest" (x$1)\n\n"

cd DONNE2/SOURCES/src

java jvn.JvnCoordImpl & 
sleep 4

for ((i=1; i<=$1; i++))
do
    sleep 1
    java tests.ClientReadLockTest &
    sleep 1
    java tests.ClientWriteLockTest &
    sleep 1
    java tests.ClientReadWriteLockTest &
done
