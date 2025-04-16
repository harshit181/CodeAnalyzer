package com.codeanalyzer.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents information about a method in the analyzed code.
 */
public class MethodInfo {
    private final String className;
    private final String methodName;
    private final String returnType;
    private final List<ParameterInfo> parameters;
    
    public MethodInfo(String className, String methodName, String returnType, List<ParameterInfo> parameters) {
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameters = parameters;
    }
    
    /**
     * @return The name of the class containing this method.
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * @return The name of the method.
     */
    public String getMethodName() {
        return methodName;
    }
    
    /**
     * @return The return type of the method.
     */
    public String getReturnType() {
        return returnType;
    }
    
    /**
     * @return The list of parameters for this method.
     */
    public List<ParameterInfo> getParameters() {
        return parameters;
    }
    
    /**
     * @return A formatted method signature including return type, name, and parameters.
     */
    public String getSignature() {
        String paramString = parameters.stream()
                .map(param -> param.getType() + " " + param.getName())
                .collect(Collectors.joining(", "));
        
        return returnType + " " + className + "." + methodName + "(" + paramString + ")";
    }
    
    /**
     * @return The list of parameters that are used in conditions within the method.
     */
    public List<ParameterInfo> getParametersUsedInConditions() {
        return parameters.stream()
                .filter(ParameterInfo::isUsedInCondition)
                .collect(Collectors.toList());
    }
    
    /**
     * @return The number of parameters for this method.
     */
    public int getParameterCount() {
        return parameters.size();
    }
    
    /**
     * @return True if the method has no parameters, false otherwise.
     */
    public boolean hasNoParameters() {
        return parameters.isEmpty();
    }
    
    /**
     * @return True if the method returns void, false otherwise.
     */
    public boolean isVoidReturn() {
        return "void".equals(returnType);
    }
    
    /**
     * @return A string representation of this method info.
     */
    @Override
    public String toString() {
        return getSignature();
    }
}
