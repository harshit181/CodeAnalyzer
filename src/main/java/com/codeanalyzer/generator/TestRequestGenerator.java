package com.codeanalyzer.generator;

import com.codeanalyzer.model.MethodInfo;
import com.codeanalyzer.model.ParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Generates test case request objects for a given method.
 */
public class TestRequestGenerator {
    private static final Logger logger = LoggerFactory.getLogger(TestRequestGenerator.class);
    private final ValueGenerator valueGenerator;
    
    public TestRequestGenerator() {
        this.valueGenerator = new ValueGenerator();
    }
    
    /**
     * Generates test cases for a method.
     * 
     * @param methodInfo The method information.
     * @return A list of test cases.
     */
    public List<TestCase> generateTestCases(MethodInfo methodInfo) {
        List<TestCase> testCases = new ArrayList<>();
        
        // If no parameters, just create a single test case
        if (methodInfo.hasNoParameters()) {
            testCases.add(createBasicTestCase(methodInfo, "Basic invocation with no parameters", 
                    Collections.emptyMap(), "Method completes successfully"));
            return testCases;
        }
        
        // Generate happy path test case with valid values
        Map<ParameterInfo, Object> happyPathValues = new HashMap<>();
        for (ParameterInfo param : methodInfo.getParameters()) {
            happyPathValues.put(param, valueGenerator.generateValidValue(param.getType()));
        }
        testCases.add(createBasicTestCase(methodInfo, "Happy path with valid values", 
                happyPathValues, "Method completes successfully"));
        
        // Generate test cases for parameters used in conditions
        for (ParameterInfo param : methodInfo.getParametersUsedInConditions()) {
            generateParameterVariationTestCases(methodInfo, param, testCases, happyPathValues);
        }
        
        // Generate test cases with null values for reference types
        for (ParameterInfo param : methodInfo.getParameters()) {
            if (!param.isPrimitive()) {
                Map<ParameterInfo, Object> nullTestValues = new HashMap<>(happyPathValues);
                nullTestValues.put(param, null);
                
                testCases.add(createBasicTestCase(methodInfo, 
                        "Null value for parameter: " + param.getName(),
                        nullTestValues,
                        "Expecting NullPointerException or special handling"));
            }
        }
        
        // Generate test case with edge case values
        Map<ParameterInfo, Object> edgeCaseValues = new HashMap<>();
        for (ParameterInfo param : methodInfo.getParameters()) {
            edgeCaseValues.put(param, valueGenerator.generateEdgeCaseValue(param.getType()));
        }
        testCases.add(createBasicTestCase(methodInfo, "Edge case values", 
                edgeCaseValues, "Testing boundary conditions"));
        
        return testCases;
    }
    
    /**
     * Generates test cases for a specific parameter with different values.
     */
    private void generateParameterVariationTestCases(MethodInfo methodInfo, ParameterInfo param, 
                                                    List<TestCase> testCases, 
                                                    Map<ParameterInfo, Object> baseValues) {
        if (param.isBoolean()) {
            // For boolean parameters, test both true and false
            Map<ParameterInfo, Object> trueValues = new HashMap<>(baseValues);
            trueValues.put(param, true);
            testCases.add(createBasicTestCase(methodInfo, 
                    "Parameter " + param.getName() + " = true",
                    trueValues, 
                    "Branch with " + param.getName() + " = true"));
            
            Map<ParameterInfo, Object> falseValues = new HashMap<>(baseValues);
            falseValues.put(param, false);
            testCases.add(createBasicTestCase(methodInfo, 
                    "Parameter " + param.getName() + " = false", 
                    falseValues,
                    "Branch with " + param.getName() + " = false"));
        } else if (param.isNumeric()) {
            // For numeric parameters, test different values
            Map<ParameterInfo, Object> positiveValues = new HashMap<>(baseValues);
            positiveValues.put(param, valueGenerator.generatePositiveValue(param.getType()));
            testCases.add(createBasicTestCase(methodInfo, 
                    "Parameter " + param.getName() + " > 0", 
                    positiveValues,
                    "Branch with positive " + param.getName()));
            
            Map<ParameterInfo, Object> zeroValues = new HashMap<>(baseValues);
            zeroValues.put(param, 0);
            testCases.add(createBasicTestCase(methodInfo, 
                    "Parameter " + param.getName() + " = 0", 
                    zeroValues,
                    "Branch with zero " + param.getName()));
            
            Map<ParameterInfo, Object> negativeValues = new HashMap<>(baseValues);
            negativeValues.put(param, valueGenerator.generateNegativeValue(param.getType()));
            testCases.add(createBasicTestCase(methodInfo, 
                    "Parameter " + param.getName() + " < 0", 
                    negativeValues,
                    "Branch with negative " + param.getName()));
        } else if (param.isString()) {
            // For string parameters, test with empty, null, and special values
            Map<ParameterInfo, Object> emptyStringValues = new HashMap<>(baseValues);
            emptyStringValues.put(param, "");
            testCases.add(createBasicTestCase(methodInfo, 
                    "Parameter " + param.getName() + " is empty string", 
                    emptyStringValues,
                    "Branch with empty " + param.getName()));
            
            Map<ParameterInfo, Object> longStringValues = new HashMap<>(baseValues);
            longStringValues.put(param, valueGenerator.generateLongString());
            testCases.add(createBasicTestCase(methodInfo, 
                    "Parameter " + param.getName() + " is long string", 
                    longStringValues,
                    "Testing with long " + param.getName()));
            
            Map<ParameterInfo, Object> specialCharValues = new HashMap<>(baseValues);
            specialCharValues.put(param, valueGenerator.generateStringWithSpecialChars());
            testCases.add(createBasicTestCase(methodInfo, 
                    "Parameter " + param.getName() + " has special characters", 
                    specialCharValues,
                    "Testing with special characters in " + param.getName()));
        } else if (param.isCollection()) {
            // For collection parameters, test with empty and multiple items
            Map<ParameterInfo, Object> emptyCollectionValues = new HashMap<>(baseValues);
            emptyCollectionValues.put(param, valueGenerator.generateEmptyCollection(param.getType()));
            testCases.add(createBasicTestCase(methodInfo, 
                    "Parameter " + param.getName() + " is empty collection", 
                    emptyCollectionValues,
                    "Branch with empty " + param.getName()));
            
            Map<ParameterInfo, Object> multipleItemValues = new HashMap<>(baseValues);
            multipleItemValues.put(param, valueGenerator.generateCollectionWithMultipleItems(param.getType()));
            testCases.add(createBasicTestCase(methodInfo, 
                    "Parameter " + param.getName() + " has multiple items", 
                    multipleItemValues,
                    "Testing with multiple items in " + param.getName()));
        }
    }
    
    /**
     * Creates a basic test case with the given information.
     */
    private TestCase createBasicTestCase(MethodInfo methodInfo, String description, 
                                          Map<ParameterInfo, Object> parameterValues, 
                                          String expectedBehavior) {
        return new TestCase(methodInfo, description, parameterValues, expectedBehavior);
    }
}
