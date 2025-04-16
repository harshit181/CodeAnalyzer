package com.codeanalyzer.analyzer;

import com.codeanalyzer.model.MethodInfo;
import com.codeanalyzer.model.ParameterInfo;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MethodAnalyzerTest {
    private MethodAnalyzer methodAnalyzer;
    private JavaParser javaParser;

    @BeforeEach
    void setUp() {
        methodAnalyzer = new MethodAnalyzer();
        javaParser = new JavaParser();
    }

    @Test
    void analyzeMethod_withSimpleMethod_shouldReturnCorrectInfo() {
        // Arrange
        String code = "public class TestClass { public void simpleMethod() { } }";
        MethodDeclaration methodDeclaration = parseMethod(code);

        // Act
        MethodInfo methodInfo = methodAnalyzer.analyzeMethod(methodDeclaration, "com.example.TestClass");

        // Assert
        assertNotNull(methodInfo);
        assertEquals("com.example.TestClass", methodInfo.getClassName());
        assertEquals("simpleMethod", methodInfo.getMethodName());
        assertEquals("void", methodInfo.getReturnType());
        assertTrue(methodInfo.getParameters().isEmpty());
        assertEquals("void com.example.TestClass.simpleMethod()", methodInfo.getSignature());
    }

    @Test
    void analyzeMethod_withParameters_shouldReturnCorrectParameterInfo() {
        // Arrange
        String code = "public class TestClass { public int calculate(int a, String b, boolean c) { return 0; } }";
        MethodDeclaration methodDeclaration = parseMethod(code);

        // Act
        MethodInfo methodInfo = methodAnalyzer.analyzeMethod(methodDeclaration, "com.example.TestClass");

        // Assert
        assertNotNull(methodInfo);
        assertEquals("calculate", methodInfo.getMethodName());
        assertEquals("int", methodInfo.getReturnType());
        
        List<ParameterInfo> parameters = methodInfo.getParameters();
        assertEquals(3, parameters.size());
        
        assertEquals("a", parameters.get(0).getName());
        assertEquals("int", parameters.get(0).getType());
        assertFalse(parameters.get(0).isUsedInCondition());
        
        assertEquals("b", parameters.get(1).getName());
        assertEquals("String", parameters.get(1).getType());
        assertFalse(parameters.get(1).isUsedInCondition());
        
        assertEquals("c", parameters.get(2).getName());
        assertEquals("boolean", parameters.get(2).getType());
        assertFalse(parameters.get(2).isUsedInCondition());
    }

    @Test
    void analyzeMethod_withConditions_shouldIdentifyParametersInConditions() {
        // Arrange
        String code = 
            "public class TestClass { " +
            "   public String test(String text, int count) { " +
            "       if (text != null && count > 0) { " +
            "           return text.repeat(count); " +
            "       } " +
            "       return \"\"; " +
            "   } " +
            "}";
        MethodDeclaration methodDeclaration = parseMethod(code);

        // Act
        MethodInfo methodInfo = methodAnalyzer.analyzeMethod(methodDeclaration, "com.example.TestClass");

        // Assert
        List<ParameterInfo> parameters = methodInfo.getParameters();
        assertEquals(2, parameters.size());
        
        assertEquals("text", parameters.get(0).getName());
        assertTrue(parameters.get(0).isUsedInCondition());
        
        assertEquals("count", parameters.get(1).getName());
        assertTrue(parameters.get(1).isUsedInCondition());
    }

    @Test
    void analyzeMethod_withComplexControlFlow_shouldIdentifyAllConditionVariables() {
        // Arrange
        String code = 
            "public class TestClass { " +
            "   public String complexMethod(String input, int max, boolean flag) { " +
            "       StringBuilder result = new StringBuilder(); " +
            "       " +
            "       if (input == null) { " +
            "           return \"\"; " +
            "       } " +
            "       " +
            "       for (int i = 0; i < max; i++) { " +
            "           char c = input.charAt(i % input.length()); " +
            "           if (flag) { " +
            "               result.append(Character.toUpperCase(c)); " +
            "           } else { " +
            "               result.append(c); " +
            "           } " +
            "       } " +
            "       " +
            "       switch(result.length()) { " +
            "           case 0: return \"empty\"; " +
            "           case 1: return \"single\"; " +
            "           default: return result.toString(); " +
            "       } " +
            "   } " +
            "}";
        MethodDeclaration methodDeclaration = parseMethod(code);

        // Act
        MethodInfo methodInfo = methodAnalyzer.analyzeMethod(methodDeclaration, "com.example.TestClass");

        // Assert
        List<ParameterInfo> parameters = methodInfo.getParameters();
        assertEquals(3, parameters.size());
        
        assertEquals("input", parameters.get(0).getName());
        assertTrue(parameters.get(0).isUsedInCondition());
        
        assertEquals("max", parameters.get(1).getName());
        assertTrue(parameters.get(1).isUsedInCondition());
        
        assertEquals("flag", parameters.get(2).getName());
        assertTrue(parameters.get(2).isUsedInCondition());
    }

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = javaParser.parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }
}