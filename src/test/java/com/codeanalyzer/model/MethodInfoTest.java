package com.codeanalyzer.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MethodInfoTest {

    @Test
    void constructor_withBasicInfo_shouldCreateInstance() {
        // Arrange
        String className = "com.example.TestClass";
        String methodName = "testMethod";
        String returnType = "String";
        List<ParameterInfo> parameters = Collections.emptyList();
        
        // Act
        MethodInfo methodInfo = new MethodInfo(className, methodName, returnType, parameters);
        
        // Assert
        assertNotNull(methodInfo);
        assertEquals(className, methodInfo.getClassName());
        assertEquals(methodName, methodInfo.getMethodName());
        assertEquals(returnType, methodInfo.getReturnType());
        assertEquals(parameters, methodInfo.getParameters());
    }
    
    @Test
    void getSignature_withNoParameters_shouldReturnCorrectSignature() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo(
            "com.example.TestClass", 
            "testMethod", 
            "void", 
            Collections.emptyList()
        );
        
        // Act
        String signature = methodInfo.getSignature();
        
        // Assert
        assertEquals("void com.example.TestClass.testMethod()", signature);
    }
    
    @Test
    void getSignature_withParameters_shouldReturnCorrectSignature() {
        // Arrange
        List<ParameterInfo> parameters = Arrays.asList(
            new ParameterInfo("param1", "String"),
            new ParameterInfo("param2", "int")
        );
        
        MethodInfo methodInfo = new MethodInfo(
            "com.example.TestClass", 
            "testMethod", 
            "boolean", 
            parameters
        );
        
        // Act
        String signature = methodInfo.getSignature();
        
        // Assert
        assertEquals("boolean com.example.TestClass.testMethod(String param1, int param2)", signature);
    }
    
    @Test
    void toString_shouldIncludeSignature() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo(
            "com.example.TestClass", 
            "testMethod", 
            "int", 
            Collections.singletonList(new ParameterInfo("param", "double"))
        );
        
        // Act
        String result = methodInfo.toString();
        
        // Assert
        assertTrue(result.contains("int com.example.TestClass.testMethod(double param)"));
    }
}