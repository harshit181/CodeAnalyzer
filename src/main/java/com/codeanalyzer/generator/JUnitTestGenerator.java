package com.codeanalyzer.generator;

import com.codeanalyzer.model.MethodInfo;
import com.codeanalyzer.model.ParameterInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates JUnit test files for analyzed Java classes.
 */
public class JUnitTestGenerator {
    private static final Logger logger = LoggerFactory.getLogger(JUnitTestGenerator.class);
    private final TestRequestGenerator testRequestGenerator;
    private final String outputDirectory;
    
    /**
     * Creates a new JUnit test generator.
     * 
     * @param outputDirectory The directory where test files will be created.
     */
    public JUnitTestGenerator(String outputDirectory) {
        this.testRequestGenerator = new TestRequestGenerator();
        this.outputDirectory = outputDirectory;
    }
    
    /**
     * Generates JUnit test files for the given methods.
     * 
     * @param methods The methods to generate tests for.
     * @return The number of test files generated.
     */
    public int generateTestFiles(List<MethodInfo> methods) {
        int generatedFiles = 0;
        
        try {
            // Create output directory if it doesn't exist
            Path outputPath = Paths.get(outputDirectory);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            
            // Group methods by class
            Map<String, List<MethodInfo>> methodsByClass = methods.stream()
                    .collect(java.util.stream.Collectors.groupingBy(MethodInfo::getClassName));
            
            // Generate a test file for each class
            for (Map.Entry<String, List<MethodInfo>> entry : methodsByClass.entrySet()) {
                String className = entry.getKey();
                List<MethodInfo> classMethods = entry.getValue();
                
                String testFileName = getTestFileName(className);
                Path testFilePath = outputPath.resolve(testFileName);
                
                String testFileContent = generateTestFileContent(className, classMethods);
                Files.writeString(testFilePath, testFileContent);
                
                generatedFiles++;
                logger.info("Generated test file: {}", testFilePath);
            }
        } catch (IOException e) {
            logger.error("Error generating test files", e);
        }
        
        return generatedFiles;
    }
    
    /**
     * Generates a test file for a single method.
     * 
     * @param method The method to generate tests for.
     * @return true if the file was generated successfully, false otherwise.
     */
    public boolean generateTestFile(MethodInfo method) {
        try {
            // Create output directory if it doesn't exist
            Path outputPath = Paths.get(outputDirectory);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            
            String className = method.getClassName();
            String testFileName = getTestFileName(className);
            Path testFilePath = outputPath.resolve(testFileName);
            
            String testFileContent = generateTestFileContent(className, List.of(method));
            Files.writeString(testFilePath, testFileContent);
            
            logger.info("Generated test file: {}", testFilePath);
            return true;
        } catch (IOException e) {
            logger.error("Error generating test file for method: {}", method.getSignature(), e);
            return false;
        }
    }
    
    /**
     * Gets the filename for a test class.
     */
    private String getTestFileName(String className) {
        // Extract the simple class name if it contains packages
        String simpleClassName = className;
        if (className.contains(".")) {
            simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        }
        
        return simpleClassName + "Test.java";
    }
    
    /**
     * Generates the content of a test file.
     */
    private String generateTestFileContent(String className, List<MethodInfo> methods) {
        StringBuilder sb = new StringBuilder();
        
        // Extract package name
        String packageName = "";
        if (className.contains(".")) {
            packageName = className.substring(0, className.lastIndexOf('.'));
        }
        
        // Extract simple class name
        String simpleClassName = className;
        if (className.contains(".")) {
            simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        }
        
        // Generate package declaration
        if (!packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }
        
        // Generate imports
        sb.append("import org.junit.jupiter.api.Test;\n");
        sb.append("import org.junit.jupiter.api.BeforeEach;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n");
        
        // Add additional imports based on parameter types
        boolean hasListParameter = methods.stream()
                .flatMap(m -> m.getParameters().stream())
                .anyMatch(p -> p.getType().contains("List"));
        
        if (hasListParameter) {
            sb.append("import java.util.List;\n");
            sb.append("import java.util.ArrayList;\n");
            sb.append("import java.util.Arrays;\n");
        }
        
        sb.append("\n");
        
        // Generate test class
        sb.append("/**\n");
        sb.append(" * Test class for ").append(simpleClassName).append(".\n");
        sb.append(" * Generated by Code Analyzer.\n");
        sb.append(" */\n");
        sb.append("class ").append(simpleClassName).append("Test {\n\n");
        
        // Generate instance field and setup method
        sb.append("    private ").append(simpleClassName).append(" instance;\n\n");
        sb.append("    @BeforeEach\n");
        sb.append("    void setUp() {\n");
        sb.append("        instance = new ").append(simpleClassName).append("();\n");
        sb.append("    }\n\n");
        
        // Generate test methods
        for (MethodInfo method : methods) {
            generateTestMethodsForMethod(sb, method);
        }
        
        // Close class
        sb.append("}\n");
        
        return sb.toString();
    }
    
