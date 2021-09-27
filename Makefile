NAME=ELECTIONS
JARFILE-CLIENT=bin/$(NAME)-CLIENT.jar
JARFILE-SERVER=bin/$(NAME)-SERVER.jar
BINARY-FOLDER=bin
MANIFEST-CLIENT=$$(find . -name "CLIENT-MANIFEST.MF")
MANIFEST-SERVER=$$(find . -name "SERVER-MANIFEST.MF")
CLS=src
all: install build-jar clean run
build-jar:
	javac -d $(CLS) $$(find $(CLS) -name "*.java")
	cd ./$(CLS)/ && ls && jar cvfm ../$(JARFILE-CLIENT) $(MANIFEST-CLIENT) $$(find . -name "*.class")
	cd ./$(CLS)/ && ls && jar cvfm ../$(JARFILE-SERVER) $(MANIFEST-SERVER) $$(find . -name "*.class")
clean:
	rm -rf $$(find $(CLS) -name "*.class")
install:
	sudo apt install default-jre default-jdk -y
	mkdir -p $(BINARY-FOLDER)
	rm -rf $(BINARY-FOLDER)/*
run:
	java -jar $(JARFILE-SERVER)
github-build: install build-jar clean