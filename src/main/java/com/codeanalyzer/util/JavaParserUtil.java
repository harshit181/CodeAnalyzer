package com.codeanalyzer.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for working with JavaParser.
 */
public class JavaParserUtil {
    private static final Logger logger = LoggerFactory.getLogger(JavaParserUtil.class);
    private static JavaParser javaParser;
    
    static {
        // Configure JavaParser with a symbol solver
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        javaParser = new JavaParser();
        javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);
    }
    
    /**
     * Parses a Java file and returns the CompilationUnit.
     * 
     * @param filePath The path to the Java file to parse.
     * @return The CompilationUnit representing the parsed Java file.
     * @throws IOException If the file cannot be read.
     */
    public static CompilationUnit parseFile(Path filePath) throws IOException {
        logger.debug("Parsing file: {}", filePath);
        
        byte[] bytes = Files.readAllBytes(filePath);
        String content = new String(bytes);
        
        ParseResult<CompilationUnit> result = javaParser.parse(content);
        
        if (!result.isSuccessful()) {
            throw new IOException("Failed to parse file: " + filePath + 
                    ". Error: " + result.getProblems());
        }
        
        return result.getResult().orElseThrow(() -> 
                new IOException("Failed to parse file: " + filePath));
    }
    
    /**
     * Parses a Java expression and returns the Expression object.
     * 
     * @param expressionStr The expression string to parse.
     * @return The Expression object representing the parsed expression.
     */
    public static Expression parseExpression(String expressionStr) {
        return javaParser.parseExpression(expressionStr)
                .getResult()
                .orElseThrow(() -> new IllegalArgumentException("Invalid expression: " + expressionStr));
    }
}
