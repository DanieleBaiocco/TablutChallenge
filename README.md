# TablutCompetition (Team INiegghie)


## Prerequisite: run the server

clone the project repository:

```
git clone https://github.com/AGalassi/TablutCompetition.git
```

Go into the project folder (the folder with the `build.xml` file):
```
cd TablutCompetition/Tablut
```

Compile the project:

```
ant clean
ant compile
```

The compiled project is in  the `build` folder.
Run the server with:

```
ant server
```

## How to make our player play the game 

Clone the project repository:

```
git clone https://github.com/DanieleBaiocco/TablutChallenge
```

Go into the Executables folder:
```
cd TablutChallenge/Tablut/Executables/tablut
```

From here you can execute a player as white typing in Linux:

```
./runmyplayer.sh white 60 localhost 
```

or as black typing:

```
./runmyplayer.sh black 60 localhost
```

The executable doesn't nothing but executing the jar file TablutINiegghie.jar in the same folder passing to it different arguments:

```
#!/bin/bash
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd $parent_path
java -jar TablutINiegghie.jar "$1" "$2" "$3"
```
