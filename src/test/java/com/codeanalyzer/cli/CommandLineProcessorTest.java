package com.codeanalyzer.cli;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineProcessorTest {
    
    private CommandLineProcessor commandLineProcessor;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    @BeforeEach
    void setUp() {
        commandLineProcessor = new CommandLineProcessor();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
    
    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    @Test
    void process_withHelpOption_shouldDisplayHelp() {
        // Arrange
        String[] args = {"-h"};
        
        // Act
        commandLineProcessor.process(args);
        
        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Java Code Analyzer"));
        assertTrue(output.contains("help"));
        assertTrue(output.contains("source"));
    }
    
    @Test
    void process_withLongHelpOption_shouldDisplayHelp() {
        // Arrange
        String[] args = {"--help"};
        
        // Act
        commandLineProcessor.process(args);
        
        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Java Code Analyzer"));
    }
    
    @Test
    void process_withoutRequiredOptions_shouldDisplayErrorAndHelp() {
        // Arrange
        String[] args = {};
        
        // Act
        commandLineProcessor.process(args);
        
        // Assert
        String errOutput = errContent.toString();
        String output = outContent.toString();
        assertTrue(errOutput.contains("Error:"));
        assertTrue(errOutput.contains("source"));
        assertTrue(output.contains("Java Code Analyzer"));
    }
    
    @Test
    void process_withInvalidSourcePath_shouldDisplayError(@TempDir Path tempDir) {
        // Arrange
        Path nonExistentPath = tempDir.resolve("non-existent");
        String[] args = {"-s", nonExistentPath.toString()};
        
        // Act
        commandLineProcessor.process(args);
        
        // Assert
        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Error: Source path does not exist"));
    }
    
    @Test
    void process_withNonDirectorySourcePath_shouldDisplayError(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "This is not a directory");
        String[] args = {"-s", file.toString()};
        
        // Act
        commandLineProcessor.process(args);
        
        // Assert
        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Error: Source path does not exist or is not a directory"));
    }
    
    @Test
    void process_withValidSourcePath_shouldAnalyzeCode(@TempDir Path tempDir) throws IOException {
        // Arrange
        // Create a Java file in the temp directory
        Path javaDir = tempDir.resolve("src");
        Files.createDirectories(javaDir);
        Path javaFile = javaDir.resolve("Test.java");
        Files.writeString(javaFile, 
            "public class Test { public void testMethod() { } }"
        );
        
        // Set up a simulated input for the scanner (to quit after analysis)
        ByteArrayInputStream in = new ByteArrayInputStream("q\n".getBytes());
        System.setIn(in);
        
        String[] args = {"-s", tempDir.toString()};
        
        // Act
        commandLineProcessor.process(args);
        
        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Analyzing source code"));
        assertTrue(output.contains("Analysis completed"));
    }
    
    @Test
    void process_withInvalidOption_shouldDisplayErrorAndHelp() {
        // Arrange
        String[] args = {"-x", "invalid"};
        
        // Act
        commandLineProcessor.process(args);
        
        // Assert
        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Error:"));
        assertTrue(errOutput.contains("Unrecognized option"));
    }
}