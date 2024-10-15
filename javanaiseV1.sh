#!/bin/bash
javac DONNE2/SOURCES/src/jvn/*.java
javac -cp DONNE2/SOURCES/src/jvn/*.java  DONNE2/SOURCES/src/irc/*.java
echo "Compilation Réussie !
Lancée avec $1 clients en JVN1 !!!!

"
cd DONNE2/SOURCES/src
java jvn.JvnCoordImpl & 
sleep 4
for ((i=1;i<=$1;i++))
do 
    sleep 1
    java irc.Irc &
    
done
 