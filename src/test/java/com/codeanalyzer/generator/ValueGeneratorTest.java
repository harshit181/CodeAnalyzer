package com.codeanalyzer.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValueGeneratorTest {
    private ValueGenerator valueGenerator;

    @BeforeEach
    void setUp() {
        valueGenerator = new ValueGenerator();
    }

    @Test
    void generateValidValue_withString_shouldReturnStringValue() {
        // Act
        Object value = valueGenerator.generateValidValue("String");
        
        // Assert
        assertNotNull(value);
        assertTrue(value instanceof String);
        assertFalse(((String) value).isEmpty());
    }
    
    @Test
    void generateValidValue_withInt_shouldReturnIntValue() {
        // Act
        Object value = valueGenerator.generateValidValue("int");
        
        // Assert
        assertNotNull(value);
        assertTrue(value instanceof Integer);
    }
    
    @Test
    void generateValidValue_withBoolean_shouldReturnBooleanValue() {
        // Act
        Object value = valueGenerator.generateValidValue("boolean");
        
        // Assert
        assertNotNull(value);
        assertTrue(value instanceof Boolean);
    }
    
    @Test
    void generateValidValue_withCollection_shouldReturnCollectionValue() {
        // Act
        Object listValue = valueGenerator.generateValidValue("List<String>");
        Object arrayValue = valueGenerator.generateValidValue("String[]");
        Object setVarlue = valueGenerator.generateValidValue("Set<Integer>");
        Object mapValue = valueGenerator.generateValidValue("Map<String, Integer>");
        
        // Assert
        assertTrue(listValue instanceof List);
        assertTrue(arrayValue instanceof List);
        assertTrue(setVarlue instanceof Set);
        assertTrue(mapValue instanceof Map);
    }
    
    @Test
    void generateValidValue_withCustomType_shouldReturnPlaceholder() {
        // Act
        Object value = valueGenerator.generateValidValue("com.example.CustomType");
        
        // Assert
        assertNotNull(value);
        assertTrue(value instanceof String);
        assertTrue(((String) value).contains("new com.example.CustomType()"));
    }
    
    @Test
    void generateEdgeCaseValue_withNumericTypes_shouldReturnMaxValues() {
        // Act
        Object intValue = valueGenerator.generateEdgeCaseValue("int");
        Object longValue = valueGenerator.generateEdgeCaseValue("long");
        Object doubleValue = valueGenerator.generateEdgeCaseValue("double");
        Object floatValue = valueGenerator.generateEdgeCaseValue("float");
        
        // Assert
        assertEquals(Integer.MAX_VALUE, intValue);
        assertEquals(Long.MAX_VALUE, longValue);
        assertEquals(Double.MAX_VALUE, doubleValue);
        assertEquals(Float.MAX_VALUE, floatValue);
    }
    
    @Test
    void generateEdgeCaseValue_withStringAndCollections_shouldReturnEmptyValues() {
        // Act
        Object stringValue = valueGenerator.generateEdgeCaseValue("String");
        Object listValue = valueGenerator.generateEdgeCaseValue("List<String>");
        Object mapValue = valueGenerator.generateEdgeCaseValue("Map<String, String>");
        
        // Assert
        assertEquals("", stringValue);
        assertTrue(((List<?>) listValue).isEmpty());
        assertTrue(((Map<?, ?>) mapValue).isEmpty());
    }
    
    @Test
    void generatePositiveValue_withNumericTypes_shouldReturnPositiveValues() {
        // Act
        Object intValue = valueGenerator.generatePositiveValue("int");
        Object longValue = valueGenerator.generatePositiveValue("long");
        Object doubleValue = valueGenerator.generatePositiveValue("double");
        
        // Assert
        assertTrue((Integer) intValue > 0);
        assertTrue((Long) longValue > 0);
        assertTrue((Double) doubleValue > 0);
    }
    
    @Test
    void generatePositiveValue_withInvalidType_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            valueGenerator.generatePositiveValue("String");
        });
    }
    
    @Test
    void generateNegativeValue_withNumericTypes_shouldReturnNegativeValues() {
        // Act
        Object intValue = valueGenerator.generateNegativeValue("int");
        Object longValue = valueGenerator.generateNegativeValue("long");
        Object doubleValue = valueGenerator.generateNegativeValue("double");
        
        // Assert
        assertTrue((Integer) intValue < 0);
        assertTrue((Long) longValue < 0);
        assertTrue((Double) doubleValue < 0);
    }
    
    @Test
    void generateNegativeValue_withInvalidType_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            valueGenerator.generateNegativeValue("String");
        });
    }
    
    @Test
    void generateLongString_shouldReturnLongString() {
        // Act
        String value = valueGenerator.generateLongString();
        
        // Assert
        assertNotNull(value);
        assertTrue(value.length() >= 1000);
    }
    
    @Test
    void generateStringWithSpecialChars_shouldReturnStringWithSpecialChars() {
        // Act
        String value = valueGenerator.generateStringWithSpecialChars();
        
        // Assert
        assertNotNull(value);
        assertTrue(value.contains("!") && value.contains("@") && value.contains("#"));
    }
    
    @Test
    void generateEmptyCollection_shouldReturnEmptyCollection() {
        // Act
        Object listValue = valueGenerator.generateEmptyCollection("List<String>");
        Object setVarlue = valueGenerator.generateEmptyCollection("Set<Integer>");
        Object mapValue = valueGenerator.generateEmptyCollection("Map<String, Integer>");
        
        // Assert
        assertTrue(((List<?>) listValue).isEmpty());
        assertTrue(((Set<?>) setVarlue).isEmpty());
        assertTrue(((Map<?, ?>) mapValue).isEmpty());
    }
    
    @Test
    void generateEmptyCollection_withInvalidType_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            valueGenerator.generateEmptyCollection("String");
        });
    }
    
    @Test
    void generateCollectionWithMultipleItems_shouldReturnNonEmptyCollection() {
        // Act
        Object listValue = valueGenerator.generateCollectionWithMultipleItems("List<String>");
        Object setVarlue = valueGenerator.generateCollectionWithMultipleItems("Set<Integer>");
        Object mapValue = valueGenerator.generateCollectionWithMultipleItems("Map<String, Integer>");
        
        // Assert
        assertFalse(((List<?>) listValue).isEmpty());
        assertTrue(((List<?>) listValue).size() > 1);
        
        assertFalse(((Set<?>) setVarlue).isEmpty());
        assertTrue(((Set<?>) setVarlue).size() > 1);
        
        assertFalse(((Map<?, ?>) mapValue).isEmpty());
        assertTrue(((Map<?, ?>) mapValue).size() > 1);
    }
    
    @Test
    void generateCollectionWithMultipleItems_withInvalidType_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            valueGenerator.generateCollectionWithMultipleItems("String");
        });
    }
}