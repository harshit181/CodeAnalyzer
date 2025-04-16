package com.codeanalyzer;

import com.codeanalyzer.cli.CommandLineProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Code Analyzer application.
 * This application can analyze Java source code, identify methods,
 * and generate test request objects for specified methods.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            CommandLineProcessor cliProcessor = new CommandLineProcessor();
            cliProcessor.process(args);
        } catch (Exception e) {
            logger.error("An error occurred during execution", e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
