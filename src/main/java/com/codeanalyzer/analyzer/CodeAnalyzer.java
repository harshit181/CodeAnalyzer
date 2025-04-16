package com.codeanalyzer.analyzer;

import com.codeanalyzer.model.AnalysisResult;
import com.codeanalyzer.model.MethodInfo;
import com.codeanalyzer.util.JavaParserUtil;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Analyzes Java source code and extracts information about the methods.
 */
public class CodeAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(CodeAnalyzer.class);
    
    /**
     * Analyzes all Java files in the specified directory.
     * 
     * @param sourceDir The source directory containing Java files.
     * @return The analysis result containing information about all found methods.
     */
    public AnalysisResult analyzeDirectory(Path sourceDir) {
        List<MethodInfo> methods = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(sourceDir)) {
            List<Path> javaFiles = paths
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());
            
            logger.info("Found {} Java files to analyze", javaFiles.size());
            
            for (Path javaFile : javaFiles) {
                try {
                    methods.addAll(analyzeFile(javaFile));
                } catch (Exception e) {
                    logger.error("Error analyzing file: {}", javaFile, e);
                }
            }
        } catch (IOException e) {
            logger.error("Error walking through directory: {}", sourceDir, e);
        }
        
        return new AnalysisResult(methods);
    }
    
    /**
     * Analyzes a single Java file and extracts method information.
     * 
     * @param filePath The path to the Java file.
     * @return A list of information about methods found in the file.
     */
    public List<MethodInfo> analyzeFile(Path filePath) {
        List<MethodInfo> methods = new ArrayList<>();
        
        try {
            CompilationUnit cu = JavaParserUtil.parseFile(filePath);
            
            // Get package name
            String packageName = cu.getPackageDeclaration()
                    .map(pd -> pd.getName().asString())
                    .orElse("");
            
            // Process all classes in the file
            for (TypeDeclaration<?> type : cu.getTypes()) {
                String className = type.getNameAsString();
                String fullClassName = packageName.isEmpty() ? className : packageName + "." + className;
                
                // Process methods in the class
                for (MethodDeclaration method : type.getMethods()) {
                    MethodAnalyzer methodAnalyzer = new MethodAnalyzer();
                    MethodInfo methodInfo = methodAnalyzer.analyzeMethod(method, fullClassName);
                    methods.add(methodInfo);
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing file: {}", filePath, e);
        }
        
        return methods;
    }
}
