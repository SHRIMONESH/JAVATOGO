package util;

/**
 * Centralized error reporting for the transpiler.
 * Provides consistent error messages across all compiler phases.
 */
public class ErrorReporter {
    private int errorCount = 0;
    private int warningCount = 0;

    public void error(SourcePosition position, String message) {
        System.err.println("Error at " + position + ": " + message);
        errorCount++;
    }

    public void error(String message) {
        System.err.println("Error: " + message);
        errorCount++;
    }

    public void warning(SourcePosition position, String message) {
        System.err.println("Warning at " + position + ": " + message);
        warningCount++;
    }

    public void warning(String message) {
        System.err.println("Warning: " + message);
        warningCount++;
    }

    public boolean hasErrors() {
        return errorCount > 0;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void reset() {
        errorCount = 0;
        warningCount = 0;
    }

    public void printSummary() {
        if (errorCount > 0 || warningCount > 0) {
            System.err.println("\nCompilation finished with " +
                             errorCount + " error(s) and " +
                             warningCount + " warning(s)");
        }
    }
}
