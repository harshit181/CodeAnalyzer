package com.codeanalyzer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Permission;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    private static class ExitException extends SecurityException {
        public final int status;
        public ExitException(int status) {
            super("System.exit(" + status + ")");
            this.status = status;
        }
    }
    
    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            // Allow all permissions
        }
        
        @Override
        public void checkPermission(Permission perm, Object context) {
            // Allow all permissions
        }
        
        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }
    
    private SecurityManager originalSecurityManager;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        originalSecurityManager = System.getSecurityManager();
        System.setSecurityManager(new NoExitSecurityManager());
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setSecurityManager(originalSecurityManager);
    }
    
    @Test
    void main_withHelpArgument_shouldDisplayHelp() {
        // Arrange
        String[] args = {"-h"};
        
        // Act
        Main.main(args);
        
        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Java Code Analyzer"));
        assertTrue(output.contains("help"));
    }
    
    @Test
    void main_withInvalidArguments_shouldExitWithErrorCode() {
        // Arrange
        String[] args = {"-invalidOption"};
        
        // Act & Assert
        ExitException exception = assertThrows(ExitException.class, () -> {
            Main.main(args);
        });
        
        // Should exit with code 1
        assertEquals(1, exception.status);
        
        // Error message should be printed
        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Error:"));
    }
    
    @Test
    void main_withValidSourcePath_shouldAnalyzeCode(@TempDir Path tempDir) throws IOException {
        // Arrange
        // Create a Java file in the temp directory
        Path javaDir = tempDir.resolve("src");
        Files.createDirectories(javaDir);
        Path javaFile = javaDir.resolve("Test.java");
        Files.writeString(javaFile, 
            "public class Test { public void testMethod() { } }"
        );
        
        String[] args = {"-s", tempDir.toString()};
        
        // Act & Assert
        try {
            Main.main(args);
            // Expect the test to be interrupted by System.exit from the CommandLineProcessor
            fail("Expected System.exit to be called");
        } catch (ExitException e) {
            // This is expected, we can't really test the full execution path
            // without refactoring the application to avoid System.exit in tests
        }
        
        // Check that the analysis started
        String output = outContent.toString();
        assertTrue(output.contains("Analyzing source code"));
    }
}