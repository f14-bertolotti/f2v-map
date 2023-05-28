
lib/commons-cli-1.4.jar:
	mkdir -p ./lib
	wget https://repo1.maven.org/maven2/commons-cli/commons-cli/1.4/commons-cli-1.4.jar -P ./lib/

lib/commons-io-2.6.jar: 
	mkdir -p ./lib
	wget https://repo1.maven.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar -P ./lib/ 

lib/commons-lang3-3.11.jar: 
	mkdir -p ./lib
	wget https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.11/commons-lang3-3.11.jar -P ./lib/

lib/jep-3.9.0.jar: 
	mkdir -p ./lib
	wget https://repo1.maven.org/maven2/black/ninia/jep/3.9.0/jep-3.9.0.jar -P ./lib/

lib/javaparser-core-3.25.3.jar: 
	mkdir -p ./lib
	wget https://repo1.maven.org/maven2/com/github/javaparser/javaparser-core/3.25.3/javaparser-core-3.25.3.jar -P ./lib/

lib/commons-text-1.10.0.jar:
	mkdir -p ./lib
	wget https://repo1.maven.org/maven2/org/apache/commons/commons-text/1.10.0/commons-text-1.10.0.jar -P ./lib/

bin: lib/commons-text-1.10.0.jar lib/commons-cli-1.4.jar lib/commons-io-2.6.jar lib/commons-lang3-3.11.jar lib/jep-3.9.0.jar lib/javaparser-core-3.25.3.jar $(shell find -name "*.java")
	mkdir -p bin
	touch bin
	javac -classpath "./lib/*" -d "./bin/" -sourcepath ./src/main/java/ ./src/main/java/*.java

jars/code-parser.jar: bin
	mkdir -p jars
	cp ./lib/* ./bin/
	cd ./bin/; find ./ -name "*.jar" -exec jar -xf {} \;
	jar cf ./jars/code-parser.jar -C ./bin/ .

clean:
	rm -rf ./lib
	rm -rf ./bin
	rm -rf ./jars



all: jars/code-parser.jar clean
