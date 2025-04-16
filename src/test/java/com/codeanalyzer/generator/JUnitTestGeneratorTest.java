package com.codeanalyzer.generator;

import com.codeanalyzer.model.MethodInfo;
import com.codeanalyzer.model.ParameterInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JUnitTestGeneratorTest {

    private JUnitTestGenerator generator;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        generator = new JUnitTestGenerator(tempDir.toString());
    }
    
    @Test
    void generateTestFile_withSimpleMethod_shouldCreateTestFile() {
        // Arrange
        MethodInfo method = new MethodInfo(
            "com.example.SimpleClass",
            "process",
            "String",
            Arrays.asList(
                new ParameterInfo("text", "String"),
                new ParameterInfo("count", "int")
            )
        );
        
        // Act
        boolean result = generator.generateTestFile(method);
        
        // Assert
        assertTrue(result);
        Path expectedFile = tempDir.resolve("SimpleClassTest.java");
        assertTrue(Files.exists(expectedFile));
        
        // Check file content
        try {
            String content = Files.readString(expectedFile);
            assertNotNull(content);
            assertTrue(content.contains("package com.example"));
            assertTrue(content.contains("class SimpleClassTest"));
            assertTrue(content.contains("private SimpleClass instance"));
            assertTrue(content.contains("void process_"));
            assertTrue(content.contains("String text ="));
            assertTrue(content.contains("int count ="));
            assertTrue(content.contains("String result = instance.process(text, count)"));
        } catch (IOException e) {
            fail("Could not read generated test file: " + e.getMessage());
        }
    }
    
    @Test
    void generateTestFile_withVoidMethod_shouldCreateTestFile() {
        // Arrange
        MethodInfo method = new MethodInfo(
            "com.example.VoidClass",
            "doSomething",
            "void",
            Arrays.asList(
                new ParameterInfo("flag", "boolean")
            )
        );
        
        // Act
        boolean result = generator.generateTestFile(method);
        
        // Assert
        assertTrue(result);
        Path expectedFile = tempDir.resolve("VoidClassTest.java");
        assertTrue(Files.exists(expectedFile));
        
        // Check file content
        try {
            String content = Files.readString(expectedFile);
            assertNotNull(content);
            assertTrue(content.contains("instance.doSomething(flag)"));
            assertTrue(content.contains("// No return value to assert"));
        } catch (IOException e) {
            fail("Could not read generated test file: " + e.getMessage());
        }
    }
    
    @Test
    void generateTestFiles_withMultipleMethods_shouldCreateTestFiles() {
        // Arrange
        List<MethodInfo> methods = new ArrayList<>();
        
        // First class with two methods
        methods.add(new MethodInfo(
            "com.example.ClassA",
            "method1",
            "int",
            Arrays.asList(
                new ParameterInfo("value", "int")
            )
        ));
        
        methods.add(new MethodInfo(
            "com.example.ClassA",
            "method2",
            "boolean",
            Arrays.asList(
                new ParameterInfo("text", "String")
            )
        ));
        
        // Second class with one method
        methods.add(new MethodInfo(
            "com.example.ClassB",
            "process",
            "void",
            Arrays.asList(
                new ParameterInfo("data", "Object")
            )
        ));
        
        // Act
        int result = generator.generateTestFiles(methods);
        
        // Assert
        assertEquals(2, result); // Should generate 2 test files (one for each class)
        
        // Check first test file
        Path fileA = tempDir.resolve("ClassATest.java");
        assertTrue(Files.exists(fileA));
        
        // Check second test file
        Path fileB = tempDir.resolve("ClassBTest.java");
        assertTrue(Files.exists(fileB));
        
        // Check file content
        try {
            String contentA = Files.readString(fileA);
            assertTrue(contentA.contains("method1_"));
            assertTrue(contentA.contains("method2_"));
            
            String contentB = Files.readString(fileB);
            assertTrue(contentB.contains("process_"));
        } catch (IOException e) {
            fail("Could not read generated test files: " + e.getMessage());
        }
    }
    
    @Test
    void generateTestFiles_withNoMethods_shouldReturnZero() {
        // Arrange
        List<MethodInfo> methods = Collections.emptyList();
        
        // Act
        int result = generator.generateTestFiles(methods);
        
        // Assert
        assertEquals(0, result);
    }
    
    @Test
    void generateTestFile_withMethodContainingListParameter_shouldAddListImports() {
        // Arrange
        MethodInfo method = new MethodInfo(
            "com.example.ListProcessor",
            "processItems",
            "int",
            Arrays.asList(
                new ParameterInfo("items", "List<String>")
            )
        );
        
        // Act
        boolean result = generator.generateTestFile(method);
        
        // Assert
        assertTrue(result);
        Path expectedFile = tempDir.resolve("ListProcessorTest.java");
        assertTrue(Files.exists(expectedFile));
        
        // Check file content
        try {
            String content = Files.readString(expectedFile);
            assertNotNull(content);
            assertTrue(content.contains("import java.util.List;"));
            assertTrue(content.contains("import java.util.ArrayList;"));
            assertTrue(content.contains("import java.util.Arrays;"));
        } catch (IOException e) {
            fail("Could not read generated test file: " + e.getMessage());
        }
    }
}