package com.codeanalyzer.generator;

import java.util.*;

/**
 * Generates test values for parameters based on their types.
 */
public class ValueGenerator {
    private final Random random = new Random();
    
    /**
     * Generates a valid value for the given type.
     * 
     * @param type The parameter type.
     * @return A valid value for the type.
     */
    public Object generateValidValue(String type) {
        if ("boolean".equals(type) || "Boolean".equals(type)) {
            return true;
        } else if ("int".equals(type) || "Integer".equals(type)) {
            return 42;
        } else if ("long".equals(type) || "Long".equals(type)) {
            return 42L;
        } else if ("double".equals(type) || "Double".equals(type)) {
            return 42.0;
        } else if ("float".equals(type) || "Float".equals(type)) {
            return 42.0f;
        } else if ("short".equals(type) || "Short".equals(type)) {
            return (short) 42;
        } else if ("byte".equals(type) || "Byte".equals(type)) {
            return (byte) 42;
        } else if ("char".equals(type) || "Character".equals(type)) {
            return 'A';
        } else if ("String".equals(type) || "java.lang.String".equals(type)) {
            return "Valid string value";
        } else if (type.contains("List") || type.endsWith("[]")) {
            // For simplicity, create a list with one item
            return new ArrayList<>(Collections.singletonList("Item 1"));
        } else if (type.contains("Set")) {
            return new HashSet<>(Collections.singletonList("Item 1"));
        } else if (type.contains("Map")) {
            Map<String, String> map = new HashMap<>();
            map.put("key1", "value1");
            return map;
        } else {
            // For complex objects, return null which indicates a mock should be used
            return "new " + type + "()"; // This is a string representation for display only
        }
    }
    
    /**
     * Generates an edge case value for the given type.
     * 
     * @param type The parameter type.
     * @return An edge case value for the type.
     */
    public Object generateEdgeCaseValue(String type) {
        if ("boolean".equals(type) || "Boolean".equals(type)) {
            return false;
        } else if ("int".equals(type) || "Integer".equals(type)) {
            return Integer.MAX_VALUE;
        } else if ("long".equals(type) || "Long".equals(type)) {
            return Long.MAX_VALUE;
        } else if ("double".equals(type) || "Double".equals(type)) {
            return Double.MAX_VALUE;
        } else if ("float".equals(type) || "Float".equals(type)) {
            return Float.MAX_VALUE;
        } else if ("short".equals(type) || "Short".equals(type)) {
            return Short.MAX_VALUE;
        } else if ("byte".equals(type) || "Byte".equals(type)) {
            return Byte.MAX_VALUE;
        } else if ("char".equals(type) || "Character".equals(type)) {
            return Character.MAX_VALUE;
        } else if ("String".equals(type) || "java.lang.String".equals(type)) {
            return "";
        } else if (type.contains("List") || type.endsWith("[]")) {
            return new ArrayList<>();
        } else if (type.contains("Set")) {
            return new HashSet<>();
        } else if (type.contains("Map")) {
            return new HashMap<>();
        } else {
            return "new " + type + "()"; // This is a string representation for display only
        }
    }
    
    /**
     * Generates a positive value for a numeric type.
     * 
     * @param type The parameter type.
     * @return A positive value.
     */
    public Object generatePositiveValue(String type) {
        if ("int".equals(type) || "Integer".equals(type)) {
            return 100;
        } else if ("long".equals(type) || "Long".equals(type)) {
            return 100L;
        } else if ("double".equals(type) || "Double".equals(type)) {
            return 100.0;
        } else if ("float".equals(type) || "Float".equals(type)) {
            return 100.0f;
        } else if ("short".equals(type) || "Short".equals(type)) {
            return (short) 100;
        } else if ("byte".equals(type) || "Byte".equals(type)) {
            return (byte) 100;
        } else {
            throw new IllegalArgumentException("Not a numeric type: " + type);
        }
    }
    
    /**
     * Generates a negative value for a numeric type.
     * 
     * @param type The parameter type.
     * @return A negative value.
     */
    public Object generateNegativeValue(String type) {
        if ("int".equals(type) || "Integer".equals(type)) {
            return -100;
        } else if ("long".equals(type) || "Long".equals(type)) {
            return -100L;
        } else if ("double".equals(type) || "Double".equals(type)) {
            return -100.0;
        } else if ("float".equals(type) || "Float".equals(type)) {
            return -100.0f;
        } else if ("short".equals(type) || "Short".equals(type)) {
            return (short) -100;
        } else if ("byte".equals(type) || "Byte".equals(type)) {
            return (byte) -100;
        } else {
            throw new IllegalArgumentException("Not a numeric type: " + type);
        }
    }
    
    /**
     * Generates a long string (more than 1000 characters).
     * 
     * @return A long string.
     */
    public String generateLongString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        return sb.toString();
    }
    
    /**
     * Generates a string with special characters.
     * 
     * @return A string with special characters.
     */
    public String generateStringWithSpecialChars() {
        return "String with special characters: !@#$%^&*()_+{}[]|\\:;\"'<>,.?/";
    }
    
    /**
     * Generates an empty collection of the specified type.
     * 
     * @param type The collection type.
     * @return An empty collection.
     */
    public Object generateEmptyCollection(String type) {
        if (type.contains("List") || type.endsWith("[]")) {
            return new ArrayList<>();
        } else if (type.contains("Set")) {
            return new HashSet<>();
        } else if (type.contains("Map")) {
            return new HashMap<>();
        } else {
            throw new IllegalArgumentException("Not a collection type: " + type);
        }
    }
    
    /**
     * Generates a collection with multiple items.
     * 
     * @param type The collection type.
     * @return A collection with multiple items.
     */
    public Object generateCollectionWithMultipleItems(String type) {
        if (type.contains("List") || type.endsWith("[]")) {
            List<String> list = new ArrayList<>();
            list.add("Item 1");
            list.add("Item 2");
            list.add("Item 3");
            return list;
        } else if (type.contains("Set")) {
            Set<String> set = new HashSet<>();
            set.add("Item 1");
            set.add("Item 2");
            set.add("Item 3");
            return set;
        } else if (type.contains("Map")) {
            Map<String, String> map = new HashMap<>();
            map.put("key1", "value1");
            map.put("key2", "value2");
            map.put("key3", "value3");
            return map;
        } else {
            throw new IllegalArgumentException("Not a collection type: " + type);
        }
    }
}
