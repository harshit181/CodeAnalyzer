package com.codeanalyzer.analyzer;

import com.codeanalyzer.model.AnalysisResult;
import com.codeanalyzer.model.MethodInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CodeAnalyzerTest {
    
    private CodeAnalyzer codeAnalyzer;
    
    @BeforeEach
    void setUp() {
        codeAnalyzer = new CodeAnalyzer();
    }
    
    @Test
    void analyzeDirectory_withValidJavaFile_shouldReturnMethods(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path javaFile = createSimpleJavaFile(tempDir);
        
        // Act
        AnalysisResult result = codeAnalyzer.analyzeDirectory(tempDir);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.getMethods().isEmpty());
        assertTrue(result.getMethods().stream()
                .anyMatch(method -> method.getMethodName().equals("simpleMethod")));
    }
    
    @Test
    void analyzeDirectory_withMultipleJavaFiles_shouldAnalyzeAll(@TempDir Path tempDir) throws IOException {
        // Arrange
        createSimpleJavaFile(tempDir);
        createAnotherJavaFile(tempDir);
        
        // Act
        AnalysisResult result = codeAnalyzer.analyzeDirectory(tempDir);
        
        // Assert
        assertNotNull(result);
        List<MethodInfo> methods = result.getMethods();
        assertTrue(methods.size() >= 2);
        
        boolean foundSimpleMethod = false;
        boolean foundProcessMethod = false;
        
        for (MethodInfo method : methods) {
            if (method.getMethodName().equals("simpleMethod")) {
                foundSimpleMethod = true;
            } else if (method.getMethodName().equals("process")) {
                foundProcessMethod = true;
            }
        }
        
        assertTrue(foundSimpleMethod, "Should find simpleMethod");
        assertTrue(foundProcessMethod, "Should find process method");
    }
    
    @Test
    void analyzeDirectory_withNoJavaFiles_shouldReturnEmptyResult(@TempDir Path tempDir) throws IOException {
        // Arrange
        // Create a non-Java file
        Path txtFile = tempDir.resolve("notJava.txt");
        Files.writeString(txtFile, "This is not a Java file");
        
        // Act
        AnalysisResult result = codeAnalyzer.analyzeDirectory(tempDir);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.getMethods().isEmpty());
    }
    
    @Test
    void analyzeDirectory_withInvalidJavaFiles_shouldHandleErrors(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path invalidJavaFile = tempDir.resolve("Invalid.java");
        Files.writeString(invalidJavaFile, "This is not valid Java code");
        
        // Act
        AnalysisResult result = codeAnalyzer.analyzeDirectory(tempDir);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.getMethods().isEmpty());
    }
    
    @Test
    void analyzeDirectory_withNestedDirectories_shouldFindAll(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path nestedDir = tempDir.resolve("nested");
        Files.createDirectories(nestedDir);
        
        createSimpleJavaFile(tempDir);
        createAnotherJavaFile(nestedDir);
        
        // Act
        AnalysisResult result = codeAnalyzer.analyzeDirectory(tempDir);
        
        // Assert
        assertNotNull(result);
        List<MethodInfo> methods = result.getMethods();
        assertTrue(methods.size() >= 2);
        
        boolean foundSimpleMethod = false;
        boolean foundProcessMethod = false;
        
        for (MethodInfo method : methods) {
            if (method.getMethodName().equals("simpleMethod")) {
                foundSimpleMethod = true;
            } else if (method.getMethodName().equals("process")) {
                foundProcessMethod = true;
            }
        }
        
        assertTrue(foundSimpleMethod, "Should find simpleMethod");
        assertTrue(foundProcessMethod, "Should find process method");
    }
    
    @Test
    void analyzeFile_shouldExtractMethodInfo() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("codeanalyzer");
        Path javaFile = createSimpleJavaFile(tempDir);
        
        // Act
        List<MethodInfo> methods = codeAnalyzer.analyzeFile(javaFile);
        
        // Assert
        assertNotNull(methods);
        assertFalse(methods.isEmpty());
        assertEquals(1, methods.size());
        assertEquals("simpleMethod", methods.get(0).getMethodName());
        assertEquals("void", methods.get(0).getReturnType());
    }
    
    private Path createSimpleJavaFile(Path directory) throws IOException {
        Path file = directory.resolve("SimpleClass.java");
        String content = 
            "public class SimpleClass {\n" +
            "    public void simpleMethod() {\n" +
            "        System.out.println(\"Hello, world!\");\n" +
            "    }\n" +
            "}\n";
        Files.writeString(file, content);
        return file;
    }
    
    private Path createAnotherJavaFile(Path directory) throws IOException {
        Path file = directory.resolve("Processor.java");
        String content = 
            "public class Processor {\n" +
            "    public String process(String input, int count) {\n" +
            "        if (input == null || input.isEmpty()) {\n" +
            "            return \"\";\n" +
            "        }\n" +
            "        StringBuilder result = new StringBuilder();\n" +
            "        for (int i = 0; i < count; i++) {\n" +
            "            result.append(input);\n" +
            "        }\n" +
            "        return result.toString();\n" +
            "    }\n" +
            "}\n";
        Files.writeString(file, content);
        return file;
    }
}