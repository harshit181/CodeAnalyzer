package com.codeanalyzer.model;

import java.util.List;

/**
 * Represents the overall result of code analysis, containing information about all found methods.
 */
public class AnalysisResult {
    private final List<MethodInfo> methods;
    
    public AnalysisResult(List<MethodInfo> methods) {
        this.methods = methods;
    }
    
    /**
     * @return A list of all methods found during analysis.
     */
    public List<MethodInfo> getMethods() {
        return methods;
    }
    
    /**
     * @return The total number of methods found.
     */
    public int getMethodCount() {
        return methods.size();
    }
}
