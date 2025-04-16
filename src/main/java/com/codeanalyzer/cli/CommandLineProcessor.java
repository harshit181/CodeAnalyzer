package com.codeanalyzer.cli;

import com.codeanalyzer.analyzer.CodeAnalyzer;
import com.codeanalyzer.generator.TestRequestGenerator;
import com.codeanalyzer.model.AnalysisResult;
import com.codeanalyzer.model.MethodInfo;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Processes command-line arguments and provides interactive interface to the user.
 */
public class CommandLineProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineProcessor.class);
    private final Options options;
    private final CommandLineParser parser;
    private final HelpFormatter formatter;
    private Scanner scanner;
    
    public CommandLineProcessor() {
        this.options = createOptions();
        this.parser = new DefaultParser();
        this.formatter = new HelpFormatter();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Creates the command-line options for the application.
     */
    private Options createOptions() {
        Options options = new Options();
        
        Option sourcePathOption = Option.builder("s")
                .longOpt("source")
                .desc("Path to the Java source files to analyze")
                .hasArg()
                .argName("SOURCE_PATH")
                .required(true)
                .build();
        
        Option helpOption = Option.builder("h")
                .longOpt("help")
                .desc("Display help information")
                .build();
        
        options.addOption(sourcePathOption);
        options.addOption(helpOption);
        
        return options;
    }
    
    /**
     * Processes the command-line arguments and runs the application.
     */
    public void process(String[] args) {
        try {
            CommandLine cmd = parser.parse(options, args);
            
            if (cmd.hasOption("help")) {
                displayHelp();
                return;
            }
            
            String sourcePath = cmd.getOptionValue("source");
            runAnalysis(sourcePath);
            
        } catch (ParseException e) {
            logger.error("Error parsing command line arguments", e);
            System.err.println("Error: " + e.getMessage());
            displayHelp();
        }
    }
    
    /**
     * Displays help information.
     */
    private void displayHelp() {
        formatter.printHelp("code-analyzer", 
                "Java Code Analyzer - Parse code and generate test request objects", 
                options, 
                "Example: java -jar code-analyzer.jar -s /path/to/source", 
                true);
    }
    
    /**
     * Runs the code analysis and handles user interaction.
     */
    private void runAnalysis(String sourcePath) {
        Path sourceDir = Paths.get(sourcePath);
        File sourceDirFile = sourceDir.toFile();
        
        if (!sourceDirFile.exists() || !sourceDirFile.isDirectory()) {
            System.err.println("Error: Source path does not exist or is not a directory: " + sourcePath);
            return;
        }
        
        System.out.println("Analyzing source code in: " + sourcePath);
        
        CodeAnalyzer analyzer = new CodeAnalyzer();
        AnalysisResult result = analyzer.analyzeDirectory(sourceDir);
        
        if (result.getMethods().isEmpty()) {
            System.out.println("No methods found in the provided source code.");
            return;
        }
        
        System.out.println("\nAnalysis completed. Found " + result.getMethods().size() + " methods:");
        displayMethods(result.getMethods());
        
        boolean running = true;
        while (running) {
            System.out.println("\nEnter the ID of a method to generate test objects (or 'q' to quit): ");
            String input = scanner.nextLine().trim();
            
            if ("q".equalsIgnoreCase(input)) {
                running = false;
            } else {
                try {
                    int methodId = Integer.parseInt(input);
                    if (methodId >= 0 && methodId < result.getMethods().size()) {
                        generateTestObjects(result.getMethods().get(methodId));
                    } else {
                        System.out.println("Invalid method ID. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a method ID or 'q' to quit.");
                }
            }
        }
        
        System.out.println("Exiting. Goodbye!");
    }
    
    /**
     * Displays the list of available methods.
     */
    private void displayMethods(List<MethodInfo> methods) {
        System.out.println("\nID\tMethod Signature");
        System.out.println("---\t----------------");
        
        for (int i = 0; i < methods.size(); i++) {
            MethodInfo method = methods.get(i);
            System.out.println(i + "\t" + method.getSignature());
        }
    }
    
    /**
     * Generates test objects for the selected method.
     */
    private void generateTestObjects(MethodInfo method) {
        System.out.println("\nGenerating test objects for method: " + method.getSignature());
        
        TestRequestGenerator generator = new TestRequestGenerator();
        generator.generateTestCases(method).forEach(testCase -> {
            System.out.println("\nTest Case: " + testCase.getDescription());
            System.out.println("Parameters:");
            testCase.getParameterValues().forEach((param, value) -> {
                System.out.println("  " + param.getName() + " (" + param.getType() + "): " + value);
            });
            System.out.println("Expected behavior: " + testCase.getExpectedBehavior());
        });
    }
}
