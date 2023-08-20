JAVAC = javac
SRC_DIR = src
BINDIR = bin
PACKAGE = clubSimulation
MAIN_CLASS = $(PACKAGE).ClubSimulation

all: $(PACKAGE)

$(PACKAGE): $(SRC_DIR)/$(PACKAGE)/*.java
	$(JAVAC) -d . $(SRC_DIR)/$(PACKAGE)/*.java

run: $(PACKAGE)
	java $(MAIN_CLASS)

clean:
	rm -rf $(PACKAGE)/*.class

.PHONY: all run clean
