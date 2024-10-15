#!/bin/bash

javac -cp DONNE2/SOURCES/src/ DONNE2/SOURCES/src/jvn/*.java DONNE2/SOURCES/src/irc/*.java DONNE2/SOURCES/src/tests/*.java
echo "Compilation réussie !\n Lancement des clients JVN2 "ClientReadLockATest", "ClientWriteLockATest", "ClientReadWriteLockATest" (x$1)\n\n"

cd DONNE2/SOURCES/src

java jvn.JvnCoordImpl &
sleep 4

for ((i=1; i<=$1; i++))
do 
    sleep 1
    java tests.ClientReadLockATest &
    sleep 1
    java tests.ClientWriteLockATest &
    sleep 1
    java tests.ClientReadWriteLockATest &
done
