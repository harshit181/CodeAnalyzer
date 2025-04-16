package com.codeanalyzer.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JavaParserUtilTest {

    @Test
    void parseFile_withValidJavaFile_shouldReturnCompilationUnit(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path javaFile = createSampleJavaFile(tempDir);

        // Act
        CompilationUnit cu = JavaParserUtil.parseFile(javaFile);

        // Assert
        assertNotNull(cu);
        assertTrue(cu.getTypes().size() > 0);
        assertEquals("SimpleClass", cu.getType(0).getNameAsString());
    }

    @Test
    void parseFile_withInvalidJavaFile_shouldThrowException(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path invalidFile = tempDir.resolve("Invalid.java");
        Files.writeString(invalidFile, "This is not valid Java code");

        // Act & Assert
        assertThrows(Exception.class, () -> JavaParserUtil.parseFile(invalidFile));
    }

    @Test
    void parseFile_withNonExistentFile_shouldThrowException(@TempDir Path tempDir) {
        // Arrange
        Path nonExistentFile = tempDir.resolve("NonExistent.java");

        // Act & Assert
        assertThrows(Exception.class, () -> JavaParserUtil.parseFile(nonExistentFile));
    }
    
    @Test
    void parseExpression_withValidBinaryExpression_shouldReturnExpression() {
        // Arrange
        String expressionStr = "a + b";
        
        // Act
        Expression expr = JavaParserUtil.parseExpression(expressionStr);
        
        // Assert
        assertNotNull(expr);
        assertTrue(expr instanceof BinaryExpr);
        BinaryExpr binaryExpr = (BinaryExpr) expr;
        assertEquals(BinaryExpr.Operator.PLUS, binaryExpr.getOperator());
    }
    
    @Test
    void parseExpression_withValidStringLiteral_shouldReturnExpression() {
        // Arrange
        String expressionStr = "\"Hello, world!\"";
        
        // Act
        Expression expr = JavaParserUtil.parseExpression(expressionStr);
        
        // Assert
        assertNotNull(expr);
        assertTrue(expr instanceof StringLiteralExpr);
        StringLiteralExpr stringExpr = (StringLiteralExpr) expr;
        assertEquals("Hello, world!", stringExpr.getValue());
    }
    
    @Test
    void parseExpression_withValidVariableReference_shouldReturnExpression() {
        // Arrange
        String expressionStr = "someVariable";
        
        // Act
        Expression expr = JavaParserUtil.parseExpression(expressionStr);
        
        // Assert
        assertNotNull(expr);
        assertTrue(expr instanceof NameExpr);
        NameExpr nameExpr = (NameExpr) expr;
        assertEquals("someVariable", nameExpr.getNameAsString());
    }
    
    @Test
    void parseExpression_withInvalidExpression_shouldThrowException() {
        // Arrange
        String invalidExpr = "@#$ invalid";
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> JavaParserUtil.parseExpression(invalidExpr));
    }

    private Path createSampleJavaFile(Path directory) throws IOException {
        Path file = directory.resolve("SimpleClass.java");
        String content = 
            "public class SimpleClass {\n" +
            "    public void simpleMethod() {\n" +
            "        System.out.println(\"Hello, world!\");\n" +
            "    }\n" +
            "}\n";
        Files.writeString(file, content);
        return file;
    }
}