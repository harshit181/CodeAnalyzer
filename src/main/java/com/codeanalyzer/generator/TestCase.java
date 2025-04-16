package com.codeanalyzer.generator;

import com.codeanalyzer.model.MethodInfo;
import com.codeanalyzer.model.ParameterInfo;

import java.util.Map;

/**
 * Represents a test case for a method, including parameter values and expected behavior.
 */
public class TestCase {
    private final MethodInfo methodInfo;
    private final String description;
    private final Map<ParameterInfo, Object> parameterValues;
    private final String expectedBehavior;
    
    public TestCase(MethodInfo methodInfo, String description, 
                   Map<ParameterInfo, Object> parameterValues, 
                   String expectedBehavior) {
        this.methodInfo = methodInfo;
        this.description = description;
        this.parameterValues = parameterValues;
        this.expectedBehavior = expectedBehavior;
    }
    
    /**
     * @return The method this test case is for.
     */
    public MethodInfo getMethodInfo() {
        return methodInfo;
    }
    
    /**
     * @return A description of this test case.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @return The parameter values for this test case.
     */
    public Map<ParameterInfo, Object> getParameterValues() {
        return parameterValues;
    }
    
    /**
     * @return The expected behavior for this test case.
     */
    public String getExpectedBehavior() {
        return expectedBehavior;
    }
    
    /**
     * @return A string representation of this test case.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Test case: ").append(description).append("\n");
        sb.append("Method: ").append(methodInfo.getSignature()).append("\n");
        sb.append("Parameters:").append("\n");
        
        for (Map.Entry<ParameterInfo, Object> entry : parameterValues.entrySet()) {
            sb.append("  ")
              .append(entry.getKey().getName())
              .append(" (").append(entry.getKey().getType()).append(")")
              .append(" = ")
              .append(formatValue(entry.getValue()))
              .append("\n");
        }
        
        sb.append("Expected behavior: ").append(expectedBehavior);
        
        return sb.toString();
    }
    
    /**
     * Formats a parameter value for display.
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + value + "\"";
        } else {
            return value.toString();
        }
    }
}
