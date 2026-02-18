# Compiler and flags
JC = javac
JFLAGS = -g

# Directories
SRC_DIR = src
BIN_DIR = bin
LIB_DIR = lib

# Dependencies
JAR_DEPS = $(LIB_DIR)/json-simple-1.1.1.jar

# Java source files
SRCS = \
	$(SRC_DIR)/*.java \
	$(SRC_DIR)/MyImplementations/*.java \

# Default target
default: classes

# Compile all Java files into bin/
classes: $(BIN_DIR)
	$(JC) $(JFLAGS) -cp "$(JAR_DEPS)" -d $(BIN_DIR) $(SRCS)

# Create bin directory if it doesn't exist
$(BIN_DIR):
	mkdir $(BIN_DIR)

# Run the server with the PORT variable
run-http:
	@java -cp "$(BIN_DIR);$(JAR_DEPS)" App $(PORT)

# Clean compiled classes
clean:
	rm -rf $(BIN_DIR)/* 

