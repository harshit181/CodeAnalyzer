package com.codeanalyzer.generator;

import com.codeanalyzer.model.MethodInfo;
import com.codeanalyzer.model.ParameterInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestRequestGeneratorTest {
    private TestRequestGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new TestRequestGenerator();
    }

    @Test
    void generateTestCases_withNoParameters_shouldReturnSingleTestCase() {
        // Arrange
        MethodInfo methodInfo = new MethodInfo(
            "com.example.TestClass",
            "simpleMethod",
            "void",
            Collections.emptyList()
        );

        // Act
        List<TestCase> testCases = generator.generateTestCases(methodInfo);

        // Assert
        assertNotNull(testCases);
        assertEquals(1, testCases.size());
        
        TestCase testCase = testCases.get(0);
        assertEquals("Basic invocation with no parameters", testCase.getDescription());
        assertTrue(testCase.getParameterValues().isEmpty());
        assertEquals("Method completes successfully", testCase.getExpectedBehavior());
    }

    @Test
    void generateTestCases_withOneParameter_shouldGenerateBasicTestCases() {
        // Arrange
        ParameterInfo param = new ParameterInfo("text", "String");
        param.setUsedInCondition(true);
        
        MethodInfo methodInfo = new MethodInfo(
            "com.example.TestClass",
            "processText",
            "String",
            Collections.singletonList(param)
        );

        // Act
        List<TestCase> testCases = generator.generateTestCases(methodInfo);

        // Assert
        assertNotNull(testCases);
        assertTrue(testCases.size() >= 3); // should have happy path, null, empty, etc.
        
        // Verify we have different test cases
        boolean hasHappyPath = false;
        boolean hasEmptyString = false;
        boolean hasNullTest = false;
        boolean hasSpecialChars = false;
        
        for (TestCase testCase : testCases) {
            String description = testCase.getDescription().toLowerCase();
            if (description.contains("happy path")) {
                hasHappyPath = true;
            } else if (description.contains("empty string")) {
                hasEmptyString = true;
            } else if (description.contains("null value")) {
                hasNullTest = true;
            } else if (description.contains("special characters")) {
                hasSpecialChars = true;
            }
        }
        
        assertTrue(hasHappyPath, "Should include a happy path test case");
        assertTrue(hasEmptyString, "Should include a test case with empty string");
        assertTrue(hasNullTest, "Should include a test case with null value");
        assertTrue(hasSpecialChars, "Should include a test case with special characters");
    }

    @Test
    void generateTestCases_withMultipleParameters_shouldGenerateCombinations() {
        // Arrange
        ParameterInfo stringParam = new ParameterInfo("text", "String");
        stringParam.setUsedInCondition(true);
        
        ParameterInfo intParam = new ParameterInfo("count", "int");
        intParam.setUsedInCondition(true);
        
        ParameterInfo boolParam = new ParameterInfo("flag", "boolean");
        boolParam.setUsedInCondition(true);
        
        MethodInfo methodInfo = new MethodInfo(
            "com.example.TestClass",
            "processWithParams",
            "String",
            Arrays.asList(stringParam, intParam, boolParam)
        );

        // Act
        List<TestCase> testCases = generator.generateTestCases(methodInfo);

        // Assert
        assertNotNull(testCases);
        assertTrue(testCases.size() >= 8); // Should have enough combinations
        
        // Check for specific combinations
        boolean hasPositiveTest = false;
        boolean hasZeroTest = false;
        boolean hasNegativeTest = false;
        boolean hasTrueTest = false;
        boolean hasFalseTest = false;
        
        for (TestCase testCase : testCases) {
            String description = testCase.getDescription().toLowerCase();
            Map<ParameterInfo, Object> values = testCase.getParameterValues();
            
            if (description.contains(intParam.getName()) && description.contains("> 0")) {
                hasPositiveTest = true;
            } else if (description.contains(intParam.getName()) && description.contains("= 0")) {
                hasZeroTest = true;
            } else if (description.contains(intParam.getName()) && description.contains("< 0")) {
                hasNegativeTest = true;
            } else if (description.contains(boolParam.getName()) && description.contains("= true")) {
                hasTrueTest = true;
            } else if (description.contains(boolParam.getName()) && description.contains("= false")) {
                hasFalseTest = true;
            }
        }
        
        assertTrue(hasPositiveTest, "Should include a test case with positive int");
        assertTrue(hasZeroTest, "Should include a test case with zero int");
        assertTrue(hasNegativeTest, "Should include a test case with negative int");
        assertTrue(hasTrueTest, "Should include a test case with true boolean");
        assertTrue(hasFalseTest, "Should include a test case with false boolean");
    }

    @Test
    void generateTestCases_withCustomTypes_shouldHandleAppropriately() {
        // Arrange
        ParameterInfo customParam = new ParameterInfo("data", "com.example.CustomType");
        customParam.setUsedInCondition(true);
        
        MethodInfo methodInfo = new MethodInfo(
            "com.example.TestClass",
            "processCustom",
            "void",
            Collections.singletonList(customParam)
        );

        // Act
        List<TestCase> testCases = generator.generateTestCases(methodInfo);

        // Assert
        assertNotNull(testCases);
        assertTrue(testCases.size() >= 2);
        
        boolean hasHappyPath = false;
        boolean hasNullTest = false;
        
        for (TestCase testCase : testCases) {
            String description = testCase.getDescription().toLowerCase();
            
            if (description.contains("happy path")) {
                hasHappyPath = true;
                assertNotNull(testCase.getParameterValues().get(customParam));
            } else if (description.contains("null value")) {
                hasNullTest = true;
                assertNull(testCase.getParameterValues().get(customParam));
            }
        }
        
        assertTrue(hasHappyPath, "Should include a happy path test case with custom object");
        assertTrue(hasNullTest, "Should include a test case with null custom object");
    }
    
    @Test
    void generateTestCases_withEdgeCases_shouldIncludeEdgeCaseTest() {
        // Arrange
        ParameterInfo intParam = new ParameterInfo("count", "int");
        ParameterInfo stringParam = new ParameterInfo("name", "String");
        
        MethodInfo methodInfo = new MethodInfo(
            "com.example.TestClass",
            "processWithMultipleParams",
            "String",
            Arrays.asList(intParam, stringParam)
        );

        // Act
        List<TestCase> testCases = generator.generateTestCases(methodInfo);

        // Assert
        boolean hasEdgeCaseTest = false;
        
        for (TestCase testCase : testCases) {
            if (testCase.getDescription().contains("Edge case")) {
                hasEdgeCaseTest = true;
                break;
            }
        }
        
        assertTrue(hasEdgeCaseTest, "Should include an edge case test");
    }
}