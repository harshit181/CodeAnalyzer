package com.example;

/**
 * A sample class for testing the code analyzer
 */
public class Sample {
    
    /**
     * Sample method with different parameter types
     */
    public String process(String text, int count, boolean flag) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        if (count <= 0) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (flag) {
                result.append(text.toUpperCase());
            } else {
                result.append(text.toLowerCase());
            }
        }
        
        return result.toString();
    }
    
    /**
     * Another method with a list parameter
     */
    public boolean checkList(java.util.List<String> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        
        for (String item : items) {
            if (item == null || item.isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
}