    /**
     * Generates test methods for a single method.
     */
    private void generateTestMethodsForMethod(StringBuilder sb, MethodInfo method) {
        // Generate test cases
        List<TestCase> testCases = testRequestGenerator.generateTestCases(method);
        
        for (int i = 0; i < testCases.size(); i++) {
            TestCase testCase = testCases.get(i);
            
            // Generate test method
            sb.append("    @Test\n");
            
            // Sanitize test method name
            String methodName = method.getMethodName();
            String testDesc = testCase.getDescription()
                    .replaceAll("[^a-zA-Z0-9]", "_")
                    .replaceAll("_+", "_");
            
            sb.append("    void ").append(methodName).append("_").append(testDesc)
                .append("_test").append(i + 1).append("() {\n");
            
            // Generate test method body
            generateTestMethodBody(sb, method, testCase);
            
            sb.append("    }\n\n");
        }
    }
    
    /**
     * Generates the body of a test method.
     */
    private void generateTestMethodBody(StringBuilder sb, MethodInfo method, TestCase testCase) {
        sb.append("        // Arrange\n");
        
        // Generate parameter variable declarations and initializations
        Map<ParameterInfo, Object> paramValues = testCase.getParameterValues();
        for (Map.Entry<ParameterInfo, Object> entry : paramValues.entrySet()) {
            ParameterInfo param = entry.getKey();
            Object value = entry.getValue();
            
            sb.append("        ");
            sb.append(param.getType()).append(" ").append(param.getName());
            sb.append(" = ");
            sb.append(formatLiteralValue(param.getType(), value));
            sb.append(";\n");
        }
        
        sb.append("\n        // Act\n");
        
        // Generate method call
        String returnType = method.getReturnType();
        boolean hasReturnValue = !"void".equals(returnType);
        
        if (hasReturnValue) {
            sb.append("        ").append(returnType).append(" result = ");
        } else {
            sb.append("        ");
        }
        
        sb.append("instance.").append(method.getMethodName()).append("(");
        
        // Add method parameters
        boolean first = true;
        for (ParameterInfo param : method.getParameters()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(param.getName());
            first = false;
        }
        
        sb.append(");\n\n");
        
        // Generate assertions
        sb.append("        // Assert\n");
        
        // Add comment with expected behavior
        sb.append("        // Expected behavior: ").append(testCase.getExpectedBehavior()).append("\n");
        
        // Generate appropriate assertions based on return type
        if (hasReturnValue) {
            if ("boolean".equals(returnType)) {
                // For boolean return types
                sb.append("        // Uncomment and adjust the appropriate assertion:\n");
                sb.append("        // assertTrue(result);\n");
                sb.append("        // assertFalse(result);\n");
            } else if ("int".equals(returnType) || "long".equals(returnType) || 
                       "double".equals(returnType) || "float".equals(returnType)) {
                // For numeric return types
                sb.append("        // Uncomment and adjust the appropriate assertion:\n");
                sb.append("        // assertEquals(expectedValue, result);\n");
                sb.append("        // assertTrue(result > 0);\n");
                sb.append("        // assertFalse(result < 0);\n");
            } else if ("String".equals(returnType)) {
                // For String return type
                sb.append("        // Uncomment and adjust the appropriate assertion:\n");
                sb.append("        // assertNotNull(result);\n");
                sb.append("        // assertEquals(\"expected string\", result);\n");
                sb.append("        // assertTrue(result.contains(\"expected substring\"));\n");
            } else {
                // For object return types
                sb.append("        // Uncomment and adjust the appropriate assertion:\n");
                sb.append("        // assertNotNull(result);\n");
                sb.append("        // assertEquals(expectedObject, result);\n");
            }
        } else {
            // For void methods, we typically verify state changes or that no exceptions were thrown
            sb.append("        // No return value to assert. Verify state changes or exception behavior.\n");
            sb.append("        // Example: verify state changes in the instance or in parameters.\n");
        }
    }
    
    /**
     * Formats a literal value for use in Java code.
     */
    private String formatLiteralValue(String type, Object value) {
        if (value == null) {
            return "null";
        }
        
        if (type.equals("String")) {
            return "\"" + value.toString().replace("\"", "\\\"") + "\"";
        } else if (type.equals("char")) {
            return "'" + value.toString().replace("'", "\\'") + "'";
        } else if (type.equals("boolean")) {
            return value.toString();
        } else if (type.equals("byte") || type.equals("short") || type.equals("int") || 
                  type.equals("long") || type.equals("float") || type.equals("double")) {
            return value.toString();
        } else if (type.contains("List")) {
            // For List types, create a new ArrayList
            return "new ArrayList<>(Arrays.asList(" + value.toString() + "))";
        } else {
            // For other object types, just use null for simplicity
            return "null /* TODO: Initialize this " + type + " properly */";
        }
    }
}