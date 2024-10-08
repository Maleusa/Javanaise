#!/bin/bash
echo "Lancement compilation..."
javac DONNE2/SOURCES/src/jvn/*.java
javac -cp DONNE2/SOURCES/src/jvn/*.java DONNE2/SOURCES/src/irc/*.java
echo "Compilation réussie !
>>>>> Lancée sur $1 clients/procs
"
cd DONNE2/SOURCES/src/
java jvn.JvnCoordImpl &
sleep 4
for ((i=1;i<=$1;i++))
do
	sleep 1
	java irc.Irc &
done
