package com.codeanalyzer.model;

/**
 * Represents information about a method parameter.
 */
public class ParameterInfo {
    private final String name;
    private final String type;
    private boolean usedInCondition;
    
    public ParameterInfo(String name, String type) {
        this.name = name;
        this.type = type;
        this.usedInCondition = false;
    }
    
    /**
     * @return The name of the parameter.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return The type of the parameter.
     */
    public String getType() {
        return type;
    }
    
    /**
     * @return True if this parameter is used in a condition within the method, false otherwise.
     */
    public boolean isUsedInCondition() {
        return usedInCondition;
    }
    
    /**
     * Sets whether this parameter is used in a condition.
     */
    public void setUsedInCondition(boolean usedInCondition) {
        this.usedInCondition = usedInCondition;
    }
    
    /**
     * @return True if this parameter is a primitive type, false otherwise.
     */
    public boolean isPrimitive() {
        return isPrimitiveType(type);
    }
    
    /**
     * @return True if this parameter is a String, false otherwise.
     */
    public boolean isString() {
        return "String".equals(type) || "java.lang.String".equals(type);
    }
    
    /**
     * @return True if this parameter is a numeric primitive type, false otherwise.
     */
    public boolean isNumeric() {
        return "int".equals(type) || "double".equals(type) || "float".equals(type) || 
               "long".equals(type) || "short".equals(type) || "byte".equals(type) ||
               "Integer".equals(type) || "Double".equals(type) || "Float".equals(type) ||
               "Long".equals(type) || "Short".equals(type) || "Byte".equals(type);
    }
    
    /**
     * @return True if this parameter is a boolean type, false otherwise.
     */
    public boolean isBoolean() {
        return "boolean".equals(type) || "Boolean".equals(type);
    }
    
    /**
     * @return True if this parameter is a collection type, false otherwise.
     */
    public boolean isCollection() {
        return type.contains("List") || type.contains("Set") || type.contains("Map") ||
               type.contains("Collection") || type.contains("[]");
    }
    
    /**
     * Checks if a type name represents a primitive type.
     */
    private boolean isPrimitiveType(String typeName) {
        return "int".equals(typeName) || "double".equals(typeName) || "float".equals(typeName) ||
               "long".equals(typeName) || "short".equals(typeName) || "byte".equals(typeName) ||
               "char".equals(typeName) || "boolean".equals(typeName);
    }
    
    /**
     * @return A string representation of this parameter.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" ").append(name);
        
        if (usedInCondition) {
            sb.append(" (used in condition)");
        }
        
        return sb.toString();
    }
}
