#!/bin/bash

# Set up Java environment
export JAVA_HOME=/nix/store/zmj3m7wrgqf340vqd4v90w8dw371vhjg-openjdk-17.0.7+7
export PATH=$JAVA_HOME/bin:$PATH

# Create dummy classes for testing
mkdir -p test_src/com/example
cat > test_src/com/example/Sample.java << EOL
package com.example;

/**
 * A sample class for testing the code analyzer
 */
public class Sample {
    
    /**
     * Sample method with different parameter types
     */
    public String process(String text, int count, boolean flag) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        if (count <= 0) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (flag) {
                result.append(text.toUpperCase());
            } else {
                result.append(text.toLowerCase());
            }
        }
        
        return result.toString();
    }
    
    /**
     * Another method with a list parameter
     */
    public boolean checkList(java.util.List<String> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        
        for (String item : items) {
            if (item == null || item.isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
}
EOL

echo "Running Gradle build..."
./gradlew build --info

echo "Running Code Analyzer on sample Java file..."
./gradlew run --args="-s test_src"

# Create directory for test generation
mkdir -p generated-tests

echo -e "\nRunning Code Analyzer with JUnit test generation..."
./gradlew run --args="-s test_src -g -o generated-tests"

echo -e "\nGenerated JUnit test files:"
ls -la generated-tests