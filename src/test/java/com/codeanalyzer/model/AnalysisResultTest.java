package com.codeanalyzer.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisResultTest {

    @Test
    void constructor_withEmptyList_shouldCreateInstance() {
        // Arrange
        List<MethodInfo> methods = Collections.emptyList();
        
        // Act
        AnalysisResult result = new AnalysisResult(methods);
        
        // Assert
        assertNotNull(result);
        assertEquals(methods, result.getMethods());
        assertEquals(0, result.getMethodCount());
    }
    
    @Test
    void constructor_withNonEmptyList_shouldCreateInstance() {
        // Arrange
        List<MethodInfo> methods = new ArrayList<>();
        methods.add(new MethodInfo("TestClass", "testMethod", "void", Collections.emptyList()));
        methods.add(new MethodInfo("TestClass", "anotherMethod", "String", Collections.emptyList()));
        
        // Act
        AnalysisResult result = new AnalysisResult(methods);
        
        // Assert
        assertNotNull(result);
        assertEquals(methods, result.getMethods());
        assertEquals(2, result.getMethodCount());
    }
    
    @Test
    void getMethods_shouldReturnMethodsList() {
        // Arrange
        List<MethodInfo> methods = new ArrayList<>();
        methods.add(new MethodInfo("TestClass", "testMethod", "void", Collections.emptyList()));
        AnalysisResult result = new AnalysisResult(methods);
        
        // Act
        List<MethodInfo> returnedMethods = result.getMethods();
        
        // Assert
        assertSame(methods, returnedMethods);
    }
    
    @Test
    void getMethodCount_withEmptyList_shouldReturnZero() {
        // Arrange
        AnalysisResult result = new AnalysisResult(Collections.emptyList());
        
        // Act
        int count = result.getMethodCount();
        
        // Assert
        assertEquals(0, count);
    }
    
    @Test
    void getMethodCount_withNonEmptyList_shouldReturnCorrectCount() {
        // Arrange
        List<MethodInfo> methods = new ArrayList<>();
        methods.add(new MethodInfo("TestClass", "method1", "void", Collections.emptyList()));
        methods.add(new MethodInfo("TestClass", "method2", "int", Collections.emptyList()));
        methods.add(new MethodInfo("TestClass", "method3", "String", Collections.emptyList()));
        AnalysisResult result = new AnalysisResult(methods);
        
        // Act
        int count = result.getMethodCount();
        
        // Assert
        assertEquals(3, count);
    }
}