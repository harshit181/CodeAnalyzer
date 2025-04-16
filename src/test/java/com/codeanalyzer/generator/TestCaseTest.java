package com.codeanalyzer.generator;

import com.codeanalyzer.model.MethodInfo;
import com.codeanalyzer.model.ParameterInfo;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestCaseTest {

    @Test
    void constructor_withAllParameters_shouldCreateInstance() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo("TestClass", "testMethod", "void", Collections.emptyList());
        String description = "Test with null input";
        Map<ParameterInfo, Object> parameterValues = new HashMap<>();
        String expectedBehavior = "Should execute successfully";
        
        // Act
        TestCase testCase = new TestCase(methodInfo, description, parameterValues, expectedBehavior);
        
        // Assert
        assertNotNull(testCase);
        assertEquals(methodInfo, testCase.getMethodInfo());
        assertEquals(description, testCase.getDescription());
        assertEquals(parameterValues, testCase.getParameterValues());
        assertEquals(expectedBehavior, testCase.getExpectedBehavior());
    }
    
    @Test
    void getParameterValues_shouldReturnMap() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo("TestClass", "testMethod", "String", Collections.emptyList());
        String description = "Test case";
        
        ParameterInfo parameter = new ParameterInfo("text", "String");
        Map<ParameterInfo, Object> parameterValues = new HashMap<>();
        parameterValues.put(parameter, "sample text");
        
        String expectedBehavior = "Should return uppercase text";
        
        // Act
        TestCase testCase = new TestCase(methodInfo, description, parameterValues, expectedBehavior);
        
        // Assert
        assertEquals(1, testCase.getParameterValues().size());
        assertEquals("sample text", testCase.getParameterValues().get(parameter));
    }
    
    @Test
    void getParameterValues_withNullValue_shouldStoreNullValue() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo("TestClass", "testMethod", "void", Collections.emptyList());
        String description = "Test with null parameter";
        
        ParameterInfo parameter = new ParameterInfo("data", "Object");
        Map<ParameterInfo, Object> parameterValues = new HashMap<>();
        parameterValues.put(parameter, null);
        
        String expectedBehavior = "Should handle null input";
        
        // Act
        TestCase testCase = new TestCase(methodInfo, description, parameterValues, expectedBehavior);
        
        // Assert
        assertTrue(testCase.getParameterValues().containsKey(parameter));
        assertNull(testCase.getParameterValues().get(parameter));
    }
    
    @Test
    void getExpectedBehavior_shouldReturnExpectedBehavior() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo("TestClass", "testMethod", "void", Collections.emptyList());
        String description = "Normal behavior test";
        Map<ParameterInfo, Object> parameterValues = new HashMap<>();
        String expectedBehavior = "Should return uppercase text";
        
        // Act
        TestCase testCase = new TestCase(methodInfo, description, parameterValues, expectedBehavior);
        
        // Assert
        assertEquals(expectedBehavior, testCase.getExpectedBehavior());
    }
    
    @Test
    void multipleParameterValues_shouldAllBeStored() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo("TestClass", "complexMethod", "String", Collections.emptyList());
        String description = "Multi-parameter test";
        
        ParameterInfo param1 = new ParameterInfo("text", "String");
        ParameterInfo param2 = new ParameterInfo("count", "int");
        ParameterInfo param3 = new ParameterInfo("flag", "boolean");
        
        Map<ParameterInfo, Object> parameterValues = new HashMap<>();
        parameterValues.put(param1, "test");
        parameterValues.put(param2, 5);
        parameterValues.put(param3, true);
        
        String expectedBehavior = "Should process values";
        
        // Act
        TestCase testCase = new TestCase(methodInfo, description, parameterValues, expectedBehavior);
        
        // Assert
        assertEquals(3, testCase.getParameterValues().size());
        assertEquals("test", testCase.getParameterValues().get(param1));
        assertEquals(5, testCase.getParameterValues().get(param2));
        assertEquals(true, testCase.getParameterValues().get(param3));
    }
    
    @Test
    void toString_shouldIncludeDescriptionAndParameters() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo("TestClass", "testMethod", "String", Collections.emptyList());
        String description = "String representation test";
        
        ParameterInfo param = new ParameterInfo("text", "String");
        Map<ParameterInfo, Object> parameterValues = new HashMap<>();
        parameterValues.put(param, "value");
        
        String expectedBehavior = "Should process text";
        
        // Act
        TestCase testCase = new TestCase(methodInfo, description, parameterValues, expectedBehavior);
        String result = testCase.toString();
        
        // Assert
        assertTrue(result.contains("String representation test"));
        assertTrue(result.contains("text"));
        assertTrue(result.contains("value"));
        assertTrue(result.contains("Should process text"));
    }
    
    @Test
    void toString_withNullParameter_shouldFormatValueCorrectly() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo("TestClass", "processNullable", "String", Collections.emptyList());
        String description = "Null parameter test";
        
        ParameterInfo param = new ParameterInfo("data", "Object");
        Map<ParameterInfo, Object> parameterValues = new HashMap<>();
        parameterValues.put(param, null);
        
        String expectedBehavior = "Should handle null gracefully";
        
        // Act
        TestCase testCase = new TestCase(methodInfo, description, parameterValues, expectedBehavior);
        String result = testCase.toString();
        
        // Assert
        assertTrue(result.contains("Null parameter test"));
        assertTrue(result.contains("data"));
        assertTrue(result.contains("null"));
        assertTrue(result.contains("Should handle null gracefully"));
    }
    
    @Test
    void toString_withStringParameter_shouldFormatValueWithQuotes() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo("TestClass", "processString", "String", Collections.emptyList());
        String description = "String parameter test";
        
        ParameterInfo param = new ParameterInfo("text", "String");
        Map<ParameterInfo, Object> parameterValues = new HashMap<>();
        parameterValues.put(param, "Hello, world!");
        
        String expectedBehavior = "Should process string";
        
        // Act
        TestCase testCase = new TestCase(methodInfo, description, parameterValues, expectedBehavior);
        String result = testCase.toString();
        
        // Assert
        assertTrue(result.contains("String parameter test"));
        assertTrue(result.contains("text"));
        assertTrue(result.contains("\"Hello, world!\""));
        assertTrue(result.contains("Should process string"));
    }
    
    @Test
    void toString_withNonStringParameter_shouldUseToString() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo("TestClass", "processNumber", "int", Collections.emptyList());
        String description = "Numeric parameter test";
        
        ParameterInfo param = new ParameterInfo("value", "int");
        Map<ParameterInfo, Object> parameterValues = new HashMap<>();
        parameterValues.put(param, 42);
        
        String expectedBehavior = "Should process number";
        
        // Act
        TestCase testCase = new TestCase(methodInfo, description, parameterValues, expectedBehavior);
        String result = testCase.toString();
        
        // Assert
        assertTrue(result.contains("Numeric parameter test"));
        assertTrue(result.contains("value"));
        assertTrue(result.contains("42"));
        assertTrue(result.contains("Should process number"));
    }
}