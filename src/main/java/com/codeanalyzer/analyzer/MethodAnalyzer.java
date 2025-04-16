package com.codeanalyzer.analyzer;

import com.codeanalyzer.model.MethodInfo;
import com.codeanalyzer.model.ParameterInfo;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Analyzes Java methods to extract information about parameters, return types, and decision points.
 */
public class MethodAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(MethodAnalyzer.class);
    
    /**
     * Analyzes a method and extracts information about its parameters, return type, and decision points.
     * 
     * @param methodDeclaration The JavaParser method declaration node.
     * @param className The name of the class containing the method.
     * @return Information about the analyzed method.
     */
    public MethodInfo analyzeMethod(MethodDeclaration methodDeclaration, String className) {
        String methodName = methodDeclaration.getNameAsString();
        String returnType = methodDeclaration.getType().asString();
        
        // Extract parameters
        List<ParameterInfo> parameters = methodDeclaration.getParameters().stream()
                .map(this::createParameterInfo)
                .collect(Collectors.toList());
        
        // Extract conditions that reference parameters
        Set<String> conditionVariables = new HashSet<>();
        if (methodDeclaration.getBody().isPresent()) {
            extractConditionVariables(methodDeclaration.getBody().get(), conditionVariables);
        }
        
        // Create method info object
        MethodInfo methodInfo = new MethodInfo(className, methodName, returnType, parameters);
        
        // Set decision points based on parameters used in conditions
        parameters.forEach(param -> {
            if (conditionVariables.contains(param.getName())) {
                param.setUsedInCondition(true);
            }
        });
        
        logger.debug("Analyzed method: {}", methodInfo.getSignature());
        return methodInfo;
    }
    
    /**
     * Creates a ParameterInfo object from a JavaParser Parameter node.
     */
    private ParameterInfo createParameterInfo(Parameter parameter) {
        String name = parameter.getNameAsString();
        String type = parameter.getType().asString();
        
        return new ParameterInfo(name, type);
    }
    
    /**
     * Extracts variables used in conditions from a statement.
     */
    private void extractConditionVariables(Statement statement, Set<String> conditionVariables) {
        if (statement instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) statement;
            extractVariablesFromExpression(ifStmt.getCondition(), conditionVariables);
            
            if (ifStmt.getThenStmt() != null) {
                extractConditionVariables(ifStmt.getThenStmt(), conditionVariables);
            }
            
            if (ifStmt.getElseStmt().isPresent()) {
                extractConditionVariables(ifStmt.getElseStmt().get(), conditionVariables);
            }
        } else if (statement instanceof WhileStmt) {
            WhileStmt whileStmt = (WhileStmt) statement;
            extractVariablesFromExpression(whileStmt.getCondition(), conditionVariables);
            extractConditionVariables(whileStmt.getBody(), conditionVariables);
        } else if (statement instanceof ForStmt) {
            ForStmt forStmt = (ForStmt) statement;
            forStmt.getCompare().ifPresent(expr -> extractVariablesFromExpression(expr, conditionVariables));
            extractConditionVariables(forStmt.getBody(), conditionVariables);
        } else if (statement instanceof ForEachStmt) {
            ForEachStmt forEachStmt = (ForEachStmt) statement;
            extractConditionVariables(forEachStmt.getBody(), conditionVariables);
        } else if (statement instanceof SwitchStmt) {
            SwitchStmt switchStmt = (SwitchStmt) statement;
            extractVariablesFromExpression(switchStmt.getSelector(), conditionVariables);
            switchStmt.getEntries().forEach(entry -> {
                entry.getStatements().forEach(stmt -> extractConditionVariables(stmt, conditionVariables));
            });
        } else if (statement instanceof BlockStmt) {
            BlockStmt blockStmt = (BlockStmt) statement;
            blockStmt.getStatements().forEach(stmt -> extractConditionVariables(stmt, conditionVariables));
        } else if (statement instanceof TryStmt) {
            TryStmt tryStmt = (TryStmt) statement;
            extractConditionVariables(tryStmt.getTryBlock(), conditionVariables);
            tryStmt.getCatchClauses().forEach(catchClause -> 
                extractConditionVariables(catchClause.getBody(), conditionVariables));
            tryStmt.getFinallyBlock().ifPresent(finallyBlock -> 
                extractConditionVariables(finallyBlock, conditionVariables));
        }
    }
    
    /**
     * Extracts variable names from an expression.
     */
    private void extractVariablesFromExpression(Expression expression, Set<String> variables) {
        if (expression instanceof NameExpr) {
            NameExpr nameExpr = (NameExpr) expression;
            variables.add(nameExpr.getNameAsString());
        } else if (expression instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expression;
            extractVariablesFromExpression(binaryExpr.getLeft(), variables);
            extractVariablesFromExpression(binaryExpr.getRight(), variables);
        }
        
        // Handle other expression types if needed
    }
}
