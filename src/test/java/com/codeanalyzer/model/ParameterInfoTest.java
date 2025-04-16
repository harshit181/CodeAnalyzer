package com.codeanalyzer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParameterInfoTest {

    @Test
    void constructor_withNameAndType_shouldCreateInstance() {
        // Arrange
        String name = "testParam";
        String type = "String";
        
        // Act
        ParameterInfo parameterInfo = new ParameterInfo(name, type);
        
        // Assert
        assertNotNull(parameterInfo);
        assertEquals(name, parameterInfo.getName());
        assertEquals(type, parameterInfo.getType());
        assertFalse(parameterInfo.isUsedInCondition());
    }
    
    @Test
    void isUsedInCondition_initialValue_shouldBeFalse() {
        // Arrange & Act
        ParameterInfo parameterInfo = new ParameterInfo("param", "int");
        
        // Assert
        assertFalse(parameterInfo.isUsedInCondition());
    }
    
    @Test
    void setUsedInCondition_whenTrue_shouldUpdateState() {
        // Arrange
        ParameterInfo parameterInfo = new ParameterInfo("param", "boolean");
        
        // Act
        parameterInfo.setUsedInCondition(true);
        
        // Assert
        assertTrue(parameterInfo.isUsedInCondition());
    }
    
    @Test
    void setUsedInCondition_whenFalse_shouldUpdateState() {
        // Arrange
        ParameterInfo parameterInfo = new ParameterInfo("param", "double");
        parameterInfo.setUsedInCondition(true);
        
        // Act
        parameterInfo.setUsedInCondition(false);
        
        // Assert
        assertFalse(parameterInfo.isUsedInCondition());
    }
    
    @Test
    void toString_shouldIncludeNameAndType() {
        // Arrange
        ParameterInfo parameterInfo = new ParameterInfo("count", "int");
        
        // Act
        String result = parameterInfo.toString();
        
        // Assert
        assertTrue(result.contains("count"));
        assertTrue(result.contains("int"));
    }
    
    @Test
    void toString_whenUsedInCondition_shouldIndicateThat() {
        // Arrange
        ParameterInfo parameterInfo = new ParameterInfo("flag", "boolean");
        parameterInfo.setUsedInCondition(true);
        
        // Act
        String result = parameterInfo.toString();
        
        // Assert
        assertTrue(result.contains("used in condition"));
    }
}