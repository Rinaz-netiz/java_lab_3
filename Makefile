OUT_DIR = out
SRC_DIR = src/main/java
MAIN_CLASS = com.taxi.Main
SOURCES = $(shell find $(SRC_DIR) -name "*.java")

.PHONY: all compile run clean

all: run

compile: $(OUT_DIR)
	@javac -d $(OUT_DIR) $(SOURCES)

$(OUT_DIR):
	@mkdir -p $(OUT_DIR)

run: compile
	@java -cp $(OUT_DIR) $(MAIN_CLASS)

clean:
	@rm -rf $(OUT_DIR)
	