# Javanaise Project
## Easy launch
For an easy launch in the root folder you can launch 
```
./javanaiseV1.sh ARG
```
For javanaise irc v1.
With an integer as ARG for the number of clients
or
```
./javanaiseV2.sh ARG
```
for javanaise irc V2.
### Compile classes from the `src` folder :
```
javac jvn/*.java
javac -cp jvn/*.java irc/*.java
```

### Launch the project jvn1 from the `src` folder for Windows:
```
start java jvn.JvnCoordImpl
start java irc.Irc
```
### Launch the project jvn1 from the `src` folder for Linus:
```
java jvn.JvnCoordImpl &
java irc.Irc &
```
### Launch the project jvn2 from the `src` folder for Windows:
```
start java jvn.JvnCoordImpl
start java irc.IrcA
```
### Launch the project jvn2 from the `src` folder for Linus:
```
java jvn.JvnCoordImpl &
java irc.IrcA &
```
