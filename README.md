# cypherparser
This is the cypher parser tool to convert cypher to query graph json as the input in PatMat Engine(https://github.com/UNSW-database/pattern_matching).

### Running the executable file
1. Build the project: gradle build

2. unzip the file build/distributions/cypherparser-1.0.SNAPSHOT.zip
(ps: you can deploy the project using the artifact file build/distributions/cypherparser-1.0.SNAPSHOT.tar if you want to do so)

3. Go into the unzipped directory cypherparser-1.0.SNAPSHOT: cd build/distributions/cypherparser-1.0.SNAPSHOT/

4. Use command to parse your cypher: java -jar cypherparser.jar -c "YOUR CYPHER" -o "OUTPUT FILE". <br> For example: java -jar cypherparser.jar -c "Match (a:A)-[b:B]->(c:C) RETURN a,c" -o "out.json"<br/>
Or you can run the executable file in the bin directory: ./bin/cypherparser -c "Match (a:A)-[b:B]->(c:C) RETURN a,c" -o "out.json"