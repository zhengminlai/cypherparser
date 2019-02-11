# cypherparser
This is the cypher parser tool to convert cypher to query graph json as the input in PatMat Engine(https://github.com/UNSW-database/pattern_matching).

### Build the project(in Intellj IDEA)
1. Install the `scala` and `gradle` plugin in IDEA.

2. Open the project as the following setting(no need to set `Gradle home`):
![image](https://github.com/zhengminlai/cypherparser/blob/master/open-proj.png)

3. Download slizaa library: https://drive.google.com/file/d/1XXXDttCAE50WEYCM23tD8SBU0Nu3EHJo/view?usp=sharing

4. Unzip the downloaded file `slizaa.zip` to your local maven repository directory `your-user-dir/.m2/repository/org/`, for example, `/Users/bob/.m2/repository/org/`.

5. Right click `build.gradle` and  `Run build`. Then you can right click `CypherParserTest.scala` to run tests.

### Running the executable file
You can download the executable file from https://drive.google.com/drive/folders/1rbiIttvNTWfw5QK0LhfDV7Zd6CMm7fZd?usp=sharing,
or you can build the project by yourself as follows:

1. Build the project: gradle build

2. Unzip the file build/distributions/cypherparser-1.0.SNAPSHOT.zip
(ps: you can deploy the project using the artifact file build/distributions/cypherparser-1.0.SNAPSHOT.tar if you want to do so)

3. Go into the unzipped directory cypherparser-1.0.SNAPSHOT: cd build/distributions/cypherparser-1.0.SNAPSHOT/

4. Use command to parse your cypher: java -jar cypherparser.jar -c "YOUR CYPHER" -o "OUTPUT FILE". <br> <br> For example: java -jar cypherparser.jar -c "Match (a:A)-[b:B]->(c:C) RETURN a,c" -o "out.json"<br><br>
Or you can run the executable file in the bin directory: ./bin/cypherparser -c "Match (a:A)-[b:B]->(c:C) RETURN a,c" -o "out.json"
